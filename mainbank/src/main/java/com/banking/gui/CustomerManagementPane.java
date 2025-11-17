package com.banking.gui;

import com.banking.database.CustomerDAO;
import com.banking.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.sql.SQLException;

public class CustomerManagementPane extends VBox {
    private CustomerDAO customerDAO;
    private TableView<Customer> customerTable;
    private ObservableList<Customer> customerList;
    private TextField nameField, emailField, phoneField;
    
    public CustomerManagementPane() {
        this.customerDAO = new CustomerDAO();
        this.customerList = FXCollections.observableArrayList();
        
        setupUI();
        loadCustomers();
    }
    
    private void setupUI() {
        setSpacing(10);
        setPadding(new Insets(10));
        
        // Add Customer Form
        VBox formBox = new VBox(5);
        formBox.setPadding(new Insets(10));
        formBox.setStyle("-fx-border-color: gray; -fx-border-radius: 5;");
        
        Label formTitle = new Label("Add New Customer");
        formTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(5);
        
        nameField = new TextField();
        emailField = new TextField();
        phoneField = new TextField();
        
        form.add(new Label("Name:"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(new Label("Email:"), 0, 1);
        form.add(emailField, 1, 1);
        form.add(new Label("Phone:"), 0, 2);
        form.add(phoneField, 1, 2);
        
        Button addButton = new Button("Add Customer");
        addButton.setOnAction(e -> addCustomer());
        
        formBox.getChildren().addAll(formTitle, form, addButton);
        
        // Customer Table
        setupCustomerTable();
        
        getChildren().addAll(formBox, new Label("Existing Customers:"), customerTable);
    }
    
    private void setupCustomerTable() {
        customerTable = new TableView<>();
        customerTable.setItems(customerList);
        
        TableColumn<Customer, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        idCol.setPrefWidth(50);
        
        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);
        
        TableColumn<Customer, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(250);
        
        TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setPrefWidth(150);
        
        customerTable.getColumns().addAll(idCol, nameCol, emailCol, phoneCol);
    }
    
    private void addCustomer() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        
        if (name.isEmpty() || email.isEmpty()) {
            showAlert("Error", "Name and Email are required fields.");
            return;
        }
        
        try {
            Customer customer = new Customer(name, email, phone);
            customerDAO.save(customer);
            
            customerList.add(customer);
            clearForm();
            showAlert("Success", "Customer added successfully!");
            
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to save customer: " + e.getMessage());
        }
    }
    
    private void loadCustomers() {
        try {
            customerList.clear();
            customerList.addAll(customerDAO.findAll());
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load customers: " + e.getMessage());
        }
    }
    
    private void clearForm() {
        nameField.clear();
        emailField.clear();
        phoneField.clear();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
