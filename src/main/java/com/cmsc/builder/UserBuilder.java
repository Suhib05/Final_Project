package com.cmsc.builder;

import com.cmsc.model.User;
import com.cmsc.model.UserType;

/**
 * Builder pattern for constructing User objects. Used during registration,
 * where a User is assembled from several individually-collected form
 * fields — a natural fit for Builder, since it lets the construction
 * happen step-by-step and reads clearly at the call site
 * (new UserBuilder().setName(...).setEmail(...).build()).
 *
 * @author Suhib
 */
public class UserBuilder {

    private String name;
    private String email;
    private String passwordHash;
    private UserType userType = UserType.USER;

    public UserBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        return this;
    }

    public UserBuilder setUserType(UserType userType) {
        this.userType = userType;
        return this;
    }

    /**
     * Assembles the final User. Throws if a required field was never
     * set, so a caller can't accidentally build a broken/incomplete User.
     */
    public User build() {
        if (name == null || email == null || passwordHash == null) {
            throw new IllegalStateException(
                    "Cannot build User: name, email, and passwordHash are required.");
        }
        return new User(name, email, passwordHash, userType);
    }
}