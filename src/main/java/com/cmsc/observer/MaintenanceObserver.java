package com.cmsc.observer;

import com.cmsc.model.Equipment;
import java.util.ArrayList;
import java.util.List;

/**
 * Observes equipment usage and maintains a list of equipment whose operating
 * hours have reached their predictive-maintenance threshold.
 *
 * The alerts are calculated from the equipment information instead of being
 * stored in a separate database table.
 *
 * @author Saleh Alhajj
 */
public class MaintenanceObserver implements Observer {

    private final List<Equipment> maintenanceAlerts = new ArrayList<>();

    /**
     * Checks whether the updated equipment requires maintenance.
     *
     * @param equipment equipment that generated the update
     */
    @Override
    public void update(Equipment equipment) {
        if (equipment == null) {
            return;
        }

        boolean requiresMaintenance
                = equipment.getHoursOfUse()
                >= equipment.getMaintenanceThreshold();

        if (requiresMaintenance && !containsEquipment(equipment)) {
            maintenanceAlerts.add(equipment);
        } else if (!requiresMaintenance) {
            maintenanceAlerts.removeIf(
                    currentEquipment
                    -> isSameEquipment(currentEquipment, equipment)
            );
        }
    }

    /**
     * Returns the equipment currently requiring maintenance.
     *
     * @return copy of the current maintenance-alert list
     */
    public List<Equipment> getMaintenanceAlerts() {
        return new ArrayList<>(maintenanceAlerts);
    }

    /**
     * Determines whether an alert exists for the supplied equipment.
     *
     * @param equipment equipment to check
     * @return true when the equipment has an active maintenance alert
     */
    public boolean hasAlertFor(Equipment equipment) {
        return equipment != null && containsEquipment(equipment);
    }

    private boolean containsEquipment(Equipment equipment) {
        return maintenanceAlerts.stream()
                .anyMatch(currentEquipment
                        -> isSameEquipment(currentEquipment, equipment));
    }

    private boolean isSameEquipment(Equipment first, Equipment second) {
        if (first == second) {
            return true;
        }

        if (first.getEquipmentId() > 0 && second.getEquipmentId() > 0) {
            return first.getEquipmentId() == second.getEquipmentId();
        }

        return first.getAssetTag() != null
                && first.getAssetTag().equals(second.getAssetTag());
    }
}