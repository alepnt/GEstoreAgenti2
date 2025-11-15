package com.example.server.service;

import com.example.server.domain.Notification;
import com.example.server.domain.NotificationSubscription;
import com.example.server.domain.Team;
import com.example.server.domain.User;
import com.example.server.dto.NotificationCreateRequest;
import com.example.server.dto.NotificationResponse;
import com.example.server.dto.NotificationSubscribeRequest;
import com.example.server.dto.NotificationSubscriptionResponse;
import com.example.server.repository.NotificationRepository;
import com.example.server.repository.NotificationSubscriptionRepository;
import com.example.server.repository.TeamRepository;
import com.example.server.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationSubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final NotificationPublisher publisher;
    private final Clock clock;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationSubscriptionRepository subscriptionRepository,
                               UserRepository userRepository,
                               TeamRepository teamRepository,
                               NotificationPublisher publisher,
                               Clock clock) {
        this.notificationRepository = notificationRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.publisher = publisher;
        this.clock = clock;
    }

    public NotificationSubscriptionResponse subscribe(NotificationSubscribeRequest request) {
        NotificationSubscribeRequest requiredRequest = Objects.requireNonNull(request, "request must not be null");
        User user = requireUser(requiredRequest.userId());
        NotificationSubscription subscription = Objects.requireNonNull(NotificationSubscription
                .create(user.getId(), requiredRequest.channel(), Instant.now(clock)),
                "subscription must not be null");
        NotificationSubscription saved = subscriptionRepository.save(subscription);
        return new NotificationSubscriptionResponse(saved.getId(), saved.getUserId(), saved.getChannel(), saved.getCreatedAt());
    }

    public List<NotificationResponse> findNotifications(Long userId, Instant since) {
        User user = requireUser(userId);
        Stream<Notification> userNotifications = Optional.ofNullable(since)
                .map(instant -> notificationRepository.findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(user.getId(), instant).stream())
                .orElseGet(() -> notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream());

        Stream<Notification> teamNotifications = Optional.ofNullable(user.getTeamId())
                .map(teamId -> Optional.ofNullable(since)
                        .map(instant -> notificationRepository.findByTeamIdAndCreatedAtAfterOrderByCreatedAtDesc(teamId, instant).stream())
                        .orElseGet(() -> notificationRepository.findByTeamIdOrderByCreatedAtDesc(teamId).stream()))
                .orElse(Stream.empty());

        return Stream.concat(userNotifications, teamNotifications)
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .map(this::toResponse)
                .toList();
    }

    public NotificationResponse createNotification(NotificationCreateRequest request) {
        NotificationCreateRequest requiredRequest = Objects.requireNonNull(request, "request must not be null");
        Assert.isTrue(requiredRequest.userId() != null || requiredRequest.teamId() != null,
                "È necessario specificare un destinatario per la notifica");
        Assert.isTrue(!(requiredRequest.userId() != null && requiredRequest.teamId() != null),
                "Una notifica può essere destinata a un utente o a un team, non a entrambi");

        Notification notification;
        if (requiredRequest.userId() != null) {
            User user = requireUser(requiredRequest.userId());
            notification = Objects.requireNonNull(Notification.forUser(user.getId(), requiredRequest.title(),
                    requiredRequest.message(), Instant.now(clock)), "notification must not be null");
        } else {
            Team team = teamRepository.findById(requiredRequest.teamId())
                    .orElseThrow(() -> new IllegalArgumentException("Team non trovato: " + requiredRequest.teamId()));
            notification = Objects.requireNonNull(Notification.forTeam(team.getId(), requiredRequest.title(),
                    requiredRequest.message(), Instant.now(clock)), "notification must not be null");
        }

        Notification saved = notificationRepository.save(notification);
        publisher.publish(saved);
        return toResponse(saved);
    }

    public void registerSubscriber(Long userId, DeferredResult<List<NotificationResponse>> deferredResult) {
        DeferredResult<List<NotificationResponse>> requiredDeferredResult = Objects.requireNonNull(deferredResult,
                "deferredResult must not be null");
        User user = requireUser(userId);
        List<NotificationPublisher.Subscription> subscriptions = new ArrayList<>();

        var listener = new java.util.concurrent.atomic.AtomicBoolean(true);
        java.util.function.Consumer<Notification> consumer = notification -> {
            if (listener.getAndSet(false)) {
                NotificationResponse response = toResponse(Objects.requireNonNull(notification, "notification must not be null"));
                requiredDeferredResult.setResult(List.of(response));
            }
        };

        subscriptions.add(publisher.subscribeToUser(user.getId(), consumer));
        if (user.getTeamId() != null) {
            subscriptions.add(publisher.subscribeToTeam(user.getTeamId(), consumer));
        }

        Runnable cancelAction = () -> subscriptions.forEach(NotificationPublisher.Subscription::cancel);
        requiredDeferredResult.onCompletion(cancelAction);
        requiredDeferredResult.onTimeout(() -> {
            List<NotificationResponse> emptyResult = List.of();
            requiredDeferredResult.setResult(emptyResult);
            cancelAction.run();
        });
    }

    private User requireUser(Long userId) {
        Long requiredUserId = Objects.requireNonNull(userId, "userId must not be null");
        return userRepository.findById(requiredUserId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + requiredUserId));
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(notification.getId(),
                notification.getUserId(),
                notification.getTeamId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt());
    }
}
