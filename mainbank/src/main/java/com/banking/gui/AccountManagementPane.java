package com.banking.gui;

import com.banking.database.AccountDAO;
import com.banking.database.CustomerDAO;
import com.banking.model.*;
import com.banking.service.BankService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.sql.SQLException;
import java.util.List;

public class AccountManagementPane extends VBox {
    private User currentUser;
    private BankService bankService;
    private AccountDAO accountDAO;
    private CustomerDAO customerDAO;
    private TableView<AccountInfo> accountTable;
    private ObservableList<AccountInfo> accountList;
    private ComboBox<Customer> customerCombo;
    private ComboBox<String> accountTypeCombo;
    private TextField initialDepositField;
    
    public AccountManagementPane(User user) {
        this.currentUser = user;
        this.bankService = new BankService();
        this.accountDAO = new AccountDAO();
        this.customerDAO = new CustomerDAO();
        this.accountList = FXCollections.observableArrayList();
        
        setupUI();
        loadCustomers();
        loadAccounts();
    }
    
    private void setupUI() {
        setSpacing(10);
        setPadding(new Insets(10));
        
        // Account Creation Form
        VBox formBox = new VBox(5);
        formBox.setPadding(new Insets(10));
        formBox.setStyle("-fx-border-color: gray; -fx-border-radius: 5;");
        
        Label formTitle = new Label("Create New Account");
        formTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(5);
        
        customerCombo = new ComboBox<>();
        customerCombo.setPrefWidth(200);
        
        accountTypeCombo = new ComboBox<>();
        accountTypeCombo.getItems().addAll("Savings", "Investment", "Cheque");
        accountTypeCombo.setValue("Savings");
        
        initialDepositField = new TextField("0.00");
        
        form.add(new Label("Customer:"), 0, 0);
        form.add(customerCombo, 1, 0);
        form.add(new Label("Account Type:"), 0, 1);
        form.add(accountTypeCombo, 1, 1);
        form.add(new Label("Initial Deposit:"), 0, 2);
        form.add(initialDepositField, 1, 2);
        
        Button createButton = new Button("Create Account");
        createButton.setOnAction(e -> createAccount());
        
        formBox.getChildren().addAll(formTitle, form, createButton);
        
        // Account Table
        setupAccountTable();
        
        getChildren().addAll(formBox, new Label("Existing Accounts:"), accountTable);
    }
    
    private void setupAccountTable() {
        accountTable = new TableView<>();
        accountTable.setItems(accountList);
        
        TableColumn<AccountInfo, String> numberCol = new TableColumn<>("Account Number");
        numberCol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        numberCol.setPrefWidth(150);
        
        TableColumn<AccountInfo, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerCol.setPrefWidth(200);
        
        TableColumn<AccountInfo, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        typeCol.setPrefWidth(100);
        
        TableColumn<AccountInfo, Double> balanceCol = new TableColumn<>("Balance");
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        balanceCol.setPrefWidth(100);
        
        accountTable.getColumns().addAll(numberCol, customerCol, typeCol, balanceCol);
    }
    
    private void loadCustomers() {
        try {
            List<Customer> customers = customerDAO.findAll();
            customerCombo.getItems().clear();
            customerCombo.getItems().addAll(customers);
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load customers: " + e.getMessage());
        }
    }
    
    private void loadAccounts() {
        try {
            accountList.clear();
            String sql = "SELECT a.account_number, c.name, a.account_type, a.balance FROM accounts a JOIN customers c ON a.customer_id = c.id";
            try (var conn = com.banking.database.DatabaseManager.getConnection();
                 var stmt = conn.createStatement();
                 var rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    AccountInfo accountInfo = new AccountInfo(
                        rs.getString("account_number"),
                        rs.getString("name"),
                        rs.getString("account_type"),
                        rs.getDouble("balance")
                    );
                    accountList.add(accountInfo);
                }
            }
        } catch (Exception e) {
            showAlert("Database Error", "Failed to load accounts: " + e.getMessage());
        }
    }
    
    private void createAccount() {
        Customer selectedCustomer = customerCombo.getValue();
        String accountType = accountTypeCombo.getValue();
        String depositText = initialDepositField.getText().trim();
        
        if (selectedCustomer == null || accountType == null) {
            showAlert("Error", "Please select a customer and account type.");
            return;
        }
        
        double initialDeposit = 0.0;
        try {
            initialDeposit = Double.parseDouble(depositText);
            if (initialDeposit < 0) {
                showAlert("Error", "Initial deposit cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid deposit amount.");
            return;
        }
        
        try {
            Account account = bankService.openAccount(selectedCustomer, accountType.toLowerCase(), initialDeposit);
            if (account != null) {
                accountDAO.save(account);
                
                // Refresh the account list from database
                loadAccounts();
                
                showAlert("Success", "Account created successfully!\nAccount Number: " + account.getAccountNumber());
                initialDepositField.setText("0.00");
                customerCombo.setValue(null);
            } else {
                showAlert("Error", "Failed to create account.");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to save account: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Helper class for table display
    public static class AccountInfo {
        private String accountNumber;
        private String customerName;
        private String accountType;
        private double balance;
        
        public AccountInfo(String accountNumber, String customerName, String accountType, double balance) {
            this.accountNumber = accountNumber;
            this.customerName = customerName;
            this.accountType = accountType;
            this.balance = balance;
        }
        
        public String getAccountNumber() { return accountNumber; }
        public String getCustomerName() { return customerName; }
        public String getAccountType() { return accountType; }
        public double getBalance() { return balance; }
    }
}
