package com.cmsc.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Wraps jBCrypt so hashing/checking is one line everywhere else in the
 * project. Cost factor 10 matches the seed data hashes in cmsc_schema_FINAL.sql.
 *
 * @author Suhib
 */
public final class PasswordUtil {

    private static final int COST_FACTOR = 10;

    private PasswordUtil() {
    }

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(COST_FACTOR));
    }

    public static boolean verify(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}