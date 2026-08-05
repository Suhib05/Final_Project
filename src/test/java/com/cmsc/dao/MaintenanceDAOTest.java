package com.cmsc.dao;

import com.cmsc.model.MaintenanceLog;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests MaintenanceDAOImpl against the real CMSC database.
 *
 * @author Saleh Alhajj
 */
class MaintenanceDAOTest {

    private final MaintenanceDAO maintenanceDAO
            = new MaintenanceDAOImpl();

    @Test
    void savesFindsUpdatesAndDeletesMaintenanceLog() {
        LocalDateTime dateLogged
                = LocalDateTime.now().withNano(0);

        MaintenanceLog testLog = new MaintenanceLog(
                0,
                1,
                3,
                dateLogged,
                "Test preventive maintenance",
                120.5
        );

        MaintenanceLog savedLog
                = maintenanceDAO.save(testLog);

        int generatedId = savedLog.getLogId();

        assertTrue(generatedId > 0);

        try {
            MaintenanceLog foundLog
                    = maintenanceDAO.findById(generatedId);

            assertNotNull(foundLog);
            assertEquals(1, foundLog.getEquipmentId());
            assertEquals(3, foundLog.getShopTechId());
            assertEquals(
                    "Test preventive maintenance",
                    foundLog.getDescription()
            );
            assertEquals(
                    120.5,
                    foundLog.getHoursAtService(),
                    0.001
            );

            List<MaintenanceLog> equipmentLogs
                    = maintenanceDAO.findByEquipmentId(1);

            assertTrue(equipmentLogs.stream()
                    .anyMatch(log -> log.getLogId() == generatedId));

            List<MaintenanceLog> shopTechLogs
                    = maintenanceDAO.findByShopTechId(3);

            assertTrue(shopTechLogs.stream()
                    .anyMatch(log -> log.getLogId() == generatedId));

            MaintenanceLog latestLog
                    = maintenanceDAO.findLatestByEquipment(1);

            assertNotNull(latestLog);
            assertEquals(generatedId, latestLog.getLogId());

            savedLog.setDescription("Updated maintenance description");
            savedLog.setHoursAtService(121.0);

            maintenanceDAO.update(savedLog);

            MaintenanceLog updatedLog
                    = maintenanceDAO.findById(generatedId);

            assertNotNull(updatedLog);
            assertEquals(
                    "Updated maintenance description",
                    updatedLog.getDescription()
            );
            assertEquals(
                    121.0,
                    updatedLog.getHoursAtService(),
                    0.001
            );

            List<MaintenanceLog> allLogs
                    = maintenanceDAO.findAll();

            assertTrue(allLogs.stream()
                    .anyMatch(log -> log.getLogId() == generatedId));
        } finally {
            maintenanceDAO.delete(generatedId);
        }

        assertNull(maintenanceDAO.findById(generatedId));
    }
}