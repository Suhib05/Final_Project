package com.cmsc.observer;

import com.cmsc.dao.ConsumableDAO;
import com.cmsc.model.Consumable;
import com.cmsc.model.Equipment;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Observes equipment activity and checks the consumable inventory for
 * low-stock conditions.
 *
 * The current low-stock information is retrieved through the DAO layer.
 *
 * @author Saleh Alhajj
 */
public class InventoryObserver implements Observer {

    private final ConsumableDAO consumableDAO;
    private List<Consumable> lowStockConsumables = new ArrayList<>();

    /**
     * Creates an inventory observer using the supplied consumable DAO.
     *
     * @param consumableDAO DAO used to retrieve low-stock consumables
     */
    public InventoryObserver(ConsumableDAO consumableDAO) {
        this.consumableDAO = Objects.requireNonNull(
                consumableDAO, "ConsumableDAO cannot be null"
        );
    }

    /**
     * Checks the database for consumables that have reached their reorder
     * threshold after equipment activity occurs.
     *
     * @param equipment equipment that generated the notification
     */
    @Override
    public void update(Equipment equipment) {
        if (equipment == null) {
            return;
        }

        List<Consumable> results = consumableDAO.findLowStock();

        if (results == null) {
            lowStockConsumables = new ArrayList<>();
        } else {
            lowStockConsumables = new ArrayList<>(results);
        }
    }

    /**
     * Returns the currently detected low-stock consumables.
     *
     * @return copy of the low-stock consumable list
     */
    public List<Consumable> getLowStockConsumables() {
        return new ArrayList<>(lowStockConsumables);
    }

    /**
     * Determines whether any low-stock alerts currently exist.
     *
     * @return true when at least one consumable requires restocking
     */
    public boolean hasLowStockAlerts() {
        return !lowStockConsumables.isEmpty();
    }
}