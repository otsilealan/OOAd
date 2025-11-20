package com.banking.database;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:h2:./data/banking;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }
    
    public static void initializeDatabase() {
        try (Connection conn = getConnection()) {
            createTables(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void createTables(Connection conn) throws SQLException {
        String userTable = "CREATE TABLE IF NOT EXISTS users (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
            "username VARCHAR(50) UNIQUE NOT NULL," +
            "password VARCHAR(255) NOT NULL," +
            "email VARCHAR(255) UNIQUE NOT NULL," +
            "role VARCHAR(20) DEFAULT 'USER'" +
            ")";
            
        String customerTable = "CREATE TABLE IF NOT EXISTS customers (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
            "user_id BIGINT," +
            "first_name VARCHAR(100) NOT NULL," +
            "surname VARCHAR(100) NOT NULL," +
            "address VARCHAR(255)," +
            "phone VARCHAR(20)," +
            "email VARCHAR(255) NOT NULL," +
            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
            ")";
            
        String accountTable = "CREATE TABLE IF NOT EXISTS accounts (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
            "account_number VARCHAR(20) UNIQUE NOT NULL," +
            "customer_id BIGINT NOT NULL," +
            "account_type VARCHAR(20) NOT NULL," +
            "balance DECIMAL(15,2) NOT NULL DEFAULT 0.00," +
            "interest_rate DECIMAL(5,4)," +
            "branch VARCHAR(100)," +
            "date_opened TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE" +
            ")";
            
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(userTable);
            stmt.execute(customerTable);
            stmt.execute(accountTable);
        }
    }
}
