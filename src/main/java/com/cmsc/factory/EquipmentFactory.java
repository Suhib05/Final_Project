package com.cmsc.factory;

import com.cmsc.model.CNCMachine;
import com.cmsc.model.Equipment;
import com.cmsc.model.LaserCutter;
import com.cmsc.model.Printer3D;

/**
 * Creates the correct concrete equipment object based on its category.
 * This class implements the Simple Factory pattern required by the project.
 *
 * @author Saleh Alhajj
 */
public final class EquipmentFactory {

    /**
     * Prevents creation of EquipmentFactory objects.
     */
    private EquipmentFactory() {
    }

    /**
     * Creates equipment using its category.
     *
     * @param category PRINTER_3D, LASER_CUTTER, or CNC
     * @return the matching concrete Equipment object
     * @throws IllegalArgumentException when the category is missing or invalid
     */
    public static Equipment createEquipment(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException(
                    "Equipment category is required"
            );
        }

        return switch (category.trim().toUpperCase()) {
            case "PRINTER_3D" -> new Printer3D();
            case "LASER_CUTTER" -> new LaserCutter();
            case "CNC" -> new CNCMachine();
            default -> throw new IllegalArgumentException(
                    "Unsupported equipment category: " + category
            );
        };
    }
}