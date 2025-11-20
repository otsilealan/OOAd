# Entity Relationships Documentation

## Overview
This document describes how User, Customer, and Account entities are properly referenced in the banking system.

## Entity Relationship Diagram

```
User (1) ----< (0..1) Customer (1) ----< (*) Account
```

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(20) DEFAULT 'USER'
)
```

### Customers Table
```sql
CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,                          -- Foreign Key to users.id
    first_name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
)
```

### Accounts Table
```sql
CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL,             -- Foreign Key to customers.id
    account_type VARCHAR(20) NOT NULL,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    interest_rate DECIMAL(5,4),
    branch VARCHAR(100),
    date_opened TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
)
```

## Relationships

### User → Customer (One-to-One Optional)
- A User can have zero or one Customer profile
- A Customer must belong to a User (optional, can be null for walk-in customers)
- Linked via `customer.user_id → user.id`
- Cascade delete: When a User is deleted, their Customer profile is also deleted

### Customer → Account (One-to-Many)
- A Customer can have multiple Accounts
- An Account must belong to exactly one Customer
- Linked via `account.customer_id → customer.id`
- Cascade delete: When a Customer is deleted, all their Accounts are also deleted

## Model Classes

### User Model
```java
public class User {
    private Long userId;           // Primary key
    private String username;
    private String password;
    private String email;
    private String role;           // ADMIN or USER
}
```

### Customer Model
```java
public class Customer {
    private Long customerId;       // Primary key
    private Long userId;           // Foreign key to User
    private String firstName;
    private String surname;
    private String address;
    private String phoneNumber;
    private String email;
    private List<Account> accounts; // In-memory relationship
}
```

### Account Model
```java
public abstract class Account {
    protected String accountNumber; // Primary key
    protected double balance;
    protected String branch;
    protected Customer customer;    // Foreign key reference
    protected LocalDateTime dateOpened;
}
```

## DAO Methods for Referencing

### CustomerDAO
- `save(Customer)` - Saves customer with user_id reference
- `findById(Long)` - Retrieves customer by ID
- `findByUserId(Long)` - Retrieves customer by user_id
- `findAll()` - Retrieves all customers with user_id populated

### AccountDAO
- `save(Account)` - Saves account with customer_id reference
- `findByCustomerId(Long)` - Retrieves all accounts for a customer
- `findByAccountNumber(String)` - Retrieves account with full customer details
- `updateBalance(String, double)` - Updates account balance

## Data Integrity Rules

1. **Referential Integrity**: All foreign keys are enforced at database level
2. **Cascade Deletes**: Deleting a User cascades to Customer and then to Accounts
3. **Not Null Constraints**: customer_id in accounts is NOT NULL (account must have owner)
4. **Unique Constraints**: account_number must be unique across all accounts

## Usage Examples

### Creating a User with Customer and Account
```java
// 1. Create and save User
User user = new User("john_doe", "password", "john@email.com");
userDAO.save(user); // user.userId is now set

// 2. Create and save Customer linked to User
Customer customer = new Customer("1", "John", "Doe", "123 St", "1234567890", "john@email.com");
customer.setUserId(user.getUserId());
customerDAO.save(customer); // customer.customerId is now set

// 3. Create and save Account linked to Customer
Account account = new SavingsAccount("ACC001", "Main Branch", customer);
accountDAO.save(account); // Saves with customer.customerId
```

### Loading Account with Full References
```java
// Load account by account number
Account account = accountDAO.findByAccountNumber("ACC001");
// account.getCustomer() returns full Customer object
// account.getCustomer().getUserId() returns the linked User ID
```

### Loading All Accounts for a Customer
```java
List<Account> accounts = accountDAO.findByCustomerId(customerId);
// Each account has full customer reference populated
```

## Transaction Workflow

When performing transactions (deposit/withdraw):
1. Lookup account by account number using `AccountDAO.findByAccountNumber()`
2. Account object includes full Customer reference
3. Perform transaction using account methods
4. Update balance in database using `AccountDAO.updateBalance()`
5. Customer and User references remain intact throughout

## Benefits of This Structure

1. **Data Integrity**: Foreign keys ensure valid references
2. **Cascade Operations**: Automatic cleanup of related data
3. **Efficient Queries**: JOIN operations retrieve related data in single query
4. **Type Safety**: Strong typing in Java models prevents invalid references
5. **Separation of Concerns**: Clear boundaries between User (authentication), Customer (profile), and Account (banking)
