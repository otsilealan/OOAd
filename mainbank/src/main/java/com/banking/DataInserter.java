package com.banking;

import com.banking.database.DatabaseManager;
import java.sql.*;

public class DataInserter {
    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        insertSampleData();
    }
    
    private static void insertSampleData() {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            
            // Insert customers
            String customerSql = "INSERT INTO customers (name, email, phone) VALUES (?, ?, ?)";
            PreparedStatement customerStmt = conn.prepareStatement(customerSql, Statement.RETURN_GENERATED_KEYS);
            
            String[][] customers = {
                {"John Doe", "john@email.com", "555-0101"},
                {"Jane Smith", "jane@email.com", "555-0102"},
                {"Bob Wilson", "bob@email.com", "555-0103"},
                {"Alice Brown", "alice@email.com", "555-0104"},
                {"Charlie Davis", "charlie@email.com", "555-0105"},
                {"Diana Miller", "diana@email.com", "555-0106"},
                {"Frank Garcia", "frank@email.com", "555-0107"},
                {"Grace Lopez", "grace@email.com", "555-0108"},
                {"Henry Clark", "henry@email.com", "555-0109"},
                {"Ivy Taylor", "ivy@email.com", "555-0110"}
            };
            
            long[] customerIds = new long[10];
            for (int i = 0; i < customers.length; i++) {
                customerStmt.setString(1, customers[i][0]);
                customerStmt.setString(2, customers[i][1]);
                customerStmt.setString(3, customers[i][2]);
                customerStmt.executeUpdate();
                
                ResultSet rs = customerStmt.getGeneratedKeys();
                if (rs.next()) {
                    customerIds[i] = rs.getLong(1);
                }
            }
            
            // Insert accounts
            String accountSql = "INSERT INTO accounts (account_number, customer_id, account_type, balance, interest_rate) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement accountStmt = conn.prepareStatement(accountSql);
            
            String[][] accounts = {
                {"ACC001", "SAVINGS", "1500.00", "0.0250"},
                {"ACC002", "CHEQUE", "2500.00", "0.0000"},
                {"ACC003", "INVESTMENT", "5000.00", "0.0400"},
                {"ACC004", "SAVINGS", "3200.00", "0.0250"},
                {"ACC005", "CHEQUE", "1800.00", "0.0000"},
                {"ACC006", "INVESTMENT", "7500.00", "0.0400"},
                {"ACC007", "SAVINGS", "4100.00", "0.0250"},
                {"ACC008", "CHEQUE", "950.00", "0.0000"},
                {"ACC009", "INVESTMENT", "12000.00", "0.0400"},
                {"ACC010", "SAVINGS", "2750.00", "0.0250"}
            };
            
            for (int i = 0; i < accounts.length; i++) {
                accountStmt.setString(1, accounts[i][0]);
                accountStmt.setLong(2, customerIds[i]);
                accountStmt.setString(3, accounts[i][1]);
                accountStmt.setBigDecimal(4, new java.math.BigDecimal(accounts[i][2]));
                accountStmt.setBigDecimal(5, new java.math.BigDecimal(accounts[i][3]));
                accountStmt.executeUpdate();
            }
            
            conn.commit();
            System.out.println("Successfully inserted 10 records:");
            System.out.println("- 10 customers");
            System.out.println("- 10 accounts");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
