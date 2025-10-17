package com.example.banking.controllers;

import com.example.banking.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.Window;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AccountController extends BaseController {
    @FXML private ComboBox<String> accountTypeCombo;
    @FXML private TextField initialDepositField;
    @FXML private VBox additionalFieldsContainer;
    @FXML private VBox root;
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> dateColumn;
    @FXML private TableColumn<Transaction, String> typeColumn;
    @FXML private TableColumn<Transaction, BigDecimal> amountColumn;
    @FXML private TableColumn<Transaction, BigDecimal> balanceColumn;
    
    private Customer selectedCustomer;
    
    public void setCustomer(Customer customer) {
        this.selectedCustomer = customer;
    }
    
    private Account currentAccount;
    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    private void initialize() {
        accountTypeCombo.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> updateAdditionalFields(newVal)
        );

        // Initialize transaction table
        dateColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().timestamp().format(DATE_FORMATTER)));
        typeColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().type()));
        amountColumn.setCellValueFactory(data -> 
            new SimpleObjectProperty<>(data.getValue().amount()));
        balanceColumn.setCellValueFactory(data ->
            new SimpleObjectProperty<>(calculateRunningBalance(data.getValue())));
            
        transactionTable.setItems(transactions);
    }
    
    private void updateAdditionalFields(String accountType) {
        additionalFieldsContainer.getChildren().clear();
        
        if (accountType == null) return;
        
        switch (accountType) {
            case "Savings Account":
                TextField interestField = new TextField();
                interestField.setPromptText("Annual Interest Rate (%)");
                additionalFieldsContainer.getChildren().add(interestField);
                break;
            case "Cheque Account":
                TextField overdraftField = new TextField();
                overdraftField.setPromptText("Overdraft Limit");
                additionalFieldsContainer.getChildren().add(overdraftField);
                break;
            case "Investment Account":
                // Add term and penalty fields
                break;
        }
    }
    
    @FXML
    private void handleCreate() {
        if (selectedCustomer == null) {
            showError("Error", "No customer selected");
            return;
        }
        
        try {
            String type = accountTypeCombo.getValue();
            BigDecimal initialDeposit = new BigDecimal(initialDepositField.getText());
            
            Map<String, Object> params = new HashMap<>();
            // Add type-specific parameters
            
            Account account = Bank.openAccount(selectedCustomer.getId(), 
                                            AccountType.valueOf(type.replace(" ", "_").toUpperCase()), 
                                            initialDeposit, 
                                            params);
            
            showInfo("Success", "Account created successfully");
            closeCurrentWindow();
            
        } catch (NumberFormatException e) {
            showError("Error", "Invalid amount format");
        } catch (Exception e) {
            showError("Error", "Failed to create account: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancel() {
        closeCurrentWindow();
    }

    @FXML
    private void handleDeposit() {
        showTransactionDialog("Deposit", TransactionType.DEPOSIT);
    }

    @FXML
    private void handleWithdraw() {
        showTransactionDialog("Withdraw", TransactionType.WITHDRAW);
    }

    @FXML
    private void handleTransfer() {
        showTransactionDialog("Transfer", TransactionType.TRANSFER);
    }

    @FXML
    private void handleViewStatement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/banking/views/statement.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Account Statement");
            stage.setScene(new Scene(root));
            
            StatementController controller = loader.getController();
            controller.setAccount(currentAccount);
            
            stage.showAndWait();
        } catch (IOException e) {
            showError("Error", "Could not open statement view", e.getMessage());
        }
    }

    private void showTransactionDialog(String title, TransactionType type) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/banking/views/transaction.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            
            TransactionController controller = loader.getController();
            controller.setAccount(currentAccount);
            controller.setTransactionType(type);
            controller.setOnSuccess(this::refreshTransactions);
            
            stage.showAndWait();
        } catch (IOException e) {
            showError("Error", "Could not open transaction form", e.getMessage());
        }
    }

    private BigDecimal calculateRunningBalance(Transaction transaction) {
        BigDecimal runningBalance = BigDecimal.ZERO;
        for (Transaction t : transactions) {
            runningBalance = runningBalance.add(t.amount());
            if (t == transaction) break;
        }
        return runningBalance;
    }

    private void refreshTransactions() {
        if (currentAccount != null) {
            transactions.setAll(Bank.getTransactionsForAccount(currentAccount.getId()));
        }
    }

    public void setAccount(Account account) {
        this.currentAccount = account;
        refreshTransactions();
    }
    
    @Override
    protected Window getWindow() {
        return root.getScene().getWindow();
    }

    private enum TransactionType {
        DEPOSIT, WITHDRAW, TRANSFER
    }
}