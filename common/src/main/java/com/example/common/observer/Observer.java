package com.example.common.observer;

/**
 * Osservatore generico per notifiche di dominio condivise.
 */
@FunctionalInterface
public interface Observer<T> {

    void update(T event);
}
