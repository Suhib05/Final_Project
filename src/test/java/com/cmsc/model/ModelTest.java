package com.cmsc.model;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the Member 2 model classes before the DAO layer is implemented.
 *
 * @author Saleh Alhajj
 */
class ModelTest {

    @Test
    void createsCorrectEquipmentTypes() {
        Equipment printer = new Printer3D();
        Equipment laser = new LaserCutter();
        Equipment cnc = new CNCMachine();

        assertEquals("PRINTER_3D", printer.getCategory());
        assertEquals("LASER_CUTTER", laser.getCategory());
        assertEquals("CNC", cnc.getCategory());

        assertEquals("AVAILABLE", printer.getStatus());
        assertEquals(200.0, printer.getMaintenanceThreshold(), 0.001);
        assertEquals(1.0, printer.getAccessCreditRate(), 0.001);
    }

    @Test
    void updatesEquipmentStatusAndUsage() {
        Equipment printer = new Printer3D();

        printer.logUsage(2.5);
        printer.updateStatus("IN_USE");

        assertEquals(2.5, printer.getHoursOfUse(), 0.001);
        assertEquals("IN_USE", printer.getStatus());
    }

    @Test
    void calculatesSessionUsage() {
        Session session = new Session();

        session.setStartTime(LocalDateTime.of(2026, 8, 5, 9, 0));
        session.setEndTime(LocalDateTime.of(2026, 8, 5, 11, 30));

        assertEquals(2.5, session.calculateUsage(), 0.001);
    }

    @Test
    void identifiesLowConsumableStock() {
        Consumable consumable = new Consumable(
                1, "PLA Filament", "g", 50.0, 10.0, 100.0
        );

        assertTrue(consumable.isLowStock());

        consumable.updateStock(200.0);

        assertFalse(consumable.isLowStock());
    }

    @Test
    void createsMaintenanceLogWithCurrentDate() {
        MaintenanceLog log = new MaintenanceLog();

        log.setEquipmentId(4);
        log.setShopTechId(3);
        log.setDescription("Replaced spindle belt");
        log.setHoursAtService(200.0);

        assertNotNull(log.getDateLogged());
        assertEquals(4, log.getEquipmentId());
        assertEquals(3, log.getShopTechId());
        assertEquals("Replaced spindle belt", log.getDescription());
        assertEquals(200.0, log.getHoursAtService(), 0.001);
    }

 
    @Test
    public void calculatesFractionalSessionHours() {
        Session session = new Session();

        LocalDateTime startTime
                = LocalDateTime.of(2026, 8, 5, 10, 0, 0);

        session.setStartTime(startTime);
        session.setEndTime(startTime.plusSeconds(45));

        assertEquals(
                0.0125,
                session.calculateUsage(),
                0.000001
            );
    }
}