package com.example.banking.boundary;

import javax.swing.*;
import java.awt.*;

/**
 * Boundary class for output display
 */
public class OutputBoundary {
    private JTextArea outputArea;
    
    public OutputBoundary() {
        outputArea = new JTextArea(8, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    }
    
    public void displayMessage(String message) {
        outputArea.append(message + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
    
    public void clearOutput() {
        outputArea.setText("");
    }
    
    public JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Output"));
        
        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
}