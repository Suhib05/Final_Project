package com.cmsc.model;

/**
 * Represents a CNC machine in the CMSC makerspace.
 *
 * @author Saleh Alhajj
 */
public class CNCMachine extends Equipment {

    /**
     * Creates a CNC machine with default equipment values.
     */
    public CNCMachine() {
        super();
        setCategory("CNC");
    }

    /**
     * Creates a CNC machine with complete equipment information.
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
    public CNCMachine(int equipmentId, String assetTag, String make,
            String model, String status, double hoursOfUse,
            double maintenanceThreshold, double accessCreditRate) {

        super(equipmentId, assetTag, make, model, "CNC",
                status, hoursOfUse, maintenanceThreshold, accessCreditRate);
    }
}