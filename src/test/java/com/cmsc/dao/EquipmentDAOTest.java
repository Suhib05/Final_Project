package com.cmsc.dao;

import com.cmsc.model.CNCMachine;
import com.cmsc.model.Equipment;
import com.cmsc.model.Printer3D;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests EquipmentDAOImpl against the real CMSC database.
 *
 * @author Saleh Alhajj
 */
class EquipmentDAOTest {

    private final EquipmentDAO equipmentDAO = new EquipmentDAOImpl();

    @Test
    void retrievesSeededEquipment() {
        List<Equipment> allEquipment = equipmentDAO.findAll();

        assertFalse(allEquipment.isEmpty());
        assertTrue(allEquipment.size() >= 4);

        Equipment printer = equipmentDAO.findByAssetTag("PR3D-001");

        assertNotNull(printer);
        assertTrue(printer instanceof Printer3D);
        assertEquals("Prusa", printer.getMake());
        assertEquals("MK4", printer.getModel());

        List<Equipment> available
                = equipmentDAO.findByStatus("AVAILABLE");

        assertFalse(available.isEmpty());
    }

    /**
     * Confirms a machine past its threshold is reported as needing service.
     *
     * The test creates its own machine rather than asserting against a seeded
     * one, because completing maintenance through the application resets the
     * usage hours. Reading live state made this fail for anyone who had used
     * the maintenance page.
     */
    @Test
    void reportsEquipmentPastItsMaintenanceThreshold() {
        String assetTag = "MAINT-" + System.currentTimeMillis();

        Equipment overdue = new CNCMachine(
                0,
                assetTag,
                "Test Make",
                "Test Model",
                "AVAILABLE",
                250.0,
                200.0,
                2.50
        );

        Equipment saved = equipmentDAO.save(overdue);
        int generatedId = saved.getEquipmentId();

        assertTrue(generatedId > 0);

        try {
            List<Equipment> requiringMaintenance
                    = equipmentDAO.findRequiringMaintenance();

            assertTrue(requiringMaintenance.stream()
                    .anyMatch(equipment
                            -> assetTag.equals(equipment.getAssetTag())),
                    "Equipment past its threshold should require maintenance");

            saved.setHoursOfUse(0.0);
            equipmentDAO.update(saved);

            assertFalse(equipmentDAO.findRequiringMaintenance().stream()
                    .anyMatch(equipment
                            -> assetTag.equals(equipment.getAssetTag())),
                    "Serviced equipment should no longer require maintenance");
        } finally {
            equipmentDAO.delete(generatedId);
        }
    }

    @Test
    void savesUpdatesFindsAndDeletesEquipment() {
        String assetTag = "TEST-" + System.currentTimeMillis();

        Equipment testEquipment = new Printer3D(
                0,
                assetTag,
                "Test Make",
                "Test Model",
                "AVAILABLE",
                10.0,
                100.0,
                1.5
        );

        Equipment savedEquipment = equipmentDAO.save(testEquipment);
        int generatedId = savedEquipment.getEquipmentId();

        assertTrue(generatedId > 0);

        try {
            Equipment foundEquipment
                    = equipmentDAO.findById(generatedId);

            assertNotNull(foundEquipment);
            assertEquals(assetTag, foundEquipment.getAssetTag());
            assertEquals("PRINTER_3D", foundEquipment.getCategory());

            foundEquipment.updateStatus("IN_USE");
            foundEquipment.setHoursOfUse(15.5);
            equipmentDAO.update(foundEquipment);

            Equipment updatedEquipment
                    = equipmentDAO.findById(generatedId);

            assertNotNull(updatedEquipment);
            assertEquals("IN_USE", updatedEquipment.getStatus());
            assertEquals(
                    15.5, updatedEquipment.getHoursOfUse(), 0.001
            );
        } finally {
            equipmentDAO.delete(generatedId);
        }

        assertNull(equipmentDAO.findById(generatedId));
        assertNull(equipmentDAO.findByAssetTag(assetTag));
    }
}