package com.banking.util;

import com.banking.database.DatabaseManager;
import java.sql.*;

public class SimpleAnalyzer {
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("DATABASE ANALYSIS");
        System.out.println("=".repeat(80));
        
        try (Connection conn = DatabaseManager.getConnection()) {
            
            // Show all tables
            System.out.println("\n[TABLES]");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES");
            while (rs.next()) {
                System.out.println("  - " + rs.getString(1));
            }
            
            // Analyze USERS table
            System.out.println("\n[USERS TABLE]");
            rs = stmt.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'USERS'");
            while (rs.next()) {
                System.out.println("  " + rs.getString("COLUMN_NAME") + " - " + rs.getString("DATA_TYPE"));
            }
            
            // Analyze CUSTOMERS table
            System.out.println("\n[CUSTOMERS TABLE]");
            rs = stmt.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'CUSTOMERS'");
            while (rs.next()) {
                System.out.println("  " + rs.getString("COLUMN_NAME") + " - " + rs.getString("DATA_TYPE"));
            }
            
            // Analyze ACCOUNTS table
            System.out.println("\n[ACCOUNTS TABLE]");
            rs = stmt.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'ACCOUNTS'");
            while (rs.next()) {
                System.out.println("  " + rs.getString("COLUMN_NAME") + " - " + rs.getString("DATA_TYPE"));
            }
            
            // Data counts
            System.out.println("\n[DATA COUNTS]");
            rs = stmt.executeQuery("SELECT COUNT(*) FROM USERS");
            rs.next();
            System.out.println("  Users: " + rs.getInt(1));
            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM CUSTOMERS");
            rs.next();
            System.out.println("  Customers: " + rs.getInt(1));
            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM ACCOUNTS");
            rs.next();
            System.out.println("  Accounts: " + rs.getInt(1));
            
            // Sample data
            System.out.println("\n[SAMPLE CUSTOMERS]");
            rs = stmt.executeQuery("SELECT * FROM CUSTOMERS LIMIT 3");
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println();
            System.out.println("-".repeat(80));
            
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
            
            System.out.println("\n" + "=".repeat(80));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
