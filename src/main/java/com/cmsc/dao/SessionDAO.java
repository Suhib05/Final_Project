package com.cmsc.dao;

import com.cmsc.model.Session;
import java.util.List;

/**
 * Defines database operations for equipment booking and usage sessions.
 * Member 1's booking and check-in/out servlets can use this interface.
 *
 * @author Saleh Alhajj
 */
public interface SessionDAO extends GenericDAO<Session> {

    /**
     * Creates a new booking or usage session.
     *
     * @param session session to create
     * @return saved session containing its generated identifier
     */
    Session createSession(Session session);

    /**
     * Updates an existing session during check-in or check-out.
     *
     * @param session session containing updated information
     * @return updated session
     */
    Session updateSession(Session session);

    /**
     * Finds the active session for one piece of equipment.
     * A session is active when its end time is null.
     *
     * @param equipmentId equipment identifier
     * @return active session, or null when the equipment has no active session
     */
    Session findActiveByEquipment(int equipmentId);

    /**
     * Finds all sessions belonging to one user.
     *
     * @param userId user identifier
     * @return sessions belonging to the user
     */
    List<Session> findByUserId(int userId);
}