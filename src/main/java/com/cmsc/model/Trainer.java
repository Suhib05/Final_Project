/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cmsc.model;

/**
 *
 * @author DELL
 */
public class Trainer extends User {
    
    private double trainingHours;
    
    public Trainer(){
        super ();
        setUserType(UserType.TRAINER);
    }

    public Trainer(String name, String email, String passwordHash) {
        super(name, email, passwordHash, UserType.TRAINER);
        this.trainingHours = 0.0;
    }

    public double getTrainingHours() {
        return trainingHours;
    }

    public void setTrainingHours(double trainingHours) {
        this.trainingHours = trainingHours;
    }
    
    public void addTrainingHours(double hours){
        this.trainingHours += hours;
    }
    
    
}
