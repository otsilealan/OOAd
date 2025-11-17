package com.banking.gui;

import com.banking.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class UserAccountsPane extends VBox {
    private User currentUser;
    private TableView<AccountInfo> accountTable;
    private ObservableList<AccountInfo> accountList;
    
    public UserAccountsPane(User user) {
        this.currentUser = user;
        this.accountList = FXCollections.observableArrayList();
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
        
        Label totalAccountsLabel = new Label("Total Accounts: 0");
        Label totalBalanceLabel = new Label("Total Balance: $0.00");
        
        summaryBox.getChildren().addAll(summaryTitle, totalAccountsLabel, totalBalanceLabel);
        
        // Account Table
        setupAccountTable();
        
        // Action Buttons
        HBox buttonBox = new HBox(10);
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> loadUserAccounts());
        
        Button requestAccountButton = new Button("Request New Account");
        requestAccountButton.setOnAction(e -> showAlert("Info", "Account request functionality not implemented yet."));
        
        buttonBox.getChildren().addAll(refreshButton, requestAccountButton);
        
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
        // For demo purposes - in a real app you'd load user's accounts from database
        accountList.clear();
        // Add sample data
        accountList.add(new AccountInfo("ACC123456", "Savings", 1500.00, "Active"));
        accountList.add(new AccountInfo("ACC789012", "Cheque", 750.50, "Active"));
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
