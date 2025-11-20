package com.banking.gui;

import com.banking.database.AccountDAO;
import com.banking.database.CustomerDAO;
import com.banking.model.Account;
import com.banking.model.Customer;
import com.banking.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.sql.SQLException;
import java.util.List;

public class UserAccountsPane extends VBox {
    private User currentUser;
    private TableView<AccountInfo> accountTable;
    private ObservableList<AccountInfo> accountList;
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;
    private Label totalAccountsLabel;
    private Label totalBalanceLabel;
    
    public UserAccountsPane(User user) {
        this.currentUser = user;
        this.accountList = FXCollections.observableArrayList();
        this.customerDAO = new CustomerDAO();
        this.accountDAO = new AccountDAO();
        setupUI();
        loadUserAccounts();
    }
    
    private void setupUI() {
        setSpacing(15);
        setPadding(new Insets(20));
        
        Label title = new Label("My Accounts");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Account Summary
        VBox summaryBox = new VBox(10);
        summaryBox.setPadding(new Insets(15));
        summaryBox.setStyle("-fx-border-color: gray; -fx-border-radius: 5;");
        
        Label summaryTitle = new Label("Account Summary");
        summaryTitle.setStyle("-fx-font-weight: bold;");
        
        totalAccountsLabel = new Label("Total Accounts: 0");
        totalBalanceLabel = new Label("Total Balance: BWP 0.00");
        
        summaryBox.getChildren().addAll(summaryTitle, totalAccountsLabel, totalBalanceLabel);
        
        // Account Table
        setupAccountTable();
        
        // Action Buttons
        HBox buttonBox = new HBox(10);
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> loadUserAccounts());
        
        buttonBox.getChildren().add(refreshButton);
        
        getChildren().addAll(title, summaryBox, new Label("My Account Details:"), accountTable, buttonBox);
    }
    
    private void setupAccountTable() {
        accountTable = new TableView<>();
        accountTable.setItems(accountList);
        
        TableColumn<AccountInfo, String> numberCol = new TableColumn<>("Account Number");
        numberCol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        numberCol.setPrefWidth(150);
        
        TableColumn<AccountInfo, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        typeCol.setPrefWidth(100);
        
        TableColumn<AccountInfo, Double> balanceCol = new TableColumn<>("Balance");
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        balanceCol.setPrefWidth(100);
        
        TableColumn<AccountInfo, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        
        accountTable.getColumns().addAll(numberCol, typeCol, balanceCol, statusCol);
    }
    
    private void loadUserAccounts() {
        accountList.clear();
        
        try {
            Customer customer = customerDAO.findByUserId(currentUser.getUserId());
            
            if (customer != null) {
                List<Account> accounts = accountDAO.findByCustomerId(customer.getCustomerId());
                
                double totalBalance = 0.0;
                for (Account account : accounts) {
                    accountList.add(new AccountInfo(
                        account.getAccountNumber(),
                        getAccountTypeName(account),
                        account.getBalance(),
                        "Active"
                    ));
                    totalBalance += account.getBalance();
                }
                
                totalAccountsLabel.setText("Total Accounts: " + accounts.size());
                totalBalanceLabel.setText(String.format("Total Balance: BWP %.2f", totalBalance));
            } else {
                totalAccountsLabel.setText("Total Accounts: 0");
                totalBalanceLabel.setText("Total Balance: BWP 0.00");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load accounts: " + e.getMessage());
        }
    }
    
    public void refresh() {
        loadUserAccounts();
    }
    
    private String getAccountTypeName(Account account) {
        String className = account.getClass().getSimpleName();
        return className.replace("Account", "");
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Helper class for account display
    public static class AccountInfo {
        private String accountNumber;
        private String accountType;
        private double balance;
        private String status;
        
        public AccountInfo(String accountNumber, String accountType, double balance, String status) {
            this.accountNumber = accountNumber;
            this.accountType = accountType;
            this.balance = balance;
            this.status = status;
        }
        
        public String getAccountNumber() { return accountNumber; }
        public String getAccountType() { return accountType; }
        public double getBalance() { return balance; }
        public String getStatus() { return status; }
    }
}
