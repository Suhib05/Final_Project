package com.cmsc.dao;

import com.cmsc.model.MaintenanceLog;
import java.util.List;

/**
 * Defines database operations for equipment maintenance history.
 *
 * @author Saleh Alhajj
 */
public interface MaintenanceDAO extends GenericDAO<MaintenanceLog> {

    /**
     * Finds maintenance records for one piece of equipment.
     *
     * @param equipmentId equipment identifier
     * @return maintenance history for the equipment
     */
    List<MaintenanceLog> findByEquipmentId(int equipmentId);

    /**
     * Finds maintenance records completed by one Shop-Tech.
     *
     * @param shopTechId Shop-Tech identifier
     * @return maintenance records completed by the Shop-Tech
     */
    List<MaintenanceLog> findByShopTechId(int shopTechId);

    /**
     * Finds the most recent maintenance record for equipment.
     *
     * @param equipmentId equipment identifier
     * @return latest maintenance record, or null when none exists
     */
    MaintenanceLog findLatestByEquipment(int equipmentId);
}