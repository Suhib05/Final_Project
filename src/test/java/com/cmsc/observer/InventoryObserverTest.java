package com.cmsc.observer;

import com.cmsc.dao.ConsumableDAO;
import com.cmsc.model.Consumable;
import com.cmsc.model.Printer3D;
import java.lang.reflect.Proxy;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests inventory notifications without modifying the project database.
 *
 * @author Saleh Alhajj
 */
public class InventoryObserverTest {

    /**
     * Verifies that low-stock consumables are detected after an equipment
     * notification.
     */
    @Test
    public void detectsLowStockConsumablesAfterEquipmentUpdate() {
        Consumable filament = createConsumable(
                101, "PLA Filament", 2.0, 5.0
        );

        ConsumableDAO fakeDAO = createFakeDAO(List.of(filament));
        InventoryObserver observer = new InventoryObserver(fakeDAO);
        Printer3D printer = createTestPrinter();

        printer.attach(observer);
        printer.logUsage(1.0);

        assertTrue(observer.hasLowStockAlerts());
        assertEquals(1, observer.getLowStockConsumables().size());
        assertEquals(
                "PLA Filament",
                observer.getLowStockConsumables().get(0).getType()
        );
    }

    /**
     * Verifies that an empty DAO result produces no inventory alert.
     */
    @Test
    public void reportsNoAlertWhenInventoryIsAboveThreshold() {
        ConsumableDAO fakeDAO = createFakeDAO(List.of());
        InventoryObserver observer = new InventoryObserver(fakeDAO);
        Printer3D printer = createTestPrinter();

        printer.attach(observer);
        printer.logUsage(1.0);

        assertFalse(observer.hasLowStockAlerts());
        assertTrue(observer.getLowStockConsumables().isEmpty());
    }

    /**
     * Verifies that a detached inventory observer receives no notification.
     */
    @Test
    public void detachedInventoryObserverDoesNotReceiveUpdate() {
        Consumable filament = createConsumable(
                102, "ABS Filament", 1.0, 4.0
        );

        ConsumableDAO fakeDAO = createFakeDAO(List.of(filament));
        InventoryObserver observer = new InventoryObserver(fakeDAO);
        Printer3D printer = createTestPrinter();

        printer.attach(observer);
        printer.detach(observer);
        printer.logUsage(1.0);

        assertFalse(observer.hasLowStockAlerts());
    }

    private Consumable createConsumable(
            int id, String type, double stock, double reorderThreshold) {

        Consumable consumable = new Consumable();
        consumable.setConsumableId(id);
        consumable.setType(type);
        consumable.setStockLevel(stock);
        consumable.setReorderThreshold(reorderThreshold);

        return consumable;
    }

    private Printer3D createTestPrinter() {
        Printer3D printer = new Printer3D();
        printer.setEquipmentId(9100);
        printer.setAssetTag("INVENTORY-TEST-PRINTER");
        printer.setHoursOfUse(0.0);
        printer.setMaintenanceThreshold(200.0);
        return printer;
    }

    private ConsumableDAO createFakeDAO(
            List<Consumable> lowStockConsumables) {

        return (ConsumableDAO) Proxy.newProxyInstance(
                ConsumableDAO.class.getClassLoader(),
                new Class<?>[]{ConsumableDAO.class},
                (proxy, method, arguments) -> {
                    if ("findLowStock".equals(method.getName())) {
                        return lowStockConsumables;
                    }

                    throw new UnsupportedOperationException(
                            "Unexpected DAO method: " + method.getName()
                    );
                }
        );
    }
}