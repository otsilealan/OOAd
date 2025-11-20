package com.banking.util;

import com.banking.database.DatabaseManager;
import java.sql.*;

public class LinkUserToCustomer {
    public static void main(String[] args) {
        System.out.println("Linking users to customers by email...");
        
        try (Connection conn = DatabaseManager.getConnection()) {
            // Link users to customers where emails match
            String sql = "UPDATE customers c SET user_id = (SELECT id FROM users u WHERE u.email = c.email) WHERE user_id IS NULL";
            Statement stmt = conn.createStatement();
            int updated = stmt.executeUpdate(sql);
            
            System.out.println("✓ Linked " + updated + " customers to users");
            
            // Show results
            ResultSet rs = stmt.executeQuery(
                "SELECT u.username, c.first_name, c.surname, c.email " +
                "FROM customers c JOIN users u ON c.user_id = u.id"
            );
            
            System.out.println("\nLinked Users:");
            while (rs.next()) {
                System.out.println("  - User: " + rs.getString("username") + 
                                 " -> Customer: " + rs.getString("first_name") + " " + rs.getString("surname") +
                                 " (" + rs.getString("email") + ")");
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
