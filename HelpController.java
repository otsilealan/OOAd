package com.example.banking.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;
import javafx.stage.Window;
import javafx.scene.layout.BorderPane;
import java.util.Map;
import java.util.LinkedHashMap;

public class HelpController extends BaseController {
    @FXML private BorderPane root;
    @FXML private ListView<String> topicList;
    @FXML private WebView contentView;
    
    private final Map<String, String> helpContent = new LinkedHashMap<>();
    
    @FXML
    private void initialize() {
        initializeHelpContent();
        
        topicList.setItems(FXCollections.observableArrayList(helpContent.keySet()));
        topicList.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    showContent(newVal);
                }
            }
        );
        
        // Select first topic
        if (!helpContent.isEmpty()) {
            topicList.getSelectionModel().selectFirst();
        }
    }
    
    private void initializeHelpContent() {
        helpContent.put("Getting Started", 
            "<h1>Getting Started</h1>" +
            "<p>Welcome to the Banking System! This application allows you to manage customers, " +
            "accounts, and transactions in a simple and efficient way.</p>" +
            "<h2>Quick Start</h2>" +
            "<ol>" +
            "<li>Add a new customer using File → New Customer (Ctrl+N)</li>" +
            "<li>Select the customer and add an account using File → New Account (Ctrl+A)</li>" +
            "<li>Use the transaction menu or buttons to manage account operations</li>" +
            "</ol>");
            
        helpContent.put("Managing Customers",
            "<h1>Managing Customers</h1>" +
            "<p>Customers are the core of the banking system. Each customer can have multiple accounts.</p>" +
            "<h2>Adding a Customer</h2>" +
            "<ul>" +
            "<li>Click File → New Customer or press Ctrl+N</li>" +
            "<li>Fill in the required information</li>" +
            "<li>Click Create to add the customer</li>" +
            "</ul>");
            
        helpContent.put("Account Types",
            "<h1>Account Types</h1>" +
            "<p>The system supports three types of accounts:</p>" +
            "<h2>Savings Account</h2>" +
            "<ul>" +
            "<li>Earns interest on the balance</li>" +
            "<li>No overdraft facility</li>" +
            "</ul>" +
            "<h2>Cheque Account</h2>" +
            "<ul>" +
            "<li>Everyday transactions</li>" +
            "<li>Optional overdraft facility</li>" +
            "</ul>" +
            "<h2>Investment Account</h2>" +
            "<ul>" +
            "<li>Higher interest rates</li>" +
            "<li>Term deposit options</li>" +
            "</ul>");
            
        helpContent.put("Transactions",
            "<h1>Transactions</h1>" +
            "<p>Manage account transactions easily:</p>" +
            "<h2>Available Operations</h2>" +
            "<ul>" +
            "<li>Deposit (Ctrl+D): Add funds to an account</li>" +
            "<li>Withdraw (Ctrl+W): Remove funds from an account</li>" +
            "<li>Transfer (Ctrl+T): Move funds between accounts</li>" +
            "</ul>" +
            "<h2>Transaction History</h2>" +
            "<p>View transaction history using the Statement option (Ctrl+S)</p>");
            
        helpContent.put("Keyboard Shortcuts",
            "<h1>Keyboard Shortcuts</h1>" +
            "<table border='1' cellpadding='5'>" +
            "<tr><th>Action</th><th>Shortcut</th></tr>" +
            "<tr><td>New Customer</td><td>Ctrl+N</td></tr>" +
            "<tr><td>New Account</td><td>Ctrl+A</td></tr>" +
            "<tr><td>Deposit</td><td>Ctrl+D</td></tr>" +
            "<tr><td>Withdraw</td><td>Ctrl+W</td></tr>" +
            "<tr><td>Transfer</td><td>Ctrl+T</td></tr>" +
            "<tr><td>Statement</td><td>Ctrl+S</td></tr>" +
            "<tr><td>Help</td><td>F1</td></tr>" +
            "<tr><td>Exit</td><td>Ctrl+Q</td></tr>" +
            "</table>");
    }
    
    private void showContent(String topic) {
        String content = helpContent.get(topic);
        if (content != null) {
            content = "<html><head>" +
                     "<style>" +
                     "body { font-family: system-ui; padding: 20px; }" +
                     "h1 { color: #2c3e50; }" +
                     "h2 { color: #34495e; }" +
                     "table { border-collapse: collapse; }" +
                     "th, td { padding: 8px; border: 1px solid #ddd; }" +
                     "th { background-color: #f8f9fa; }" +
                     "</style>" +
                     "</head><body>" +
                     content +
                     "</body></html>";
            contentView.getEngine().loadContent(content);
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