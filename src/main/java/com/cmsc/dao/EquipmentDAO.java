package com.cmsc.dao;

import com.cmsc.model.Equipment;
import java.util.List;

/**
 * Defines database operations for CMSC equipment.
 *
 * @author Saleh Alhajj
 */
public interface EquipmentDAO extends GenericDAO<Equipment> {

    /**
     * Finds equipment using its unique asset tag.
     *
     * @param assetTag unique equipment asset tag
     * @return matching equipment, or null when it does not exist
     */
    Equipment findByAssetTag(String assetTag);

    /**
     * Finds all equipment with a particular status.
     *
     * @param status equipment status
     * @return equipment matching the requested status
     */
    List<Equipment> findByStatus(String status);

    /**
     * Finds equipment that has reached its maintenance threshold.
     *
     * @return equipment requiring maintenance
     */
    List<Equipment> findRequiringMaintenance();
}