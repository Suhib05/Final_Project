package com.cmsc.dao;

import com.cmsc.model.User;

/**
 * Defines persistence operations for registered users. The email lookup
 * supports registration and authentication in addition to the shared CRUD
 * operations.
 *
 * @author Suhib
 */
public interface UserDAO extends GenericDAO<User> {

    /**
     * Finds a user by unique email address.
     *
     * @param email email address
     * @return matching user, or null when absent
     */
    User findByEmail(String email);
}
