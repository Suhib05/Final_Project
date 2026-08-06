package com.cmsc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A read-only, co-op-wide view of the maker space, assembled for FR-06
 * reporting.
 *
 * The figures come from the DAOs owned by Member 2; this class only arranges
 * them for display. It holds no persistence logic and is never written back to
 * the database.
 *
 * @author Annour Mohamed
 */
public class EquipmentStatusReport {

    private List<Equipment> equipment = new ArrayList<>();
    private List<Equipment> maintenanceAlerts = new ArrayList<>();
    private List<Consumable> lowStockConsumables = new ArrayList<>();

    /**
     * Creates an empty report.
     */
    public EquipmentStatusReport() {
    }

    /**
     * Creates a report from the figures the DAOs returned.
     *
     * @param equipment every machine in the space
     * @param maintenanceAlerts machines past their maintenance threshold
     * @param lowStockConsumables materials at or below their reorder level
     */
    public EquipmentStatusReport(List<Equipment> equipment,
            List<Equipment> maintenanceAlerts,
            List<Consumable> lowStockConsumables) {

        setEquipment(equipment);
        setMaintenanceAlerts(maintenanceAlerts);
        setLowStockConsumables(lowStockConsumables);
    }

    /**
     * Returns how many machines the space has.
     *
     * @return total equipment count
     */
    public int getTotalEquipment() {
        return equipment.size();
    }

    /**
     * Counts the machines currently in one state.
     *
     * @param status status to count, for example AVAILABLE
     * @return number of machines in that state
     */
    public int countByStatus(String status) {
        int count = 0;

        for (Equipment machine : equipment) {
            if (machine.getStatus() != null
                    && machine.getStatus().equalsIgnoreCase(status)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Returns how many machines are free to book.
     *
     * @return available machine count
     */
    public int getAvailableCount() {
        return countByStatus("AVAILABLE");
    }

    /**
     * Returns how many machines are in use.
     *
     * @return in-use machine count
     */
    public int getInUseCount() {
        return countByStatus("IN_USE");
    }

    /**
     * Returns how many machines are out of service.
     *
     * @return machine count down for maintenance
     */
    public int getDownCount() {
        return countByStatus("DOWN_FOR_MAINTENANCE");
    }

    /**
     * Returns how many maintenance alerts are outstanding.
     *
     * @return number of machines past their threshold
     */
    public int getAlertCount() {
        return maintenanceAlerts.size();
    }

    /**
     * Returns how many materials need restocking.
     *
     * @return number of consumables at or below their reorder level
     */
    public int getLowStockCount() {
        return lowStockConsumables.size();
    }

    /**
     * Reports whether anything in the space needs attention, which is what
     * the report highlights at the top of the page.
     *
     * @return true when there is a maintenance alert or a restock to do
     */
    public boolean hasOutstandingIssues() {
        return getAlertCount() > 0 || getLowStockCount() > 0;
    }

    /**
     * Returns every machine in the space.
     *
     * @return the equipment list
     */
    public List<Equipment> getEquipment() {
        return equipment;
    }

    /**
     * Sets the machines the report describes.
     *
     * @param equipment the equipment list
     */
    public final void setEquipment(List<Equipment> equipment) {
        this.equipment = equipment == null ? new ArrayList<>() : equipment;
    }

    /**
     * Returns the machines past their maintenance threshold.
     *
     * @return outstanding maintenance alerts
     */
    public List<Equipment> getMaintenanceAlerts() {
        return maintenanceAlerts;
    }

    /**
     * Sets the machines past their maintenance threshold.
     *
     * @param maintenanceAlerts outstanding maintenance alerts
     */
    public final void setMaintenanceAlerts(List<Equipment> maintenanceAlerts) {
        this.maintenanceAlerts = maintenanceAlerts == null
                ? new ArrayList<>() : maintenanceAlerts;
    }

    /**
     * Returns the materials that need restocking.
     *
     * @return low stock consumables
     */
    public List<Consumable> getLowStockConsumables() {
        return lowStockConsumables;
    }

    /**
     * Sets the materials that need restocking.
     *
     * @param lowStockConsumables low stock consumables
     */
    public final void setLowStockConsumables(
            List<Consumable> lowStockConsumables) {

        this.lowStockConsumables = lowStockConsumables == null
                ? new ArrayList<>() : lowStockConsumables;
    }
}
