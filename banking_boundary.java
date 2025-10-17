package com.example.banking.boundary;

import javax.swing.*;
import java.awt.*;

/**
 * Boundary class for customer-related UI operations
 */
public class CustomerBoundary {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField addressField;
    
    public CustomerBoundary(JTextField firstNameField, JTextField lastNameField, JTextField addressField) {
        this.firstNameField = firstNameField;
        this.lastNameField = lastNameField;
        this.addressField = addressField;
    }
    
    public String getFirstName() {
        return firstNameField.getText().trim();
    }
    
    public String getLastName() {
        return lastNameField.getText().trim();
    }
    
    public String getAddress() {
        return addressField.getText().trim();
    }
    
    public boolean validateCustomerInput() {
        return !getFirstName().isEmpty() && 
               !getLastName().isEmpty() && 
               !getAddress().isEmpty();
    }
    
    public void clearFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        addressField.setText("");
    }
    
    public JPanel createCustomerPanel(java.awt.event.ActionListener createCustomerListener) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Customer Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(15);
        panel.add(firstNameField, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 3;
        lastNameField = new JTextField(15);
        panel.add(lastNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        addressField = new JTextField(30);
        panel.add(addressField, gbc);
        
        gbc.gridx = 3; gbc.gridwidth = 1;
        JButton createCustomerBtn = new JButton("Create Customer");
        createCustomerBtn.addActionListener(createCustomerListener);
        panel.add(createCustomerBtn, gbc);
        
        return panel;
    }
}