package com.cmsc.observer;

import com.cmsc.model.Printer3D;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the predictive-maintenance Observer implementation.
 *
 * @author Saleh Alhajj
 */
public class MaintenanceObserverTest {

    /**
     * Verifies that reaching the maintenance threshold creates an alert.
     */
    @Test
    public void createsAlertWhenMaintenanceThresholdIsReached() {
        Printer3D printer = createTestPrinter(190.0, 200.0);
        MaintenanceObserver observer = new MaintenanceObserver();

        printer.attach(observer);
        printer.logUsage(10.0);

        assertTrue(observer.hasAlertFor(printer));
        assertEquals(1, observer.getMaintenanceAlerts().size());
    }

    /**
     * Verifies that equipment below its threshold does not create an alert.
     */
    @Test
    public void doesNotCreateAlertBelowMaintenanceThreshold() {
        Printer3D printer = createTestPrinter(100.0, 200.0);
        MaintenanceObserver observer = new MaintenanceObserver();

        printer.attach(observer);
        printer.logUsage(50.0);

        assertFalse(observer.hasAlertFor(printer));
        assertTrue(observer.getMaintenanceAlerts().isEmpty());
    }

    /**
     * Verifies that repeated notifications do not create duplicate alerts.
     */
    @Test
    public void doesNotCreateDuplicateMaintenanceAlerts() {
        Printer3D printer = createTestPrinter(195.0, 200.0);
        MaintenanceObserver observer = new MaintenanceObserver();

        printer.attach(observer);
        printer.logUsage(10.0);
        printer.logUsage(5.0);

        assertEquals(1, observer.getMaintenanceAlerts().size());
    }

    /**
     * Verifies that a detached observer no longer receives updates.
     */
    @Test
    public void detachedObserverDoesNotReceiveUpdates() {
        Printer3D printer = createTestPrinter(190.0, 200.0);
        MaintenanceObserver observer = new MaintenanceObserver();

        printer.attach(observer);
        printer.detach(observer);
        printer.logUsage(20.0);

        assertFalse(observer.hasAlertFor(printer));
    }

    private Printer3D createTestPrinter(
            double hoursOfUse, double maintenanceThreshold) {

        Printer3D printer = new Printer3D();
        printer.setEquipmentId(9001);
        printer.setAssetTag("TEST-PRINTER-9001");
        printer.setHoursOfUse(hoursOfUse);
        printer.setMaintenanceThreshold(maintenanceThreshold);

        return printer;
    }
}