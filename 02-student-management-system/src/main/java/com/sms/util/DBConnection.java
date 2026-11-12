package com.sms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/student_management_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASS = "password";

    private static final String SQLITE_URL = "jdbc:sqlite:student_management.db";
    private static boolean useSqliteFallback = false;

    public static Connection getConnection() throws SQLException {
        if (useSqliteFallback) {
            return getSqliteConnection();
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASS);
            initSchemaIfRequired(conn, false);
            return conn;
        } catch (Exception ex) {
            // Fallback to embedded SQLite for instant zero-config execution
            useSqliteFallback = true;
            System.out.println("[INFO] MySQL connection failed (" + ex.getMessage() + "). Switching to zero-config portable SQLite engine.");
            return getSqliteConnection();
        }
    }

    private static Connection getSqliteConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(SQLITE_URL);
            initSchemaIfRequired(conn, true);
            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC Driver not found in Classpath!", e);
        }
    }

    private static void initSchemaIfRequired(Connection conn, boolean isSqlite) {
        try (Statement st = conn.createStatement()) {
            if (isSqlite) {
                st.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE NOT NULL, password TEXT NOT NULL, role TEXT DEFAULT 'ADMIN');");
                st.execute("CREATE TABLE IF NOT EXISTS students (id INTEGER PRIMARY KEY AUTOINCREMENT, roll_no TEXT UNIQUE NOT NULL, name TEXT NOT NULL, course TEXT NOT NULL, semester INTEGER NOT NULL, email TEXT, contact TEXT, address TEXT, fee_status TEXT DEFAULT 'PAID');");
                st.execute("INSERT OR IGNORE INTO users (username, password, role) VALUES ('admin', 'admin123', 'ADMIN');");
                
                // Seed initial students if table empty
                var rs = st.executeQuery("SELECT COUNT(*) FROM students;");
                if (rs.next() && rs.getInt(1) == 0) {
                    st.execute("INSERT INTO students (roll_no, name, course, semester, email, contact, address, fee_status) VALUES " +
                            "('2026BCA001', 'Aarav Sharma', 'BCA', 3, 'aarav.sharma@email.com', '+91 9876543210', 'Indira Nagar, Lucknow', 'PAID'), " +
                            "('2026BCA002', 'Rohan Verma', 'BCA', 3, 'rohan.verma@email.com', '+91 9123456789', 'Gomti Nagar, Lucknow', 'PAID'), " +
                            "('2026BCA003', 'Kunal Patel', 'BCA', 3, 'kunal.patel@email.com', '+91 9988776655', 'Aliganj, Lucknow', 'DUE'), " +
                            "('2026BCA004', 'Ananya Singh', 'BCA', 3, 'ananya.singh@email.com', '+91 9453193268', 'Tedhi Puliya, Lucknow', 'PAID');");
                }
            }
        } catch (SQLException ignored) {}
    }
}
