package com.cmsc;

import com.cmsc.util.DBConnectionUtil;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Run this test first, before writing any DAO or servlet code, to confirm
 * MySQL is running, cmsc_db exists, and the cst8288/cst8288 credentials work.
 *
 * In NetBeans: right-click this file -> Test File (or Run -> Test Project).
 *
 * @author TODO-your-name
 */
class DBConnectionTest {

    @Test
    void canConnectToDatabase() throws Exception {
        try (Connection conn = DBConnectionUtil.getConnection()) {
            assertNotNull(conn, "Connection should not be null");
            assertFalse(conn.isClosed(), "Connection should be open");
            System.out.println("Connected successfully to: " + conn.getCatalog());
        }
    }
}
