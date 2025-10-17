package com.example.banking.boundary;

import javax.swing.*;
import java.awt.*;

/**
 * Boundary class for account-related UI operations
 */
public class AccountBoundary {
    private JComboBox<String> accountTypeCombo;
    private JTextField initialDepositField;
    private JTextField companyNameField;
    private JTextField companyAddressField;
    private JList<String> accountList;
    private DefaultListModel<String> accountListModel;
    
    public AccountBoundary() {
        accountListModel = new DefaultListModel<>();
        accountList = new JList<>(accountListModel);
    }
    
    public String getAccountType() {
        return (String) accountTypeCombo.getSelectedItem();
    }
    
    public String getInitialDeposit() {
        return initialDepositField.getText().trim();
    }
    
    public String getCompanyName() {
        return companyNameField.getText().trim();
    }
    
    public String getCompanyAddress() {
        return companyAddressField.getText().trim();
    }
    
    public int getSelectedAccountIndex() {
        return accountList.getSelectedIndex();
    }
    
    public boolean validateAccountInput() {
        return !getInitialDeposit().isEmpty();
    }
    
    public boolean validateCompanyInput() {
        return !getCompanyName().isEmpty() && !getCompanyAddress().isEmpty();
    }
    
    public void clearAccountFields() {
        initialDepositField.setText("");
        companyNameField.setText("");
        companyAddressField.setText("");
    }
    
    public void toggleCompanyFields() {
        boolean isCheque = "Cheque".equals(accountTypeCombo.getSelectedItem());
        companyNameField.setEnabled(isCheque);
        companyAddressField.setEnabled(isCheque);
    }
    
    public void updateAccountList(java.util.List<String> accountDisplayList) {
        accountListModel.clear();
        for (String accountDisplay : accountDisplayList) {
            accountListModel.addElement(accountDisplay);
        }
    }
    
    public JPanel createAccountPanel(java.awt.event.ActionListener createAccountListener) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Account Management"));
        
        // Account creation form
        JPanel createPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        createPanel.add(new JLabel("Account Type:"), gbc);
        gbc.gridx = 1;
        accountTypeCombo = new JComboBox<>(new String[]{"Savings", "Investment", "Cheque"});
        accountTypeCombo.addActionListener(e -> toggleCompanyFields());
        createPanel.add(accountTypeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        createPanel.add(new JLabel("Initial Deposit:"), gbc);
        gbc.gridx = 1;
        initialDepositField = new JTextField(10);
        createPanel.add(initialDepositField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        createPanel.add(new JLabel("Company Name:"), gbc);
        gbc.gridx = 1;
        companyNameField = new JTextField(10);
        companyNameField.setEnabled(false);
        createPanel.add(companyNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        createPanel.add(new JLabel("Company Address:"), gbc);
        gbc.gridx = 1;
        companyAddressField = new JTextField(10);
        companyAddressField.setEnabled(false);
        createPanel.add(companyAddressField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton createAccountBtn = new JButton("Create Account");
        createAccountBtn.addActionListener(createAccountListener);
        createPanel.add(createAccountBtn, gbc);
        
        panel.add(createPanel, BorderLayout.NORTH);
        
        // Account list
        accountList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(accountList);
        scrollPane.setPreferredSize(new Dimension(300, 150));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
}