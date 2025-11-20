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
        String sql = "SELECT a.*, c.* FROM accounts a " +
                    "JOIN customers c ON a.customer_id = c.id " +
                    "WHERE a.customer_id = ?";
        
        CustomerDAO customerDAO = new CustomerDAO();
        Customer customer = customerDAO.findById(customerId);
        
        if (customer == null) return accounts;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String accountType = rs.getString("account_type");
                    String accountNumber = rs.getString("account_number");
                    String branch = rs.getString("branch");
                    double balance = rs.getDouble("balance");
                    
                    Account account = null;
                    if ("SAVINGS".equals(accountType)) {
                        account = new SavingsAccount(accountNumber, branch, customer);
                    } else if ("INVESTMENT".equals(accountType)) {
                        account = new InvestmentAccount(accountNumber, branch, customer, balance);
                    } else if ("CHEQUE".equals(accountType)) {
                        account = new ChequeAccount(accountNumber, branch, customer, "", "");
                    }
                    
                    if (account != null) {
                        account.setBalance(balance);
                        accounts.add(account);
                    }
                }
            }
        }
        return accounts;
    }
    
    public Account findByAccountNumber(String accountNumber) throws SQLException {
        String sql = "SELECT a.*, c.id as customer_id, c.first_name, c.surname, c.address, c.phone, c.email, c.user_id " +
                    "FROM accounts a " +
                    "JOIN customers c ON a.customer_id = c.id " +
                    "WHERE a.account_number = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer(
                        String.valueOf(rs.getLong("customer_id")),
                        rs.getString("first_name"),
                        rs.getString("surname"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email")
                    );
                    customer.setUserId(rs.getLong("user_id"));
                    
                    String accountType = rs.getString("account_type");
                    String branch = rs.getString("branch");
                    double balance = rs.getDouble("balance");
                    
                    Account account = null;
                    if ("SAVINGS".equals(accountType)) {
                        account = new SavingsAccount(accountNumber, branch, customer);
                    } else if ("INVESTMENT".equals(accountType)) {
                        account = new InvestmentAccount(accountNumber, branch, customer, balance);
                    } else if ("CHEQUE".equals(accountType)) {
                        account = new ChequeAccount(accountNumber, branch, customer, "", "");
                    }
                    
                    if (account != null) {
                        account.setBalance(balance);
                    }
                    return account;
                }
            }
        }
        return null;
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
    
    public void delete(String accountNumber) throws SQLException {
        String sql = "DELETE FROM accounts WHERE account_number = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            stmt.executeUpdate();
        }
    }
    
    public List<Account> findAll() throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT a.*, c.* FROM accounts a " +
                    "JOIN customers c ON a.customer_id = c.id";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Customer customer = new Customer(
                    String.valueOf(rs.getLong("c.id")),
                    rs.getString("first_name"),
                    rs.getString("surname"),
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getString("email")
                );
                customer.setUserId(rs.getLong("user_id"));
                
                String accountNumber = rs.getString("account_number");
                String accountType = rs.getString("account_type");
                String branch = rs.getString("branch");
                double balance = rs.getDouble("balance");
                
                Account account = null;
                if ("SAVINGS".equals(accountType)) {
                    account = new SavingsAccount(accountNumber, branch, customer);
                } else if ("INVESTMENT".equals(accountType)) {
                    account = new InvestmentAccount(accountNumber, branch, customer, balance);
                } else if ("CHEQUE".equals(accountType)) {
                    account = new ChequeAccount(accountNumber, branch, customer, "", "");
                }
                
                if (account != null) {
                    account.setBalance(balance);
                    accounts.add(account);
                }
            }
        }
        return accounts;
    }
}
