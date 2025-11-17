package com.banking.gui;

import com.banking.database.UserDAO;
import com.banking.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class LoginPane extends VBox {
    private UserDAO userDAO;
    private TextField usernameField, emailField;
    private PasswordField passwordField;
    private ComboBox<String> roleCombo;
    private Button loginButton, signupButton, switchButton;
    private Label titleLabel;
    private boolean isLoginMode = true;
    private Stage stage;
    
    public LoginPane(Stage stage) {
        this.stage = stage;
        this.userDAO = new UserDAO();
        setupUI();
    }
    
    private void setupUI() {
        setSpacing(15);
        setPadding(new Insets(30));
        setAlignment(Pos.CENTER);
        setMaxWidth(400);
        
        titleLabel = new Label("Login");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        
        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setVisible(false);
        emailField.setManaged(false);
        
        roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("USER", "ADMIN");
        roleCombo.setValue("USER");
        roleCombo.setVisible(false);
        roleCombo.setManaged(false);
        
        loginButton = new Button("Login");
        loginButton.setPrefWidth(200);
        loginButton.setOnAction(e -> handleLogin());
        
        signupButton = new Button("Sign Up");
        signupButton.setPrefWidth(200);
        signupButton.setOnAction(e -> handleSignup());
        signupButton.setVisible(false);
        signupButton.setManaged(false);
        
        switchButton = new Button("Need an account? Sign up");
        switchButton.setOnAction(e -> switchMode());
        
        getChildren().addAll(titleLabel, usernameField, emailField, roleCombo, passwordField, loginButton, signupButton, switchButton);
    }
    
    private void switchMode() {
        isLoginMode = !isLoginMode;
        if (isLoginMode) {
            titleLabel.setText("Login");
            emailField.setVisible(false);
            emailField.setManaged(false);
            roleCombo.setVisible(false);
            roleCombo.setManaged(false);
            loginButton.setVisible(true);
            loginButton.setManaged(true);
            signupButton.setVisible(false);
            signupButton.setManaged(false);
            switchButton.setText("Need an account? Sign up");
        } else {
            titleLabel.setText("Sign Up");
            emailField.setVisible(true);
            emailField.setManaged(true);
            roleCombo.setVisible(true);
            roleCombo.setManaged(true);
            loginButton.setVisible(false);
            loginButton.setManaged(false);
            signupButton.setVisible(true);
            signupButton.setManaged(true);
            switchButton.setText("Have an account? Login");
        }
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }
        
        try {
            if (userDAO.authenticate(username, password)) {
                User user = userDAO.findByUsername(username);
                openMainApplication(user);
            } else {
                showAlert("Error", "Invalid username or password.");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Login failed: " + e.getMessage());
        }
    }
    
    private void handleSignup() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String role = roleCombo.getValue();
        
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }
        
        try {
            User user = new User(username, password, email, role);
            userDAO.save(user);
            showAlert("Success", "Account created successfully! You can now login.");
            switchMode();
            clearFields();
        } catch (SQLException e) {
            showAlert("Database Error", "Signup failed: " + e.getMessage());
        }
    }
    
    private void openMainApplication(User user) {
        if (user.isAdmin()) {
            AdminDashboard adminDashboard = new AdminDashboard(user);
            stage.getScene().setRoot(adminDashboard);
            stage.setTitle("Banking System - Admin Dashboard");
        } else {
            UserDashboard userDashboard = new UserDashboard(user);
            stage.getScene().setRoot(userDashboard);
            stage.setTitle("Banking System - User Dashboard");
        }
    }
    
    private void clearFields() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        roleCombo.setValue("USER");
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
