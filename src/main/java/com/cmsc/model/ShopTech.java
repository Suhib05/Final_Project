package com.cmsc.model;

/**
 * Specialized user who manages equipment and maintenance.
 *
 * @author Suhib
 */
public class ShopTech extends User {
    
    private double maintenanceHours;

    /** Creates an empty Shop-Tech for DAO mapping. */
    public ShopTech() {
        super();
        setUserType(UserType.SHOP_TECH);
    }

    /**
     * Creates a registered Shop-Tech.
     *
     * @param name Shop-Tech name
     * @param email Shop-Tech email
     * @param passwordHash bcrypt password hash
     */
    public ShopTech(String name, String email, String passwordHash) {
        super(name, email, passwordHash, UserType.SHOP_TECH);
        this.maintenanceHours = 0.0;
    }

    /** @return accumulated maintenance hours */
    public double getMaintenanceHours() {
        return maintenanceHours;
    }

    /** @param maintenanceHours accumulated maintenance hours */
    public void setMaintenanceHours(double maintenanceHours) {
        this.maintenanceHours = maintenanceHours;
    }
    
    /** @param hours maintenance hours to add */
    public void addMaintenanceHours(double hours) {
        this.maintenanceHours += hours;
    }
}
