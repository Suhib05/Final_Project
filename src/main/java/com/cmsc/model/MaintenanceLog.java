package com.cmsc.model;

import java.time.LocalDateTime;

/**
 * Represents maintenance performed on one piece of equipment.
 *
 * @author Saleh Alhajj
 */
public class MaintenanceLog {

    private int logId;
    private int equipmentId;
    private int shopTechId;
    private LocalDateTime dateLogged;
    private String description;
    private double hoursAtService;

    /**
     * Creates a maintenance log using the current date and time.
     */
    public MaintenanceLog() {
        this.dateLogged = LocalDateTime.now();
    }

    /**
     * Creates a maintenance log with complete information.
     *
     * @param logId database identifier
     * @param equipmentId serviced equipment identifier
     * @param shopTechId Shop-Tech who performed the work
     * @param dateLogged date and time maintenance was recorded
     * @param description description of the maintenance
     * @param hoursAtService equipment usage hours at service time
     */
    public MaintenanceLog(int logId, int equipmentId, int shopTechId,
            LocalDateTime dateLogged, String description,
            double hoursAtService) {

        this.logId = logId;
        this.equipmentId = equipmentId;
        this.shopTechId = shopTechId;
        this.dateLogged = dateLogged;
        this.description = description;
        this.hoursAtService = hoursAtService;
    }

    /** @return maintenance log identifier */
    public int getLogId() {
        return logId;
    }

    /** @param logId maintenance log identifier */
    public void setLogId(int logId) {
        this.logId = logId;
    }

    /** @return serviced equipment identifier */
    public int getEquipmentId() {
        return equipmentId;
    }

    /** @param equipmentId serviced equipment identifier */
    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    /** @return Shop-Tech identifier */
    public int getShopTechId() {
        return shopTechId;
    }

    /** @param shopTechId Shop-Tech identifier */
    public void setShopTechId(int shopTechId) {
        this.shopTechId = shopTechId;
    }

    /** @return date and time maintenance was logged */
    public LocalDateTime getDateLogged() {
        return dateLogged;
    }

    /** @param dateLogged date and time maintenance was logged */
    public void setDateLogged(LocalDateTime dateLogged) {
        this.dateLogged = dateLogged;
    }

    /** @return maintenance description */
    public String getDescription() {
        return description;
    }

    /** @param description maintenance description */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return equipment usage hours at service time */
    public double getHoursAtService() {
        return hoursAtService;
    }

    /** @param hoursAtService equipment usage hours at service time */
    public void setHoursAtService(double hoursAtService) {
        this.hoursAtService = hoursAtService;
    }

    /**
     * Returns a readable description of the maintenance log.
     *
     * @return maintenance log description
     */
    @Override
    public String toString() {
        return "MaintenanceLog{"
                + "logId=" + logId
                + ", equipmentId=" + equipmentId
                + ", shopTechId=" + shopTechId
                + ", dateLogged=" + dateLogged
                + ", description='" + description + '\''
                + ", hoursAtService=" + hoursAtService
                + '}';
    }
}