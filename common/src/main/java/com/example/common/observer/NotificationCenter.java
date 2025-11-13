package com.example.common.observer;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Implementazione thread-safe del pattern Observer per chat/notifiche.
 */
public class NotificationCenter<T> implements Subject<T> {

    private final Set<Observer<T>> observers = new CopyOnWriteArraySet<>();

    @Override
    public void registerObserver(Observer<T> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<T> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(T event) {
        observers.forEach(observer -> observer.update(event));
    }

    @Override
    public Collection<Observer<T>> getObservers() {
        return Collections.unmodifiableSet(observers);
    }
}
