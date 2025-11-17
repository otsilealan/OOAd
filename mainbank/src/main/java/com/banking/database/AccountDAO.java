package com.banking.database;

import com.banking.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    
    public void save(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (account_number, customer_id, account_type, balance, interest_rate) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, account.getAccountNumber());
            stmt.setLong(2, account.getCustomer().getCustomerId());
            
            // Set account type based on class
            String accountType = "SAVINGS";
            if (account instanceof InvestmentAccount) {
                accountType = "INVESTMENT";
            } else if (account instanceof ChequeAccount) {
                accountType = "CHEQUE";
            }
            stmt.setString(3, accountType);
            
            stmt.setBigDecimal(4, java.math.BigDecimal.valueOf(account.getBalance()));
            
            // Set interest rate
            if (account instanceof SavingsAccount) {
                stmt.setBigDecimal(5, java.math.BigDecimal.valueOf(((SavingsAccount) account).getInterestRate()));
            } else if (account instanceof InvestmentAccount) {
                stmt.setBigDecimal(5, java.math.BigDecimal.valueOf(0.04)); // 4% for investment
            } else {
                stmt.setBigDecimal(5, java.math.BigDecimal.valueOf(0.0)); // 0% for cheque
            }
            
            stmt.executeUpdate();
        }
    }
    
    public List<Account> findByCustomerId(Long customerId) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Note: This is simplified - in a real app you'd need to reconstruct the full Account objects
                    // For now, we'll just return the basic info
                }
            }
        }
        return accounts;
    }
    
    public void updateBalance(String accountNumber, double newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, java.math.BigDecimal.valueOf(newBalance));
            stmt.setString(2, accountNumber);
            stmt.executeUpdate();
        }
    }
}
