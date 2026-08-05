package com.cmsc.observer;

/**
 * Defines the subject operations required by the Observer pattern.
 *
 * @author Saleh Alhajj
 */
public interface EquipmentSubject {

    /**
     * Registers an observer.
     *
     * @param observer observer to register
     */
    void attach(Observer observer);

    /**
     * Removes a registered observer.
     *
     * @param observer observer to remove
     */
    void detach(Observer observer);

    /**
     * Notifies all registered observers about an equipment change.
     */
    void notifyObservers();
}