package com.cmsc.model;

import java.time.LocalDateTime;

/**
 * Represents one intro or safety training session delivered by a Trainer on a
 * specific piece of equipment.
 *
 * Training is one of the four ways a member contributes to the co-op, so every
 * row here is the basis for a TRAINING credit on the delivering Trainer's
 * ledger.
 *
 * @author Annour Mohamed
 */
public class Training {

    private int trainingId;
    private int trainerId;
    private int equipmentId;
    private LocalDateTime dateDelivered;

    /**
     * Creates a training session stamped with the current date and time.
     */
    public Training() {
        this.dateDelivered = LocalDateTime.now();
    }

    /**
     * Creates a training session that has not yet been persisted.
     *
     * @param trainerId Trainer who delivered the session
     * @param equipmentId equipment the session covered
     */
    public Training(int trainerId, int equipmentId) {
        this.trainerId = trainerId;
        this.equipmentId = equipmentId;
        this.dateDelivered = LocalDateTime.now();
    }

    /**
     * Creates a training session with complete information.
     *
     * @param trainingId database identifier
     * @param trainerId Trainer who delivered the session
     * @param equipmentId equipment the session covered
     * @param dateDelivered date and time the session was delivered
     */
    public Training(int trainingId, int trainerId, int equipmentId,
            LocalDateTime dateDelivered) {

        this.trainingId = trainingId;
        this.trainerId = trainerId;
        this.equipmentId = equipmentId;
        this.dateDelivered = dateDelivered;
    }

    /**
     * Returns the database identifier.
     *
     * @return training identifier
     */
    public int getTrainingId() {
        return trainingId;
    }

    /**
     * Sets the database identifier.
     *
     * @param trainingId training identifier
     */
    public void setTrainingId(int trainingId) {
        this.trainingId = trainingId;
    }

    /**
     * Returns the Trainer who delivered the session.
     *
     * @return trainer user identifier
     */
    public int getTrainerId() {
        return trainerId;
    }

    /**
     * Sets the Trainer who delivered the session.
     *
     * @param trainerId trainer user identifier
     */
    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    /**
     * Returns the equipment the session covered.
     *
     * @return equipment identifier
     */
    public int getEquipmentId() {
        return equipmentId;
    }

    /**
     * Sets the equipment the session covered.
     *
     * @param equipmentId equipment identifier
     */
    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    /**
     * Returns when the session was delivered.
     *
     * @return date and time delivered
     */
    public LocalDateTime getDateDelivered() {
        return dateDelivered;
    }

    /**
     * Sets when the session was delivered.
     *
     * @param dateDelivered date and time delivered
     */
    public void setDateDelivered(LocalDateTime dateDelivered) {
        this.dateDelivered = dateDelivered;
    }

    /**
     * Returns a readable summary of the session, used for logging and testing.
     *
     * @return string describing this training session
     */
    @Override
    public String toString() {
        return "Training{"
                + "id=" + trainingId
                + ", trainerId=" + trainerId
                + ", equipmentId=" + equipmentId
                + ", delivered=" + dateDelivered
                + '}';
    }
}
