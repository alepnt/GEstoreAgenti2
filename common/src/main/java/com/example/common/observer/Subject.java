package com.example.common.observer;

import java.util.Collection;

/**
 * Interfaccia per i soggetti osservabili.
 */
public interface Subject<T> {

    void registerObserver(Observer<T> observer);

    void removeObserver(Observer<T> observer);

    void notifyObservers(T event);

    Collection<Observer<T>> getObservers();
}
