package com.cmsc.model;

/**
 * Represents a 3D printer in the CMSC makerspace.
 *
 * @author Saleh Alhajj
 */
public class Printer3D extends Equipment {

    /**
     * Creates a 3D printer with default equipment values.
     */
    public Printer3D() {
        super();
        setCategory("PRINTER_3D");
    }

    /**
     * Creates a 3D printer with complete equipment information.
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
    public Printer3D(int equipmentId, String assetTag, String make,
            String model, String status, double hoursOfUse,
            double maintenanceThreshold, double accessCreditRate) {

        super(equipmentId, assetTag, make, model, "PRINTER_3D",
                status, hoursOfUse, maintenanceThreshold, accessCreditRate);
    }
}