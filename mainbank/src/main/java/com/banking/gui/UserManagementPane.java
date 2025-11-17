package com.banking.gui;

import com.banking.database.UserDAO;
import com.banking.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.sql.SQLException;

public class UserManagementPane extends VBox {
    private UserDAO userDAO;
    private TableView<User> userTable;
    private ObservableList<User> userList;
    private TextField usernameField, emailField;
    private PasswordField passwordField;
    private ComboBox<String> roleCombo;
    
    public UserManagementPane() {
        this.userDAO = new UserDAO();
        this.userList = FXCollections.observableArrayList();
        
        setupUI();
        loadUsers();
    }
    
    private void setupUI() {
        setSpacing(10);
        setPadding(new Insets(10));
        
        // Add User Form
        VBox formBox = new VBox(5);
        formBox.setPadding(new Insets(10));
        formBox.setStyle("-fx-border-color: gray; -fx-border-radius: 5;");
        
        Label formTitle = new Label("Create New User");
        formTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(5);
        
        usernameField = new TextField();
        emailField = new TextField();
        passwordField = new PasswordField();
        
        roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll(User.ROLE_USER, User.ROLE_ADMIN);
        roleCombo.setValue(User.ROLE_USER);
        
        form.add(new Label("Username:"), 0, 0);
        form.add(usernameField, 1, 0);
        form.add(new Label("Email:"), 0, 1);
        form.add(emailField, 1, 1);
        form.add(new Label("Password:"), 0, 2);
        form.add(passwordField, 1, 2);
        form.add(new Label("Role:"), 0, 3);
        form.add(roleCombo, 1, 3);
        
        Button addButton = new Button("Create User");
        addButton.setOnAction(e -> createUser());
        
        formBox.getChildren().addAll(formTitle, form, addButton);
        
        // User Table
        setupUserTable();
        
        getChildren().addAll(formBox, new Label("System Users:"), userTable);
    }
    
    private void setupUserTable() {
        userTable = new TableView<>();
        userTable.setItems(userList);
        
        TableColumn<User, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        idCol.setPrefWidth(50);
        
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(150);
        
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);
        
        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(100);
        
        userTable.getColumns().addAll(idCol, usernameCol, emailCol, roleCol);
    }
    
    private void createUser() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String role = roleCombo.getValue();
        
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "All fields are required.");
            return;
        }
        
        try {
            User user = new User(username, password, email, role);
            userDAO.save(user);
            
            userList.add(user);
            clearForm();
            showAlert("Success", "User created successfully!");
            
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to create user: " + e.getMessage());
        }
    }
    
    private void loadUsers() {
        // For demo purposes - in a real app you'd load all users from database
        userList.clear();
    }
    
    private void clearForm() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        roleCombo.setValue(User.ROLE_USER);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
