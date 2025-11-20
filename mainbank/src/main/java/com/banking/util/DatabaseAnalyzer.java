package com.banking.util;

import com.banking.database.DatabaseManager;
import java.sql.*;

public class DatabaseAnalyzer {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("DATABASE ANALYSIS REPORT");
        System.out.println("=".repeat(80));
        
        try (Connection conn = DatabaseManager.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            
            System.out.println("\n[DATABASE INFO]");
            System.out.println("Database: " + metaData.getDatabaseProductName());
            System.out.println("Version: " + metaData.getDatabaseProductVersion());
            System.out.println("URL: " + metaData.getURL());
            
            analyzeTables(conn);
            analyzeData(conn);
            analyzeRelationships(conn);
            
        } catch (SQLException e) {
            System.err.println("Error analyzing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void analyzeTables(Connection conn) throws SQLException {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("[TABLE STRUCTURES]");
        System.out.println("=".repeat(80));
        
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
        
        while (tables.next()) {
            String tableName = tables.getString("TABLE_NAME");
            if (tableName.equalsIgnoreCase("USERS") || 
                tableName.equalsIgnoreCase("CUSTOMERS") || 
                tableName.equalsIgnoreCase("ACCOUNTS")) {
                
                System.out.println("\nTable: " + tableName);
                System.out.println("-".repeat(80));
                
                // Get columns
                ResultSet columns = metaData.getColumns(null, null, tableName, null);
                System.out.printf("%-25s %-20s %-10s %-10s%n", "COLUMN", "TYPE", "NULLABLE", "DEFAULT");
                System.out.println("-".repeat(80));
                
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    int columnSize = columns.getInt("COLUMN_SIZE");
                    String nullable = columns.getString("IS_NULLABLE");
                    String defaultVal = columns.getString("COLUMN_DEF");
                    
                    System.out.printf("%-25s %-20s %-10s %-10s%n", 
                        columnName, 
                        columnType + "(" + columnSize + ")",
                        nullable,
                        defaultVal != null ? defaultVal : "NULL"
                    );
                }
                columns.close();
                
                // Get primary keys
                ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);
                System.out.println("\nPrimary Keys:");
                while (primaryKeys.next()) {
                    System.out.println("  - " + primaryKeys.getString("COLUMN_NAME"));
                }
                primaryKeys.close();
                
                // Get foreign keys
                ResultSet foreignKeys = metaData.getImportedKeys(null, null, tableName);
                System.out.println("\nForeign Keys:");
                while (foreignKeys.next()) {
                    String fkColumn = foreignKeys.getString("FKCOLUMN_NAME");
                    String pkTable = foreignKeys.getString("PKTABLE_NAME");
                    String pkColumn = foreignKeys.getString("PKCOLUMN_NAME");
                    System.out.println("  - " + fkColumn + " -> " + pkTable + "(" + pkColumn + ")");
                }
                foreignKeys.close();
            }
        }
        tables.close();
    }
    
    private static void analyzeData(Connection conn) throws SQLException {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("[DATA STATISTICS]");
        System.out.println("=".repeat(80));
        
        // Users
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM users");
            if (rs.next()) {
                System.out.println("\nUsers: " + rs.getInt("count"));
            }
            
            rs = stmt.executeQuery("SELECT role, COUNT(*) as count FROM users GROUP BY role");
            System.out.println("  By Role:");
            while (rs.next()) {
                System.out.println("    - " + rs.getString("role") + ": " + rs.getInt("count"));
            }
        }
        
        // Customers
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM customers");
            if (rs.next()) {
                System.out.println("\nCustomers: " + rs.getInt("count"));
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM customers WHERE user_id IS NOT NULL");
            if (rs.next()) {
                System.out.println("  Linked to Users: " + rs.getInt("count"));
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM customers WHERE user_id IS NULL");
            if (rs.next()) {
                System.out.println("  Without User Link: " + rs.getInt("count"));
            }
        }
        
        // Accounts
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM accounts");
            if (rs.next()) {
                System.out.println("\nAccounts: " + rs.getInt("count"));
            }
            
            rs = stmt.executeQuery("SELECT account_type, COUNT(*) as count FROM accounts GROUP BY account_type");
            System.out.println("  By Type:");
            while (rs.next()) {
                System.out.println("    - " + rs.getString("account_type") + ": " + rs.getInt("count"));
            }
            
            rs = stmt.executeQuery("SELECT SUM(balance) as total FROM accounts");
            if (rs.next()) {
                System.out.println("  Total Balance: BWP " + String.format("%.2f", rs.getDouble("total")));
            }
            
            rs = stmt.executeQuery("SELECT AVG(balance) as avg FROM accounts");
            if (rs.next()) {
                System.out.println("  Average Balance: BWP " + String.format("%.2f", rs.getDouble("avg")));
            }
        }
    }
    
    private static void analyzeRelationships(Connection conn) throws SQLException {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("[RELATIONSHIP INTEGRITY]");
        System.out.println("=".repeat(80));
        
        // Check orphaned customers
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(
                "SELECT COUNT(*) as count FROM customers c " +
                "WHERE c.user_id IS NOT NULL AND c.user_id NOT IN (SELECT id FROM users)"
            );
            if (rs.next()) {
                int orphaned = rs.getInt("count");
                System.out.println("\nOrphaned Customers (user_id not in users): " + orphaned);
                if (orphaned > 0) {
                    System.out.println("  ⚠ WARNING: Data integrity issue detected!");
                } else {
                    System.out.println("  ✓ OK");
                }
            }
        }
        
        // Check orphaned accounts
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(
                "SELECT COUNT(*) as count FROM accounts a " +
                "WHERE a.customer_id NOT IN (SELECT id FROM customers)"
            );
            if (rs.next()) {
                int orphaned = rs.getInt("count");
                System.out.println("\nOrphaned Accounts (customer_id not in customers): " + orphaned);
                if (orphaned > 0) {
                    System.out.println("  ⚠ WARNING: Data integrity issue detected!");
                } else {
                    System.out.println("  ✓ OK");
                }
            }
        }
        
        // Check accounts per customer
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(
                "SELECT c.first_name || ' ' || c.surname as name, COUNT(a.id) as account_count " +
                "FROM customers c LEFT JOIN accounts a ON c.id = a.customer_id " +
                "GROUP BY c.id, c.first_name, c.surname " +
                "ORDER BY account_count DESC"
            );
            System.out.println("\nAccounts per Customer:");
            int count = 0;
            while (rs.next() && count < 10) {
                System.out.println("  - " + rs.getString("name") + ": " + rs.getInt("account_count") + " account(s)");
                count++;
            }
            if (count == 0) {
                System.out.println("  No customers found");
            }
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("END OF REPORT");
        System.out.println("=".repeat(80));
    }
}
