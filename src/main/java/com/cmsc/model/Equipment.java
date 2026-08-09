package com.cmsc.model;
import com.cmsc.observer.EquipmentSubject;
import com.cmsc.observer.Observer;
import java.util.ArrayList;
import java.util.List;
/**
 * Represents shared information and behaviour for equipment in the CMSC.
 * Concrete equipment types such as Printer3D, LaserCutter, and CNCMachine
 * extend this class.
 *
 * @author Saleh Alhajj
 */
public abstract class Equipment implements EquipmentSubject {

    private int equipmentId;
    private String assetTag;
    private String make;
    private String model;
    private String category;
    private String consumableType;
    private String status;
    private double hoursOfUse;
    private double maintenanceThreshold;
    private double accessCreditRate;
    private final List<Observer> observers = new ArrayList<>();

    /**
     * Creates equipment using the default database values.
     */
    protected Equipment() {
        this.status = "AVAILABLE";
        this.consumableType = "Not specified";
        this.hoursOfUse = 0.0;
        this.maintenanceThreshold = 200.0;
        this.accessCreditRate = 1.0;
    }

    /**
     * Creates equipment with all its information.
     *
     * @param equipmentId database identifier
     * @param assetTag unique equipment asset tag
     * @param make equipment manufacturer
     * @param model equipment model
     * @param category equipment category
     * @param status current equipment status
     * @param hoursOfUse total recorded usage hours
     * @param maintenanceThreshold hours that trigger maintenance
     * @param accessCreditRate debit rate per access hour
     */
    protected Equipment(int equipmentId, String assetTag, String make,
            String model, String category, String status, double hoursOfUse,
            double maintenanceThreshold, double accessCreditRate) {

        this.equipmentId = equipmentId;
        this.assetTag = assetTag;
        this.make = make;
        this.model = model;
        this.category = category;
        this.consumableType = "Not specified";
        this.status = status;
        this.hoursOfUse = hoursOfUse;
        this.maintenanceThreshold = maintenanceThreshold;
        this.accessCreditRate = accessCreditRate;
    }

    /**
     * Changes the current equipment status.
     *
     * @param status new equipment status
     */
    public void updateStatus(String status) {
        this.status = status;
    }

    /**
    * Adds operating hours to this equipment and notifies all registered
    * observers so that maintenance and inventory conditions can be checked.
    *
    * @param hours number of operating hours to add
    */
    public void logUsage(double hours) {
        if (hours > 0) {
            this.hoursOfUse += hours;
            notifyObservers();
        }
    }
    
    /**
    * Registers an observer to receive equipment updates.
    *
    * @param observer observer to register
    */
    @Override
    public void attach(Observer observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
    * Removes a previously registered observer.
    *
    * @param observer observer to remove
    */
    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    /**
    * Notifies all currently registered observers about this equipment.
    */
    @Override
    public void notifyObservers() {
        List<Observer> observerSnapshot = new ArrayList<>(observers);

        for (Observer observer : observerSnapshot) {
            observer.update(this);
        }
    }

    /**
     * Gets the equipment database identifier.
     *
     * @return equipment identifier
     */
    public int getEquipmentId() {
        return equipmentId;
    }

    /**
     * Sets the equipment database identifier.
     *
     * @param equipmentId equipment identifier
     */
    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    /**
     * Gets the unique asset tag.
     *
     * @return asset tag
     */
    public String getAssetTag() {
        return assetTag;
    }

    /**
     * Sets the unique asset tag.
     *
     * @param assetTag asset tag
     */
    public void setAssetTag(String assetTag) {
        this.assetTag = assetTag;
    }

    /**
     * Gets the equipment manufacturer.
     *
     * @return manufacturer name
     */
    public String getMake() {
        return make;
    }

    /**
     * Sets the equipment manufacturer.
     *
     * @param make manufacturer name
     */
    public void setMake(String make) {
        this.make = make;
    }

    /**
     * Gets the equipment model.
     *
     * @return model name
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the equipment model.
     *
     * @param model model name
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Gets the equipment category.
     *
     * @return equipment category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the equipment category.
     *
     * @param category equipment category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the consumable type registered for this equipment.
     *
     * @return registered consumable type
     */
    public String getConsumableType() {
        return consumableType;
    }

    /**
     * Sets the consumable type registered for this equipment.
     *
     * @param consumableType registered consumable type
     */
    public void setConsumableType(String consumableType) {
        this.consumableType = consumableType;
    }

    /**
     * Gets the current equipment status.
     *
     * @return current status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the current equipment status.
     *
     * @param status current status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the total usage hours.
     *
     * @return total usage hours
     */
    public double getHoursOfUse() {
        return hoursOfUse;
    }

    /**
     * Sets the total usage hours.
     *
     * @param hoursOfUse total usage hours
     */
    public void setHoursOfUse(double hoursOfUse) {
        this.hoursOfUse = hoursOfUse;
    }

    /**
     * Gets the maintenance threshold.
     *
     * @return maintenance threshold in hours
     */
    public double getMaintenanceThreshold() {
        return maintenanceThreshold;
    }

    /**
     * Sets the maintenance threshold.
     *
     * @param maintenanceThreshold maintenance threshold in hours
     */
    public void setMaintenanceThreshold(double maintenanceThreshold) {
        this.maintenanceThreshold = maintenanceThreshold;
    }

    /**
     * Gets the access credit rate.
     *
     * @return credit rate per hour
     */
    public double getAccessCreditRate() {
        return accessCreditRate;
    }

    /**
     * Sets the access credit rate.
     *
     * @param accessCreditRate credit rate per hour
     */
    public void setAccessCreditRate(double accessCreditRate) {
        this.accessCreditRate = accessCreditRate;
    }

    /**
     * Returns a simple diagnostic based on equipment state and accumulated
     * usage. This supports the maintenance screen without storing duplicate
     * diagnostic state in the database.
     *
     * @return NORMAL, WARNING, or SERVICE_REQUIRED
     */
    public String getDiagnosticStatus() {
        if ("DOWN_FOR_MAINTENANCE".equals(status)
                || hoursOfUse >= maintenanceThreshold) {
            return "SERVICE_REQUIRED";
        }

        if (maintenanceThreshold > 0
                && hoursOfUse >= maintenanceThreshold * 0.8) {
            return "WARNING";
        }

        return "NORMAL";
    }

    /**
     * Returns a readable description of the equipment.
     *
     * @return equipment description
     */
    @Override
    public String toString() {
        return "Equipment{"
                + "equipmentId=" + equipmentId
                + ", assetTag='" + assetTag + '\''
                + ", make='" + make + '\''
                + ", model='" + model + '\''
                + ", category='" + category + '\''
                + ", consumableType='" + consumableType + '\''
                + ", status='" + status + '\''
                + ", hoursOfUse=" + hoursOfUse
                + ", maintenanceThreshold=" + maintenanceThreshold
                + ", accessCreditRate=" + accessCreditRate
                + '}';
    }
}
