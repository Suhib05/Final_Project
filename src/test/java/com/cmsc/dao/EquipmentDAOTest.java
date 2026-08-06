package com.cmsc.dao;

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

        List<Equipment> requiringMaintenance
                = equipmentDAO.findRequiringMaintenance();

        assertTrue(requiringMaintenance.stream()
                .anyMatch(equipment
                        -> "CNC-001".equals(equipment.getAssetTag())));
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