package com.cmsc.model;

/**
 * Represents a consumable material tracked in the CMSC inventory.
 *
 * @author Saleh Alhajj
 */
public class Consumable {

    private int consumableId;
    private String type;
    private String unit;
    private double stockLevel;
    private double consumptionRate;
    private double reorderThreshold;

    /**
     * Creates a consumable using the database default values.
     */
    public Consumable() {
        this.unit = "g";
        this.stockLevel = 0.0;
        this.consumptionRate = 0.0;
        this.reorderThreshold = 100.0;
    }

    /**
     * Creates a consumable with complete inventory information.
     *
     * @param consumableId database identifier
     * @param type material type
     * @param unit measurement unit
     * @param stockLevel current stock quantity
     * @param consumptionRate average consumption rate
     * @param reorderThreshold low-stock threshold
     */
    public Consumable(int consumableId, String type, String unit,
            double stockLevel, double consumptionRate,
            double reorderThreshold) {

        this.consumableId = consumableId;
        this.type = type;
        this.unit = unit;
        this.stockLevel = stockLevel;
        this.consumptionRate = consumptionRate;
        this.reorderThreshold = reorderThreshold;
    }

    /**
     * Changes the current stock level.
     *
     * @param stockLevel new stock quantity
     */
    public void updateStock(double stockLevel) {
        this.stockLevel = stockLevel;
    }

    /** @return consumable identifier */
    public int getConsumableId() {
        return consumableId;
    }

    /** @param consumableId consumable identifier */
    public void setConsumableId(int consumableId) {
        this.consumableId = consumableId;
    }

    /** @return consumable type */
    public String getType() {
        return type;
    }

    /** @param type consumable type */
    public void setType(String type) {
        this.type = type;
    }

    /** @return measurement unit */
    public String getUnit() {
        return unit;
    }

    /** @param unit measurement unit */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /** @return current stock level */
    public double getStockLevel() {
        return stockLevel;
    }

    /** @param stockLevel current stock level */
    public void setStockLevel(double stockLevel) {
        this.stockLevel = stockLevel;
    }

    /** @return average consumption rate */
    public double getConsumptionRate() {
        return consumptionRate;
    }

    /** @param consumptionRate average consumption rate */
    public void setConsumptionRate(double consumptionRate) {
        this.consumptionRate = consumptionRate;
    }

    /** @return low-stock reorder threshold */
    public double getReorderThreshold() {
        return reorderThreshold;
    }

    /** @param reorderThreshold low-stock reorder threshold */
    public void setReorderThreshold(double reorderThreshold) {
        this.reorderThreshold = reorderThreshold;
    }

    /**
     * Returns whether this consumable has reached its reorder threshold.
     *
     * @return true when the stock level is low
     */
    public boolean isLowStock() {
        return stockLevel <= reorderThreshold;
    }

    /**
     * Returns a readable description of the consumable.
     *
     * @return consumable description
     */
    @Override
    public String toString() {
        return "Consumable{"
                + "consumableId=" + consumableId
                + ", type='" + type + '\''
                + ", unit='" + unit + '\''
                + ", stockLevel=" + stockLevel
                + ", consumptionRate=" + consumptionRate
                + ", reorderThreshold=" + reorderThreshold
                + '}';
    }
}