package com.example.banking.controllers;

import com.example.banking.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.Window;
import javafx.stage.FileChooser;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class StatementController extends BaseController {
    @FXML private Label accountTypeLabel;
    @FXML private Label balanceLabel;
    @FXML private DatePicker fromDate;
    @FXML private DatePicker toDate;
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, LocalDateTime> dateColumn;
    @FXML private TableColumn<Transaction, TransactionType> typeColumn;
    @FXML private TableColumn<Transaction, BigDecimal> amountColumn;
    @FXML private TableColumn<Transaction, BigDecimal> balanceColumn;
    @FXML private Label totalCreditsLabel;
    @FXML private Label totalDebitsLabel;
    @FXML private VBox root;
    
    private Account account;
    private List<Transaction> allTransactions;
    
    @FXML
    private void initialize() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("resultingBalance"));
        
        // Format date column
        dateColumn.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });
        
        // Format amount and balance columns with currency
        amountColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });
        
        balanceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });
    }
    
    public void setAccount(Account account) {
        this.account = account;
        accountTypeLabel.setText(account.getAccountType());
        balanceLabel.setText(String.format("$%.2f", account.getBalance()));
        
        // Load all transactions
        allTransactions = Bank.getTransactionsForAccount(account.getId());
        updateTransactionView();
    }
    
    @FXML
    private void handleApplyFilter() {
        updateTransactionView();
    }
    
    private void updateTransactionView() {
        LocalDate start = fromDate.getValue();
        LocalDate end = toDate.getValue();
        
        List<Transaction> filtered = allTransactions.stream()
            .filter(t -> {
                if (start != null && t.timestamp().toLocalDate().isBefore(start)) return false;
                if (end != null && t.timestamp().toLocalDate().isAfter(end)) return false;
                return true;
            })
            .collect(Collectors.toList());
        
        transactionTable.getItems().setAll(filtered);
        
        // Update totals
        BigDecimal credits = filtered.stream()
            .filter(t -> t.amount().compareTo(BigDecimal.ZERO) > 0)
            .map(Transaction::amount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal debits = filtered.stream()
            .filter(t -> t.amount().compareTo(BigDecimal.ZERO) < 0)
            .map(Transaction::amount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        totalCreditsLabel.setText(String.format("$%.2f", credits));
        totalDebitsLabel.setText(String.format("$%.2f", debits.abs()));
    }
    
    @FXML
    private void handleExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Statement");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        
        File file = fileChooser.showSaveDialog(getWindow());
        if (file == null) return;
        
        try (PrintWriter writer = new PrintWriter(file)) {
            // Write header
            writer.println("Date,Type,Amount,Balance");
            
            // Write transactions
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (Transaction t : transactionTable.getItems()) {
                writer.printf("%s,%s,%.2f,%.2f%n",
                    t.timestamp().format(formatter),
                    t.type(),
                    t.amount(),
                    t.resultingBalance()
                );
            }
            
            showInfo("Success", "Statement exported successfully");
            
        } catch (IOException e) {
            showError("Export Failed", "Failed to export statement: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClose() {
        closeCurrentWindow();
    }
    
    @Override
    protected Window getWindow() {
        return root.getScene().getWindow();
    }
}