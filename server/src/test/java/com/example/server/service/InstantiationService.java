package com.example.server.service;

import com.example.server.repository.NotificationSubscriptionRepository;
import com.example.server.repository.UserRepository;

import java.time.Clock;

/**
 * Utility di supporto ai test per creare le istanze dei servizi del package
 * {@code com.example.server.service} senza passare dai costruttori reali nei
 * test. Centralizza la configurazione in modo da poter sostituire le
 * dipendenze con mock o implementazioni finte.
 */
public final class InstantiationService {

    private InstantiationService() {
        // utility
    }

    public static StatisticsService statisticsService(StatisticsRepository statisticsRepository,
                                                      CommissionService commissionService) {
        return new StatisticsService(statisticsRepository, commissionService);
    }

    public static NotificationSubscriptionService notificationSubscriptionService(
            NotificationSubscriptionRepository subscriptionRepository,
            UserRepository userRepository,
            Clock clock) {
        return new NotificationSubscriptionService(subscriptionRepository, userRepository, clock);
    }
}
