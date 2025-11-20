package com.banking.gui;

import com.banking.database.AccountDAO;
import com.banking.model.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.SQLException;

public class TransactionPane extends VBox {
    private AccountDAO accountDAO;
    private TextField accountNumberField;
    private TextField amountField;
    private Label balanceLabel;
    private Account currentAccount;
    
    public TransactionPane() {
        this.accountDAO = new AccountDAO();
        setupUI();
    }
    
    private void setupUI() {
        setSpacing(15);
        setPadding(new Insets(20));
        
        Label title = new Label("Account Transactions");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Account lookup
        GridPane lookupGrid = new GridPane();
        lookupGrid.setHgap(10);
        lookupGrid.setVgap(10);
        
        lookupGrid.add(new Label("Account Number:"), 0, 0);
        accountNumberField = new TextField();
        lookupGrid.add(accountNumberField, 1, 0);
        
        Button lookupButton = new Button("Lookup");
        lookupButton.setOnAction(e -> lookupAccount());
        lookupGrid.add(lookupButton, 2, 0);
        
        balanceLabel = new Label("Balance: -");
        balanceLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        // Transaction form
        GridPane transactionGrid = new GridPane();
        transactionGrid.setHgap(10);
        transactionGrid.setVgap(10);
        transactionGrid.setPadding(new Insets(10));
        transactionGrid.setStyle("-fx-border-color: gray; -fx-border-radius: 5;");
        
        transactionGrid.add(new Label("Amount:"), 0, 0);
        amountField = new TextField();
        amountField.setPromptText("Enter amount");
        transactionGrid.add(amountField, 1, 0);
        
        HBox buttonBox = new HBox(10);
        Button depositButton = new Button("Deposit");
        depositButton.setOnAction(e -> performDeposit());
        
        Button withdrawButton = new Button("Withdraw");
        withdrawButton.setOnAction(e -> performWithdraw());
        
        buttonBox.getChildren().addAll(depositButton, withdrawButton);
        transactionGrid.add(buttonBox, 0, 1, 2, 1);
        
        getChildren().addAll(title, lookupGrid, balanceLabel, new Label("Transaction:"), transactionGrid);
    }
    
    private void lookupAccount() {
        String accountNumber = accountNumberField.getText().trim();
        if (accountNumber.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please enter account number");
            return;
        }
        
        try {
            currentAccount = accountDAO.findByAccountNumber(accountNumber);
            if (currentAccount != null) {
                updateBalanceDisplay();
                showAlert(Alert.AlertType.INFORMATION, "Account loaded successfully");
            } else {
                showAlert(Alert.AlertType.WARNING, "Account not found");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
        }
    }
    
    private void performDeposit() {
        if (currentAccount == null) {
            showAlert(Alert.AlertType.WARNING, "Please lookup an account first");
            return;
        }
        
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                showAlert(Alert.AlertType.WARNING, "Amount must be positive");
                return;
            }
            
            currentAccount.deposit(amount);
            accountDAO.updateBalance(currentAccount.getAccountNumber(), currentAccount.getBalance());
            
            // Reload account to get fresh data
            currentAccount = accountDAO.findByAccountNumber(currentAccount.getAccountNumber());
            updateBalanceDisplay();
            amountField.clear();
            showAlert(Alert.AlertType.INFORMATION, "Deposit successful!");
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid amount");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
        }
    }
    
    private void performWithdraw() {
        if (currentAccount == null) {
            showAlert(Alert.AlertType.WARNING, "Please lookup an account first");
            return;
        }
        
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                showAlert(Alert.AlertType.WARNING, "Amount must be positive");
                return;
            }
            
            if (currentAccount instanceof SavingsAccount) {
                showAlert(Alert.AlertType.WARNING, "Savings accounts do not allow withdrawals");
                return;
            }
            
            if (currentAccount.withdraw(amount)) {
                accountDAO.updateBalance(currentAccount.getAccountNumber(), currentAccount.getBalance());
                
                // Reload account to get fresh data
                currentAccount = accountDAO.findByAccountNumber(currentAccount.getAccountNumber());
                updateBalanceDisplay();
                amountField.clear();
                showAlert(Alert.AlertType.INFORMATION, "Withdrawal successful!");
            } else {
                showAlert(Alert.AlertType.WARNING, "Insufficient funds");
            }
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid amount");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
        }
    }
    
    private void updateBalanceDisplay() {
        if (currentAccount != null) {
            balanceLabel.setText(String.format("Balance: BWP %.2f", currentAccount.getBalance()));
        }
    }
    
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type.toString());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
