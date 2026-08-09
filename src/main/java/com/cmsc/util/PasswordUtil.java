package com.cmsc.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Wraps jBCrypt so hashing/checking is one line everywhere else in the
 * project. Cost factor 10 matches the seed data hashes in sql/cmsc_db.sql.
 *
 * @author Suhib
 */
public final class PasswordUtil {

    private static final int COST_FACTOR = 10;

    private PasswordUtil() {
    }

    /**
     * Hashes a plain-text password with bcrypt.
     *
     * @param plainPassword password supplied by the member
     * @return bcrypt password hash
     */
    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(COST_FACTOR));
    }

    /**
     * Checks a plain-text password against a stored bcrypt hash.
     *
     * @param plainPassword password supplied by the member
     * @param hashedPassword stored bcrypt hash
     * @return true when the password matches
     */
    public static boolean verify(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
