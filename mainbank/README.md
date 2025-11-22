# Banking System with JavaFX GUI and H2 Database

This is a Java-based banking system that demonstrates Object-Oriented Programming principles with a JavaFX graphical user interface and H2 database integration.

## Features

- **User Authentication**: Login/Signup system with secure user management
- **JavaFX GUI**: Modern graphical user interface for easy interaction
- **H2 Database**: Embedded database for persistent data storage
- **Customer Management**: Add and view customers
- **Account Management**: Create and manage different account types (Savings, Investment, Cheque)
- **OOP Principles**: Demonstrates inheritance, polymorphism, abstraction, and encapsulation

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

**Windows Users**: See [WINDOWS_SETUP.md](WINDOWS_SETUP.md) for detailed Windows installation and setup instructions.

## Running the Application

### GUI Mode (Default)
```bash
mvn javafx:run
```

### Console Mode
```bash
mvn compile exec:java -Dexec.mainClass="com.banking.BankingSystemApp" -Dexec.args="--console"
```

### Alternative GUI Launch
```bash
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -cp target/classes com.banking.gui.BankingApp
```

## Project Structure

```
src/main/java/com/banking/
├── gui/                    # JavaFX GUI components
│   ├── BankingApp.java    # Main JavaFX application
│   ├── LoginPane.java     # Login/Signup interface
│   ├── MainBankingPane.java # Main application interface
│   ├── CustomerManagementPane.java # Customer management
│   └── AccountManagementPane.java  # Account management
├── database/              # Database layer
│   ├── DatabaseManager.java
│   ├── UserDAO.java       # User authentication
│   ├── CustomerDAO.java   # Customer operations
│   └── AccountDAO.java    # Account operations
├── model/                 # Domain models
│   ├── User.java          # User authentication model
│   ├── Account.java
│   ├── Customer.java
│   ├── SavingsAccount.java
│   ├── InvestmentAccount.java
│   └── ChequeAccount.java
├── service/               # Business logic
│   └── BankService.java
├── controller/            # Controllers
│   └── BankController.java
├── view/                  # Console view
│   └── BankingSystemView.java
└── interfaces/            # Interfaces
    └── BankingOperations.java
```

## Database

The application uses H2 database with the following tables:
- `users`: Stores user authentication information
- `customers`: Stores customer information (linked to users)
- `accounts`: Stores account information

Database files are created in the `data/` directory.

## Usage

### First Time Setup
1. **Start the application**: Run `mvn javafx:run`
2. **Create an account**: Click "Need an account? Sign up" and fill in your details
3. **Login**: Use your username and password to login

### Using the System
1. **Customer Management**: Add and view customers in the "Customers" tab
2. **Account Management**: Create accounts for customers in the "Accounts" tab
3. **Account Types**:
   - **Savings**: Earns interest, no withdrawals allowed
   - **Investment**: For investment purposes with initial deposit
   - **Cheque**: Standard checking account with employer information
4. **Logout**: Use the User menu to logout securely

## Security Features

- User authentication with username/password
- Session management
- Secure logout functionality
- Database-backed user storage

## OOP Principles Demonstrated

- **Inheritance**: Account → SavingsAccount, InvestmentAccount, ChequeAccount
- **Polymorphism**: Different account types with different behaviors
- **Abstraction**: Abstract Account class
- **Interface Implementation**: BankingOperations interface
- **Encapsulation**: Private fields with getters/setters
- **Composition**: User-Customer-Account relationships

## Building

```bash
mvn clean compile
mvn clean package
```

## Dependencies

- JavaFX Controls 17.0.2
- JavaFX FXML 17.0.2
- H2 Database 2.1.214
- JUnit 4.13.2 (for testing)
