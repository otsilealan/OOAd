package com.banking;

import com.banking.database.DatabaseManager;
import java.sql.*;

public class DataVerifier {
    public static void main(String[] args) {
        try (Connection conn = DatabaseManager.getConnection()) {
            System.out.println("=== BANKING SYSTEM DATA VERIFICATION ===\n");
            
            // Count customers
            String customerCountSql = "SELECT COUNT(*) FROM customers";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(customerCountSql)) {
                if (rs.next()) {
                    System.out.println("Total Customers: " + rs.getInt(1));
                }
            }
            
            // Count accounts
            String accountCountSql = "SELECT COUNT(*) FROM accounts";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(accountCountSql)) {
                if (rs.next()) {
                    System.out.println("Total Accounts: " + rs.getInt(1));
                }
            }
            
            System.out.println("\n=== CUSTOMER DETAILS ===");
            String customerSql = "SELECT id, name, email, phone FROM customers ORDER BY id";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(customerSql)) {
                while (rs.next()) {
                    System.out.printf("ID: %d | Name: %s | Email: %s | Phone: %s%n",
                        rs.getLong("id"), rs.getString("name"), 
                        rs.getString("email"), rs.getString("phone"));
                }
            }
            
            System.out.println("\n=== ACCOUNT DETAILS ===");
            String accountSql = "SELECT a.account_number, c.name, a.account_type, a.balance FROM accounts a JOIN customers c ON a.customer_id = c.id ORDER BY a.id";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(accountSql)) {
                while (rs.next()) {
                    System.out.printf("Account: %s | Customer: %s | Type: %s | Balance: $%.2f%n",
                        rs.getString("account_number"), rs.getString("name"),
                        rs.getString("account_type"), rs.getBigDecimal("balance"));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
