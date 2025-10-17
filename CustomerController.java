package com.example.banking.controllers;

import com.example.banking.Customer;
import com.example.banking.Bank;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.Window;

public class CustomerController extends BaseController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private VBox root;
    
    @FXML
    private void handleCreate() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        
        if (name.isEmpty()) {
            showError("Validation Error", "Name is required");
            return;
        }
        
        try {
            Customer customer = Bank.createCustomer(name);
            if (!email.isEmpty()) {
                // Set email if provided
            }
            if (!phone.isEmpty()) {
                // Set phone if provided
            }
            
            showInfo("Success", "Customer created successfully");
            closeCurrentWindow();
        } catch (Exception e) {
            showError("Error", "Failed to create customer: " + e.getMessage());
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
}