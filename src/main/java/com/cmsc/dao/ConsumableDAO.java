package com.cmsc.dao;

import com.cmsc.model.Consumable;
import java.util.List;

/**
 * Defines database operations for CMSC consumable inventory.
 *
 * @author Saleh Alhajj
 */
public interface ConsumableDAO extends GenericDAO<Consumable> {

    /**
     * Finds a consumable using its material type.
     *
     * @param type consumable type
     * @return matching consumable, or null when none exists
     */
    Consumable findByType(String type);

    /**
     * Finds consumables whose stock is at or below the reorder threshold.
     *
     * @return low-stock consumables
     */
    List<Consumable> findLowStock();
}