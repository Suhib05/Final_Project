/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cmsc.model;

import java.time.LocalDateTime;

/**
 *
 * @author DELL
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
    
    public User(){
        
    }

    public User(String name, String email, String passwordHash, UserType userType) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.userType = userType;
        this.creditBalance = 0.0;
        this.debitBalance = 0.0;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserType getUserType() {
        return userType;
    }

    public double getCreditBalance() {
        return creditBalance;
    }

    public double getDebitBalance() {
        return debitBalance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public void setCreditBalance(double creditBalance) {
        this.creditBalance = creditBalance;
    }

    public void setDebitBalance(double debitBalance) {
        this.debitBalance = debitBalance;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" + "name=" + name + ", email=" + email + ", passwordHash=" + passwordHash + ", userType=" + userType + '}';
    }
    
    
    
    
    
    
    
}
