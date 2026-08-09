package com.cmsc.model;

/**
 * Specialized user who can deliver training sessions.
 *
 * @author Suhib
 */
public class Trainer extends User {
    
    private double trainingHours;
    
    /** Creates an empty Trainer for DAO mapping. */
    public Trainer() {
        super();
        setUserType(UserType.TRAINER);
    }

    /**
     * Creates a registered Trainer.
     *
     * @param name trainer name
     * @param email trainer email
     * @param passwordHash bcrypt password hash
     */
    public Trainer(String name, String email, String passwordHash) {
        super(name, email, passwordHash, UserType.TRAINER);
        this.trainingHours = 0.0;
    }

    /** @return accumulated training hours */
    public double getTrainingHours() {
        return trainingHours;
    }

    /** @param trainingHours accumulated training hours */
    public void setTrainingHours(double trainingHours) {
        this.trainingHours = trainingHours;
    }
    
    /** @param hours training hours to add */
    public void addTrainingHours(double hours) {
        this.trainingHours += hours;
    }
}
