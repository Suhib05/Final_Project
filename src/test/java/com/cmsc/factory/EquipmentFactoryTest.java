package com.cmsc.factory;

import com.cmsc.model.CNCMachine;
import com.cmsc.model.Equipment;
import com.cmsc.model.LaserCutter;
import com.cmsc.model.Printer3D;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the required Simple Factory pattern.
 *
 * @author Saleh Alhajj
 */
class EquipmentFactoryTest {

    @Test
    void createsPrinter3D() {
        Equipment equipment
                = EquipmentFactory.createEquipment("PRINTER_3D");

        assertTrue(equipment instanceof Printer3D);
        assertEquals("PRINTER_3D", equipment.getCategory());
    }

    @Test
    void createsLaserCutterAndCNCMachine() {
        Equipment laser
                = EquipmentFactory.createEquipment("LASER_CUTTER");

        Equipment cnc
                = EquipmentFactory.createEquipment("CNC");

        assertTrue(laser instanceof LaserCutter);
        assertTrue(cnc instanceof CNCMachine);
        assertEquals("LASER_CUTTER", laser.getCategory());
        assertEquals("CNC", cnc.getCategory());
    }

    @Test
    void acceptsDifferentLetterCaseAndSpaces() {
        Equipment equipment
                = EquipmentFactory.createEquipment("  printer_3d  ");

        assertTrue(equipment instanceof Printer3D);
        assertEquals("PRINTER_3D", equipment.getCategory());
    }

    @Test
    void rejectsMissingOrUnsupportedCategory() {
        assertThrows(
                IllegalArgumentException.class,
                () -> EquipmentFactory.createEquipment(null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> EquipmentFactory.createEquipment(" ")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> EquipmentFactory.createEquipment("UNKNOWN")
        );
    }
}