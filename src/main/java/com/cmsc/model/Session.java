package com.cmsc.model;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Represents one equipment booking and usage session.
 * It records the user, equipment, consumable, time, and material usage.
 *
 * @author Saleh Alhajj
 */
public class Session {

    private int sessionId;
    private int userId;
    private int equipmentId;
    private Integer consumableId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double materialsConsumed;

    /**
     * Creates an empty session.
     */
    public Session() {
    }

    /**
     * Creates a session with complete information.
     *
     * @param sessionId database identifier
     * @param userId user who booked the equipment
     * @param equipmentId equipment being used
     * @param consumableId consumable used, or null when none is used
     * @param startTime session start time
     * @param endTime session end time, or null when active
     * @param materialsConsumed quantity of material consumed
     */
    public Session(int sessionId, int userId, int equipmentId,
            Integer consumableId, LocalDateTime startTime,
            LocalDateTime endTime, double materialsConsumed) {

        this.sessionId = sessionId;
        this.userId = userId;
        this.equipmentId = equipmentId;
        this.consumableId = consumableId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.materialsConsumed = materialsConsumed;
    }

    /**
     * Starts the session using the current date and time.
     */
    public void startSession() {
        this.startTime = LocalDateTime.now();
    }

    /**
     * Ends the session using the current date and time.
     */
    public void endSession() {
        this.endTime = LocalDateTime.now();
    }

    /**
     * Calculates the session duration in hours.
     *
     * @return duration in hours, or zero when the session is incomplete
     */
    public double calculateUsage() {
        if (startTime == null || endTime == null) {
            return 0.0;
        }

        return Duration.between(startTime, endTime).toSeconds() / 3600.0;
    }

    /**
     * Gets the session identifier.
     *
     * @return session identifier
     */
    public int getSessionId() {
        return sessionId;
    }

    /**
     * Sets the session identifier.
     *
     * @param sessionId session identifier
     */
    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Gets the user identifier.
     *
     * @return user identifier
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user identifier.
     *
     * @param userId user identifier
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the equipment identifier.
     *
     * @return equipment identifier
     */
    public int getEquipmentId() {
        return equipmentId;
    }

    /**
     * Sets the equipment identifier.
     *
     * @param equipmentId equipment identifier
     */
    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    /**
     * Gets the consumable identifier.
     *
     * @return consumable identifier, or null when no consumable is used
     */
    public Integer getConsumableId() {
        return consumableId;
    }

    /**
     * Sets the consumable identifier.
     *
     * @param consumableId consumable identifier or null
     */
    public void setConsumableId(Integer consumableId) {
        this.consumableId = consumableId;
    }

    /**
     * Gets the session start time.
     *
     * @return session start time
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Sets the session start time.
     *
     * @param startTime session start time
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the session end time.
     *
     * @return end time, or null when the session is active
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Sets the session end time.
     *
     * @param endTime session end time or null
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Gets the amount of material consumed.
     *
     * @return amount of material consumed
     */
    public double getMaterialsConsumed() {
        return materialsConsumed;
    }

    /**
     * Sets the amount of material consumed.
     *
     * @param materialsConsumed amount of material consumed
     */
    public void setMaterialsConsumed(double materialsConsumed) {
        this.materialsConsumed = materialsConsumed;
    }

    /**
     * Returns a readable description of the session.
     *
     * @return session description
     */
    @Override
    public String toString() {
        return "Session{"
                + "sessionId=" + sessionId
                + ", userId=" + userId
                + ", equipmentId=" + equipmentId
                + ", consumableId=" + consumableId
                + ", startTime=" + startTime
                + ", endTime=" + endTime
                + ", materialsConsumed=" + materialsConsumed
                + '}';
    }
}