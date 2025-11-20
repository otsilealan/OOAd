package com.banking.util;

import com.banking.database.DatabaseManager;
import java.sql.*;

public class FixNameColumn {
    public static void main(String[] args) {
        System.out.println("Fixing NAME column in CUSTOMERS table...");
        
        try (Connection conn = DatabaseManager.getConnection()) {
            Statement stmt = conn.createStatement();
            
            // Option 1: Make NAME nullable
            System.out.println("Making NAME column nullable...");
            stmt.execute("ALTER TABLE CUSTOMERS ALTER COLUMN name SET NULL");
            
            // Option 2: Set default value for existing constraint
            System.out.println("Setting default value for NAME column...");
            stmt.execute("ALTER TABLE CUSTOMERS ALTER COLUMN name SET DEFAULT ''");
            
            System.out.println("✓ Fixed successfully!");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
