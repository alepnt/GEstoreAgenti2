package com.example.server.service;

import com.example.common.dto.NotificationSubscriptionDTO;
import com.example.server.domain.NotificationSubscription;
import com.example.server.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class NotificationSubscriptionServiceTest {

    private FakeNotificationSubscriptionRepository subscriptionRepository;
    private FakeUserRepository userRepository;
    private Clock clock;
    private NotificationSubscriptionService service;

    @BeforeEach
    void setUp() {
        subscriptionRepository = new FakeNotificationSubscriptionRepository();
        userRepository = new FakeUserRepository();
        clock = Clock.fixed(Instant.parse("2024-03-02T10:15:30Z"), ZoneOffset.UTC);
        service = InstantiationService.notificationSubscriptionService(subscriptionRepository, userRepository, clock);

        userRepository.save(new User(1L, "az-1", "one@example.com", "Mario Rossi", null, 10L, 20L, true, LocalDateTime.now()));
        userRepository.save(new User(2L, "az-2", "two@example.com", "Luisa Bianchi", null, 11L, 21L, true, LocalDateTime.now()));
    }

    @Test
    void createNormalizesChannelAndAssignsTimestamp() {
        NotificationSubscriptionDTO dto = new NotificationSubscriptionDTO(null, 1L, "  EMAIL  ", null);

        NotificationSubscriptionDTO saved = service.create(dto);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getChannel()).isEqualTo("EMAIL");
        assertThat(saved.getCreatedAt()).isEqualTo(Instant.parse("2024-03-02T10:15:30Z"));
    }

    @Test
    void createRejectsMissingData() {
        NotificationSubscriptionDTO missingUser = new NotificationSubscriptionDTO(null, null, "SMS", null);
        NotificationSubscriptionDTO missingChannel = new NotificationSubscriptionDTO(null, 1L, " ", null);

        assertThatThrownBy(() -> service.create(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("subscription must not be null");
        assertThatThrownBy(() -> service.create(missingUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("L'utente è obbligatorio");
        assertThatThrownBy(() -> service.create(missingChannel))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Il canale è obbligatorio");
    }

    @Test
    void listOrdersByCreationAndNormalizesChannel() {
        NotificationSubscription first = subscriptionRepository.save(NotificationSubscription.create(1L, " EMAIL", Instant.parse("2024-03-01T00:00:00Z")));
        NotificationSubscription second = subscriptionRepository.save(NotificationSubscription.create(2L, "PUSH", Instant.parse("2024-03-05T00:00:00Z")));

        assertThat(service.list(null)).extracting(NotificationSubscriptionDTO::getId, NotificationSubscriptionDTO::getChannel)
                .containsExactly(
                        tuple(first.getId(), "EMAIL"),
                        tuple(second.getId(), "PUSH")
                );

        assertThat(service.list(2L)).extracting(NotificationSubscriptionDTO::getId)
                .containsExactly(second.getId());
    }

    @Test
    void updatePreservesCreatedAtWhenMissingAndValidatesUser() {
        NotificationSubscription saved = subscriptionRepository.save(NotificationSubscription.create(1L, "SMS", Instant.parse("2024-03-01T00:00:00Z")));
        NotificationSubscriptionDTO toUpdate = new NotificationSubscriptionDTO(saved.getId(), 1L, "  sms  ", null);

        Optional<NotificationSubscriptionDTO> updated = service.update(saved.getId(), toUpdate);

        assertThat(updated).isPresent();
        assertThat(updated.get().getChannel()).isEqualTo("sms");
        assertThat(updated.get().getCreatedAt()).isEqualTo(saved.getCreatedAt());

        assertThatThrownBy(() -> service.update(null, toUpdate))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("id must not be null");
        assertThatThrownBy(() -> service.update(saved.getId(), new NotificationSubscriptionDTO(saved.getId(), 99L, "sms", null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Utente non trovato: 99");
    }

    @Test
    void deleteReturnsTrueOnlyWhenEntityExists() {
        NotificationSubscription saved = subscriptionRepository.save(NotificationSubscription.create(1L, "SMS", Instant.now(clock)));

        assertThat(service.delete(saved.getId())).isTrue();
        assertThat(subscriptionRepository.count()).isZero();
        assertThat(service.delete(999L)).isFalse();
    }
}
