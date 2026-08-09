package com.cmsc.dao;

import com.cmsc.model.Training;

import java.util.List;

/**
 * Defines database operations for training sessions delivered by Trainers.
 *
 * @author Annour Mohamed
 */
public interface TrainingDAO extends GenericDAO<Training> {

    /**
     * Finds every session one Trainer has delivered, newest first.
     *
     * @param trainerId trainer user identifier
     * @return the Trainer's delivered sessions
     */
    List<Training> findByTrainerId(int trainerId);

    /**
     * Finds every session delivered on one piece of equipment, newest first.
     *
     * @param equipmentId equipment identifier
     * @return sessions covering that equipment
     */
    List<Training> findByEquipmentId(int equipmentId);

    /**
     * Counts the sessions one Trainer has delivered, used on the reports page.
     *
     * @param trainerId trainer user identifier
     * @return number of sessions delivered
     */
    int countByTrainerId(int trainerId);
}
