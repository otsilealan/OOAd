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
        
        TableColumn<Customer, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(150);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox buttons = new HBox(5, editBtn, deleteBtn);
            
            {
                editBtn.setOnAction(e -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    editCustomer(customer);
                });
                deleteBtn.setOnAction(e -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    deleteCustomer(customer);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
        
        customerTable.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, actionCol);
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
            
            clearForm();
            loadCustomers(); // Auto-refresh
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
    
    private void deleteCustomer(Customer customer) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Customer: " + customer.getName());
        confirm.setContentText("This will also delete all accounts associated with this customer. Continue?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                customerDAO.delete(customer.getCustomerId());
                loadCustomers(); // Auto-refresh
                showAlert("Success", "Customer deleted successfully!");
            } catch (SQLException e) {
                showAlert("Database Error", "Failed to delete customer: " + e.getMessage());
            }
        }
    }
    
    private void editCustomer(Customer customer) {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Edit Customer");
        dialog.setHeaderText("Edit customer: " + customer.getName());
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField firstNameField = new TextField(customer.getFirstName());
        TextField surnameField = new TextField(customer.getSurname());
        TextField emailField = new TextField(customer.getEmail());
        TextField phoneField = new TextField(customer.getPhoneNumber());
        TextField addressField = new TextField(customer.getAddress() != null ? customer.getAddress() : "");
        
        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Surname:"), 0, 1);
        grid.add(surnameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(addressField, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                customer.setFirstName(firstNameField.getText().trim());
                customer.setSurname(surnameField.getText().trim());
                customer.setEmail(emailField.getText().trim());
                customer.setPhoneNumber(phoneField.getText().trim());
                customer.setAddress(addressField.getText().trim());
                return customer;
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(result -> {
            try {
                customerDAO.update(result);
                loadCustomers();
                showAlert("Success", "Customer updated successfully!");
            } catch (SQLException e) {
                showAlert("Database Error", "Failed to update customer: " + e.getMessage());
            }
        });
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
