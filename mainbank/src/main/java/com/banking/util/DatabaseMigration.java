package com.banking.util;

import com.banking.database.DatabaseManager;
import java.sql.*;

public class DatabaseMigration {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("DATABASE MIGRATION UTILITY");
        System.out.println("=".repeat(80));
        
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            
            System.out.println("\n[STEP 1] Adding new columns to CUSTOMERS table...");
            migrateCustomersTable(conn);
            
            System.out.println("\n[STEP 2] Adding new columns to ACCOUNTS table...");
            migrateAccountsTable(conn);
            
            System.out.println("\n[STEP 3] Cleaning up USERS table...");
            cleanupUsersTable(conn);
            
            conn.commit();
            System.out.println("\n✓ Migration completed successfully!");
            System.out.println("=".repeat(80));
            
        } catch (Exception e) {
            System.err.println("\n❌ Migration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void migrateCustomersTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        
        // Check if columns already exist
        ResultSet rs = stmt.executeQuery(
            "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
            "WHERE TABLE_NAME = 'CUSTOMERS' AND COLUMN_NAME = 'USER_ID'"
        );
        
        if (!rs.next()) {
            System.out.println("  - Adding USER_ID column...");
            stmt.execute("ALTER TABLE CUSTOMERS ADD COLUMN user_id BIGINT");
        } else {
            System.out.println("  - USER_ID column already exists");
        }
        
        rs = stmt.executeQuery(
            "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
            "WHERE TABLE_NAME = 'CUSTOMERS' AND COLUMN_NAME = 'FIRST_NAME'"
        );
        
        if (!rs.next()) {
            System.out.println("  - Adding FIRST_NAME and SURNAME columns...");
            stmt.execute("ALTER TABLE CUSTOMERS ADD COLUMN first_name VARCHAR(100)");
            stmt.execute("ALTER TABLE CUSTOMERS ADD COLUMN surname VARCHAR(100)");
            
            System.out.println("  - Migrating NAME data to FIRST_NAME and SURNAME...");
            // Split existing NAME into first_name and surname
            stmt.execute(
                "UPDATE CUSTOMERS SET " +
                "first_name = CASE " +
                "  WHEN LOCATE(' ', name) > 0 THEN SUBSTRING(name, 1, LOCATE(' ', name) - 1) " +
                "  ELSE name " +
                "END, " +
                "surname = CASE " +
                "  WHEN LOCATE(' ', name) > 0 THEN SUBSTRING(name, LOCATE(' ', name) + 1) " +
                "  ELSE '' " +
                "END"
            );
        } else {
            System.out.println("  - FIRST_NAME and SURNAME columns already exist");
        }
        
        rs = stmt.executeQuery(
            "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
            "WHERE TABLE_NAME = 'CUSTOMERS' AND COLUMN_NAME = 'ADDRESS'"
        );
        
        if (!rs.next()) {
            System.out.println("  - Adding ADDRESS column...");
            stmt.execute("ALTER TABLE CUSTOMERS ADD COLUMN address VARCHAR(255)");
        } else {
            System.out.println("  - ADDRESS column already exists");
        }
        
        // Add foreign key if not exists
        try {
            System.out.println("  - Adding foreign key constraint...");
            stmt.execute(
                "ALTER TABLE CUSTOMERS ADD CONSTRAINT fk_customer_user " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
            );
        } catch (SQLException e) {
            if (e.getMessage().contains("already exists")) {
                System.out.println("  - Foreign key constraint already exists");
            } else {
                throw e;
            }
        }
    }
    
    private static void migrateAccountsTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        
        ResultSet rs = stmt.executeQuery(
            "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
            "WHERE TABLE_NAME = 'ACCOUNTS' AND COLUMN_NAME = 'BRANCH'"
        );
        
        if (!rs.next()) {
            System.out.println("  - Adding BRANCH column...");
            stmt.execute("ALTER TABLE ACCOUNTS ADD COLUMN branch VARCHAR(100) DEFAULT 'Main Branch'");
            stmt.execute("UPDATE ACCOUNTS SET branch = 'Main Branch' WHERE branch IS NULL");
        } else {
            System.out.println("  - BRANCH column already exists");
        }
        
        rs = stmt.executeQuery(
            "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
            "WHERE TABLE_NAME = 'ACCOUNTS' AND COLUMN_NAME = 'DATE_OPENED'"
        );
        
        if (!rs.next()) {
            System.out.println("  - Adding DATE_OPENED column...");
            stmt.execute("ALTER TABLE ACCOUNTS ADD COLUMN date_opened TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
            stmt.execute("UPDATE ACCOUNTS SET date_opened = CURRENT_TIMESTAMP WHERE date_opened IS NULL");
        } else {
            System.out.println("  - DATE_OPENED column already exists");
        }
    }
    
    private static void cleanupUsersTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        
        // Check and drop legacy columns
        String[] legacyColumns = {"USER_NAME", "IS_ADMIN", "REMARKS"};
        
        for (String column : legacyColumns) {
            ResultSet rs = stmt.executeQuery(
                "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_NAME = 'USERS' AND COLUMN_NAME = '" + column + "'"
            );
            
            if (rs.next()) {
                System.out.println("  - Dropping legacy column: " + column);
                try {
                    stmt.execute("ALTER TABLE USERS DROP COLUMN " + column);
                } catch (SQLException e) {
                    System.out.println("    Warning: Could not drop " + column + ": " + e.getMessage());
                }
            }
        }
    }
}
