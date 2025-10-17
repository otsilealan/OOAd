package com.example.banking.controllers;

import com.example.banking.*;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.Window;
import java.math.BigDecimal;

public class TransactionController extends BaseController {
    @FXML private ComboBox<String> transactionTypeCombo;
    @FXML private TextField amountField;
    @FXML private ComboBox<Account> toAccountCombo;
    @FXML private VBox transferContainer;
    @FXML private VBox root;
    
    private Account sourceAccount;
    
    public void setSourceAccount(Account account) {
        this.sourceAccount = account;
    }
    
    @FXML
    private void initialize() {
        transactionTypeCombo.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                transferContainer.setVisible("Transfer".equals(newVal));
                transferContainer.setManaged("Transfer".equals(newVal));
            }
        );
    }
    
    @FXML
    private void handleProcess() {
        if (sourceAccount == null) {
            showError("Error", "No source account selected");
            return;
        }
        
        try {
            String type = transactionTypeCombo.getValue();
            BigDecimal amount = new BigDecimal(amountField.getText());
            
            switch (type) {
                case "Deposit":
                    Bank.deposit(sourceAccount.getId(), amount);
                    showInfo("Success", "Deposit processed successfully");
                    break;
                    
                case "Withdrawal":
                    Bank.withdraw(sourceAccount.getId(), amount);
                    showInfo("Success", "Withdrawal processed successfully");
                    break;
                    
                case "Transfer":
                    Account targetAccount = toAccountCombo.getValue();
                    if (targetAccount == null) {
                        showError("Error", "Please select target account");
                        return;
                    }
                    Bank.transfer(sourceAccount.getId(), targetAccount.getId(), amount);
                    showInfo("Success", "Transfer processed successfully");
                    break;
            }
            
            closeCurrentWindow();
            
        } catch (NumberFormatException e) {
            showError("Error", "Invalid amount format");
        } catch (Exception e) {
            showError("Error", "Transaction failed: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancel() {
        closeCurrentWindow();
    }
    
    @Override
    protected Window getWindow() {
        return root.getScene().getWindow();
    }
    
    public void setAvailableAccounts(Account sourceAccount) {
        this.sourceAccount = sourceAccount;
        // TODO: Populate toAccountCombo with all accounts except source
    }
}