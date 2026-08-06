package com.cmsc.dao;

import com.cmsc.model.Session;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests SessionDAOImpl against the real CMSC database.
 *
 * @author Saleh Alhajj
 */
class SessionDAOTest {

    private final SessionDAO sessionDAO = new SessionDAOImpl();

    @Test
    void createsFindsUpdatesAndDeletesSession() {
        LocalDateTime startTime = LocalDateTime.now().withNano(0);

        Session testSession = new Session(
                0,
                1,
                1,
                null,
                startTime,
                null,
                0.0
        );

        Session savedSession
                = sessionDAO.createSession(testSession);

        int generatedId = savedSession.getSessionId();

        assertTrue(generatedId > 0);

        try {
            Session foundSession
                    = sessionDAO.findById(generatedId);

            assertNotNull(foundSession);
            assertEquals(1, foundSession.getUserId());
            assertEquals(1, foundSession.getEquipmentId());
            assertNull(foundSession.getConsumableId());
            assertNull(foundSession.getEndTime());

            Session activeSession
                    = sessionDAO.findActiveByEquipment(1);

            assertNotNull(activeSession);
            assertEquals(generatedId, activeSession.getSessionId());

            List<Session> userSessions
                    = sessionDAO.findByUserId(1);

            assertTrue(userSessions.stream()
                    .anyMatch(session
                            -> session.getSessionId() == generatedId));

            List<Session> allSessions = sessionDAO.findAll();

            assertTrue(allSessions.stream()
                    .anyMatch(session
                            -> session.getSessionId() == generatedId));

            savedSession.setConsumableId(1);
            savedSession.setEndTime(startTime.plusHours(2));
            savedSession.setMaterialsConsumed(75.0);

            sessionDAO.updateSession(savedSession);

            Session updatedSession
                    = sessionDAO.findById(generatedId);

            assertNotNull(updatedSession);
            assertEquals(1, updatedSession.getConsumableId());
            assertEquals(
                    startTime.plusHours(2),
                    updatedSession.getEndTime()
            );
            assertEquals(
                    75.0,
                    updatedSession.getMaterialsConsumed(),
                    0.001
            );

            assertNull(sessionDAO.findActiveByEquipment(1));
        } finally {
            sessionDAO.delete(generatedId);
        }

        assertNull(sessionDAO.findById(generatedId));
    }
}