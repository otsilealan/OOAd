package com.example.banking.boundary;

import javax.swing.*;
import java.awt.*;

/**
 * Boundary class for account operation UI
 */
public class OperationBoundary {
    
    public JPanel createOperationsPanel(
            java.awt.event.ActionListener depositListener,
            java.awt.event.ActionListener withdrawListener,
            java.awt.event.ActionListener interestListener,
            java.awt.event.ActionListener balanceListener) {
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Account Operations"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        gbc.gridx = 0; gbc.gridy = 0;
        JButton depositBtn = new JButton("Make Deposit");
        depositBtn.addActionListener(depositListener);
        panel.add(depositBtn, gbc);
        
        gbc.gridy = 1;
        JButton withdrawBtn = new JButton("Make Withdrawal");
        withdrawBtn.addActionListener(withdrawListener);
        panel.add(withdrawBtn, gbc);
        
        gbc.gridy = 2;
        JButton interestBtn = new JButton("Calculate Interest");
        interestBtn.addActionListener(interestListener);
        panel.add(interestBtn, gbc);
        
        gbc.gridy = 3;
        JButton balanceBtn = new JButton("Check Balance");
        balanceBtn.addActionListener(balanceListener);
        panel.add(balanceBtn, gbc);
        
        return panel;
    }
    
    public String promptForAmount(Component parent, String message) {
        return JOptionPane.showInputDialog(parent, message);
    }
}