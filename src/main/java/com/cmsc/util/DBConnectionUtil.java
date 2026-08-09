package com.cmsc.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Loads {@code db.properties} from the classpath and hands out JDBC
 * connections to {@code cmsc_db}. Every DAO in the project should obtain
 * its connections through this class rather than hard-coding connection
 * details, so the credentials only live in one place.
 *
 * @author Group 3
 */
public final class DBConnectionUtil {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = DBConnectionUtil.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new RuntimeException("db.properties not found on classpath");
            }
            PROPS.load(in);
            Class.forName(PROPS.getProperty("db.driver"));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to initialize DBConnectionUtil", e);
        }
    }

    private DBConnectionUtil() {
        // utility class — no instances
    }

    /**
     * Opens a new connection to cmsc_db using the credentials in
     * db.properties. Caller is responsible for closing it (use
     * try-with-resources).
     *
     * @return an open JDBC Connection
     * @throws SQLException if the connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                PROPS.getProperty("db.url"),
                PROPS.getProperty("db.username"),
                PROPS.getProperty("db.password")
        );
    }
}
