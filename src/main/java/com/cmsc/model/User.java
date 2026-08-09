package com.cmsc.model;

import java.time.LocalDateTime;

/**
 * Represents a registered CMSC member and their role and ledger balances.
 *
 * @author Suhib
 */
public class User {
    
    private int userId;
    private String name;
    private String email;
    private String passwordHash;
    private UserType userType;
    private double creditBalance;
    private double debitBalance;
    private LocalDateTime createdAt;
    
    /**
     * Creates an empty user for DAO mapping.
     */
    public User() {
    }

    /**
     * Creates a new registered user.
     *
     * @param name member name
     * @param email member email
     * @param passwordHash bcrypt password hash
     * @param userType selected member role
     */
    public User(String name, String email, String passwordHash, UserType userType) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.userType = userType;
        this.creditBalance = 0.0;
        this.debitBalance = 0.0;
    }

    /** @return database identifier */
    public int getUserId() {
        return userId;
    }

    /** @return member name */
    public String getName() {
        return name;
    }

    /** @return member email */
    public String getEmail() {
        return email;
    }

    /** @return bcrypt password hash */
    public String getPasswordHash() {
        return passwordHash;
    }

    /** @return member role */
    public UserType getUserType() {
        return userType;
    }

    /** @return available credit balance */
    public double getCreditBalance() {
        return creditBalance;
    }

    /** @return outstanding debit balance */
    public double getDebitBalance() {
        return debitBalance;
    }

    /** @return account creation date */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** @param userId database identifier */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /** @param name member name */
    public void setName(String name) {
        this.name = name;
    }

    /** @param email member email */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @param passwordHash bcrypt password hash */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /** @param userType member role */
    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    /** @param creditBalance available credit balance */
    public void setCreditBalance(double creditBalance) {
        this.creditBalance = creditBalance;
    }

    /** @param debitBalance outstanding debit balance */
    public void setDebitBalance(double debitBalance) {
        this.debitBalance = debitBalance;
    }

    /** @param createdAt account creation date */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /** @return readable user description */
    @Override
    public String toString() {
        return "User{" + "name=" + name + ", email=" + email + ", passwordHash=" + passwordHash + ", userType=" + userType + '}';
    }
    
}
