package com.banking.database;

import com.banking.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    
    public void save(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (user_id, first_name, surname, address, phone, email, name) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setObject(1, customer.getUserId(), Types.BIGINT);
            stmt.setString(2, customer.getFirstName());
            stmt.setString(3, customer.getSurname());
            stmt.setString(4, customer.getAddress());
            stmt.setString(5, customer.getPhoneNumber());
            stmt.setString(6, customer.getEmail());
            stmt.setString(7, customer.getName()); // Also populate NAME for backward compatibility
            stmt.executeUpdate();
            
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    customer.setCustomerId(keys.getLong(1));
                }
            }
        }
    }
    
    public List<Customer> findAll() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Customer customer = new Customer(
                    String.valueOf(rs.getLong("id")),
                    rs.getString("first_name"),
                    rs.getString("surname"),
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getString("email")
                );
                customer.setUserId(rs.getLong("user_id"));
                customers.add(customer);
            }
        }
        return customers;
    }
    
    public Customer findById(Long id) throws SQLException {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer(
                        String.valueOf(rs.getLong("id")),
                        rs.getString("first_name"),
                        rs.getString("surname"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email")
                    );
                    customer.setUserId(rs.getLong("user_id"));
                    return customer;
                }
            }
        }
        return null;
    }
    
    public Customer findByUserId(Long userId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer(
                        String.valueOf(rs.getLong("id")),
                        rs.getString("first_name"),
                        rs.getString("surname"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email")
                    );
                    customer.setUserId(userId);
                    return customer;
                }
            }
        }
        return null;
    }
    
    public void delete(Long customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, customerId);
            stmt.executeUpdate();
        }
    }
    
    public void update(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET first_name = ?, surname = ?, email = ?, phone = ?, address = ?, name = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getSurname());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPhoneNumber());
            stmt.setString(5, customer.getAddress());
            stmt.setString(6, customer.getName());
            stmt.setLong(7, customer.getCustomerId());
            stmt.executeUpdate();
        }
    }
}
