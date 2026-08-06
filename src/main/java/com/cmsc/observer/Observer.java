package com.cmsc.observer;

import com.cmsc.model.Equipment;

/**
 * Defines the update operation for classes that observe equipment changes.
 *
 * @author Saleh Alhajj
 */
public interface Observer {

    /**
     * Responds when the observed equipment changes.
     *
     * @param equipment equipment whose state changed
     */
    void update(Equipment equipment);
}