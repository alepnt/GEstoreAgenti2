package com.example.server.service;

import com.example.server.domain.User;
import com.example.server.repository.UserRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Repository utente in-memory per test. Permette di simulare le operazioni di
 * persistenza senza accedere a un database reale.
 */
class FakeUserRepository implements UserRepository {

    private final AtomicLong sequence = new AtomicLong(1);
    private final Map<Long, User> storage = new HashMap<>();

    @Override
    public List<User> findAllByOrderByDisplayNameAsc() {
        return storage.values().stream()
                .sorted((a, b) -> a.getDisplayName().compareToIgnoreCase(b.getDisplayName()))
                .toList();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return storage.values().stream()
                .filter(user -> email != null && email.equals(user.getEmail()))
                .findFirst();
    }

    @Override
    public Optional<User> findByAzureId(String azureId) {
        return storage.values().stream()
                .filter(user -> azureId != null && azureId.equals(user.getAzureId()))
                .findFirst();
    }

    @Override
    public List<User> findByTeamId(Long teamId) {
        return storage.values().stream()
                .filter(user -> teamId != null && teamId.equals(user.getTeamId()))
                .toList();
    }

    @Override
    public <S extends User> S save(S entity) {
        Long id = entity.getId();
        if (id == null) {
            id = sequence.getAndIncrement();
            entity = (S) new User(id, entity.getAzureId(), entity.getEmail(), entity.getDisplayName(),
                    entity.getPasswordHash(), entity.getRoleId(), entity.getTeamId(), entity.getActive(), entity.getCreatedAt());
        }
        storage.put(id, entity);
        return entity;
    }

    @Override
    public <S extends User> Iterable<S> saveAll(Iterable<S> entities) {
        List<S> saved = new ArrayList<>();
        for (S entity : entities) {
            saved.add(save(entity));
        }
        return saved;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }

    @Override
    public Iterable<User> findAll() {
        return storage.values();
    }

    @Override
    public Iterable<User> findAllById(Iterable<Long> ids) {
        List<User> result = new ArrayList<>();
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
    public void delete(User entity) {
        if (entity != null) {
            storage.remove(entity.getId());
        }
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        ids.forEach(storage::remove);
    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {
        entities.forEach(entity -> storage.remove(entity.getId()));
    }

    @Override
    public void deleteAll() {
        storage.clear();
    }
}
