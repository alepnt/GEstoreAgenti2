package com.example.server.service;

import com.example.server.domain.NotificationSubscription;
import com.example.server.repository.NotificationSubscriptionRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Implementazione in-memory di {@link NotificationSubscriptionRepository} per
 * scenari di test. Usa una semplice {@link Map} per persistere le entità
 * mantenendo la semantica prevista dal {@link CrudRepository}.
 */
class FakeNotificationSubscriptionRepository implements NotificationSubscriptionRepository {

    private final AtomicLong sequence = new AtomicLong(1);
    private final Map<Long, NotificationSubscription> storage = new HashMap<>();

    @Override
    public List<NotificationSubscription> findByUserId(Long userId) {
        return storage.values().stream()
                .filter(subscription -> subscription.getUserId().equals(userId))
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public <S extends NotificationSubscription> S save(S entity) {
        Long id = entity.getId();
        if (id == null) {
            id = sequence.getAndIncrement();
            entity = (S) entity.withId(id);
        }
        storage.put(id, entity);
        return entity;
    }

    @Override
    public <S extends NotificationSubscription> Iterable<S> saveAll(Iterable<S> entities) {
        List<S> result = new ArrayList<>();
        for (S entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    @Override
    public Optional<NotificationSubscription> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }

    @Override
    public Iterable<NotificationSubscription> findAll() {
        return storage.values();
    }

    @Override
    public Iterable<NotificationSubscription> findAllById(Iterable<Long> ids) {
        List<NotificationSubscription> result = new ArrayList<>();
        ids.forEach(id -> findById(id).ifPresent(result::add));
        return result;
    }

    @Override
    public long count() {
        return storage.size();
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public void delete(NotificationSubscription entity) {
        if (entity != null) {
            storage.remove(entity.getId());
        }
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        ids.forEach(storage::remove);
    }

    @Override
    public void deleteAll(Iterable<? extends NotificationSubscription> entities) {
        entities.forEach(entity -> storage.remove(entity.getId()));
    }

    @Override
    public void deleteAll() {
        storage.clear();
    }
}
