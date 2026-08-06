/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cmsc.dao;

import com.cmsc.model.User;




/**
 *
 * @author DELL
 */
public interface UserDAO extends GenericDAO<User> {
    
    /**
 * DAO contract for User persistence. Extends GenericDAO's CRUD operations
 * and adds findByEmail, which login/registration need but the generic
 * contract has no reason to know about.
 *
 * @author Suhib
     * @param email
     * @return 
     * @throws java.sql.SQLException
 */
    
    User findByEmail(String email) ;
    
    
}
