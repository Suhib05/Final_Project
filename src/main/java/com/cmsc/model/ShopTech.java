/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cmsc.model;

/**
 *
 * @author DELL
 */
public class ShopTech extends User {
    
    private double  maintenanceHours;

    public ShopTech(){
        super();
        
        setUserType(UserType.SHOP_TECH);
    }
    
    public ShopTech(String name, String email, String passwordHash){
        super (name, email, passwordHash,  UserType.SHOP_TECH );
        this.maintenanceHours = 0.0;
        
    }

    public double getMaintenanceHours() {
        return maintenanceHours;
    }

    public void setMaintenanceHours(double maintenanceHours) {
        this.maintenanceHours = maintenanceHours;
    }
    
    public void addMaintenanceHours (double hours){
        this.maintenanceHours += hours;
    }
    
}
