package com.cmsc.model;

/**
 * Represents a laser cutter in the CMSC makerspace.
 *
 * @author Saleh Alhajj
 */
public class LaserCutter extends Equipment {

    /**
     * Creates a laser cutter with default equipment values.
     */
    public LaserCutter() {
        super();
        setCategory("LASER_CUTTER");
    }

    /**
     * Creates a laser cutter with complete equipment information.
     *
     * @param equipmentId database identifier
     * @param assetTag unique asset tag
     * @param make manufacturer name
     * @param model model name
     * @param status current status
     * @param hoursOfUse recorded usage hours
     * @param maintenanceThreshold maintenance threshold
     * @param accessCreditRate credit rate per hour
     */
    public LaserCutter(int equipmentId, String assetTag, String make,
            String model, String status, double hoursOfUse,
            double maintenanceThreshold, double accessCreditRate) {

        super(equipmentId, assetTag, make, model, "LASER_CUTTER",
                status, hoursOfUse, maintenanceThreshold, accessCreditRate);
    }
}