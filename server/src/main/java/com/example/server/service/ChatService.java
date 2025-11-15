package com.example.server.service;

import com.example.server.domain.Message;
import com.example.server.domain.User;
import com.example.server.dto.ChatConversationSummary;
import com.example.server.dto.ChatMessageRequest;
import com.example.server.dto.ChatMessageResponse;
import com.example.server.repository.MessageRepository;
import com.example.server.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ChatService {

    private static final String TEAM_PREFIX = "team:";

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatPublisher chatPublisher;
    private final Clock clock;

    public ChatService(MessageRepository messageRepository,
                       UserRepository userRepository,
                       ChatPublisher chatPublisher,
                       Clock clock) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatPublisher = chatPublisher;
        this.clock = clock;
    }

    public List<ChatConversationSummary> listConversations(Long userId) {
        User user = requireUser(userId);
        Map<String, List<Message>> grouped = StreamSupport.stream(messageRepository.findAll().spliterator(), false)
                .filter(message -> canAccessConversation(user, message.getConversationId()))
                .collect(Collectors.groupingBy(Message::getConversationId));

        return grouped.entrySet().stream()
                .map(entry -> {
                    List<Message> messages = entry.getValue();
                    messages.sort(Comparator.comparing(Message::getCreatedAt).reversed());
                    Message latest = messages.getFirst();
                    String title = buildConversationTitle(entry.getKey(), user);
                    return new ChatConversationSummary(entry.getKey(),
                            title,
                            latest.getCreatedAt(),
                            truncate(latest.getBody()));
                })
                .sorted(Comparator.comparing(ChatConversationSummary::lastActivity).reversed())
                .toList();
    }

    public List<ChatMessageResponse> getMessages(Long userId, String conversationId, Instant since) {
        User user = requireUser(userId);
        String requiredConversationId = Objects.requireNonNull(conversationId, "conversationId must not be null");
        assertCanAccess(user, requiredConversationId);

        List<Message> messages = Optional.ofNullable(since)
                .map(instant -> messageRepository
                        .findByConversationIdAndCreatedAtAfterOrderByCreatedAtAsc(requiredConversationId, instant))
                .orElseGet(() -> messageRepository.findByConversationIdOrderByCreatedAtAsc(requiredConversationId));

        return messages.stream().map(this::toResponse).toList();
    }

    public ChatMessageResponse sendMessage(ChatMessageRequest request) {
        ChatMessageRequest requiredRequest = Objects.requireNonNull(request, "request must not be null");
        User sender = requireUser(requiredRequest.senderId());
        String conversationId = Objects.requireNonNull(requiredRequest.conversationId(),
                "conversationId must not be null");
        assertCanAccess(sender, conversationId);
        Message message = Objects.requireNonNull(Message.create(conversationId, sender.getId(), sender.getTeamId(),
                requiredRequest.body(), Instant.now(clock)), "message must not be null");
        Message saved = messageRepository.save(message);
        ChatMessageResponse response = toResponse(saved);
        chatPublisher.publish(response);
        return response;
    }

    public void registerConversationListener(Long userId,
                                             String conversationId,
                                             DeferredResult<List<ChatMessageResponse>> deferredResult) {
        User user = requireUser(userId);
        String requiredConversationId = Objects.requireNonNull(conversationId, "conversationId must not be null");
        DeferredResult<List<ChatMessageResponse>> requiredDeferredResult = Objects.requireNonNull(deferredResult,
                "deferredResult must not be null");
        assertCanAccess(user, requiredConversationId);

        AtomicBoolean pending = new AtomicBoolean(true);
        List<ChatPublisher.Subscription> subscriptions = new ArrayList<>();

        subscriptions.add(chatPublisher.subscribe(requiredConversationId, message -> {
            if (pending.getAndSet(false)) {
                ChatMessageResponse nonNullMessage = Objects.requireNonNull(message, "message must not be null");
                requiredDeferredResult.setResult(List.of(nonNullMessage));
            }
        }));

        Runnable cancel = () -> subscriptions.forEach(ChatPublisher.Subscription::cancel);
        requiredDeferredResult.onCompletion(cancel);
        requiredDeferredResult.onTimeout(() -> {
            List<ChatMessageResponse> emptyResult = List.of();
            requiredDeferredResult.setResult(emptyResult);
            cancel.run();
        });
    }

    private User requireUser(Long id) {
        Long requiredId = Objects.requireNonNull(id, "id must not be null");
        return userRepository.findById(requiredId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + requiredId));
    }

    private void assertCanAccess(User user, String conversationId) {
        if (!canAccessConversation(user, conversationId)) {
            throw new IllegalArgumentException("Accesso alla conversazione negato");
        }
    }

    private boolean canAccessConversation(User user, String conversationId) {
        if (conversationId.startsWith(TEAM_PREFIX)) {
            long teamId = Long.parseLong(conversationId, TEAM_PREFIX.length(), conversationId.length(), 10);
            Long userTeamId = user.getTeamId();
            return userTeamId != null && userTeamId.longValue() == teamId;
        }
        return true;
    }

    private String buildConversationTitle(String conversationId, User user) {
        if (conversationId.startsWith(TEAM_PREFIX)) {
            return "Team " + Optional.ofNullable(user.getTeamId()).map(Object::toString).orElse("sconosciuto");
        }
        return conversationId;
    }

    private String truncate(String body) {
        if (body == null) {
            return "";
        }
        return body.length() > 60 ? body.substring(0, 57) + "..." : body;
    }

    private ChatMessageResponse toResponse(Message message) {
        return new ChatMessageResponse(message.getId(),
                message.getConversationId(),
                message.getSenderId(),
                message.getTeamId(),
                message.getBody(),
                message.getCreatedAt());
    }
}
