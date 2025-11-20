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
        
        TableColumn<AccountInfo, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(150);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox buttons = new HBox(5, editBtn, deleteBtn);
            
            {
                editBtn.setOnAction(e -> {
                    AccountInfo account = getTableView().getItems().get(getIndex());
                    editAccount(account);
                });
                deleteBtn.setOnAction(e -> {
                    AccountInfo account = getTableView().getItems().get(getIndex());
                    deleteAccount(account);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
        
        accountTable.getColumns().addAll(numberCol, customerCol, typeCol, balanceCol, actionCol);
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
            String sql = "SELECT a.account_number, CONCAT(c.first_name, ' ', c.surname) as customer_name, a.account_type, a.balance " +
                        "FROM accounts a JOIN customers c ON a.customer_id = c.id";
            try (var conn = com.banking.database.DatabaseManager.getConnection();
                 var stmt = conn.createStatement();
                 var rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    AccountInfo accountInfo = new AccountInfo(
                        rs.getString("account_number"),
                        rs.getString("customer_name"),
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
                
                loadAccounts(); // Auto-refresh
                
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
    
    private void deleteAccount(AccountInfo accountInfo) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Account: " + accountInfo.getAccountNumber());
        confirm.setContentText("Customer: " + accountInfo.getCustomerName() + "\nBalance: BWP " + accountInfo.getBalance() + "\n\nAre you sure?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                accountDAO.delete(accountInfo.getAccountNumber());
                loadAccounts(); // Auto-refresh
                showAlert("Success", "Account deleted successfully!");
            } catch (SQLException e) {
                showAlert("Database Error", "Failed to delete account: " + e.getMessage());
            }
        }
    }
    
    private void editAccount(AccountInfo accountInfo) {
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Edit Account Balance");
        dialog.setHeaderText("Account: " + accountInfo.getAccountNumber() + "\nCurrent Balance: BWP " + accountInfo.getBalance());
        
        ButtonType saveButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField balanceField = new TextField(String.valueOf(accountInfo.getBalance()));
        
        grid.add(new Label("New Balance:"), 0, 0);
        grid.add(balanceField, 1, 0);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    return Double.parseDouble(balanceField.getText().trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(newBalance -> {
            if (newBalance != null && newBalance >= 0) {
                try {
                    accountDAO.updateBalance(accountInfo.getAccountNumber(), newBalance);
                    loadAccounts();
                    showAlert("Success", "Account balance updated successfully!");
                } catch (SQLException e) {
                    showAlert("Database Error", "Failed to update balance: " + e.getMessage());
                }
            } else {
                showAlert("Error", "Invalid balance amount");
            }
        });
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
