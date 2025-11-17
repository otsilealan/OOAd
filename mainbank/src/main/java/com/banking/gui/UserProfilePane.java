package com.banking.gui;

import com.banking.model.User;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class UserProfilePane extends VBox {
    private User currentUser;
    
    public UserProfilePane(User user) {
        this.currentUser = user;
        setupUI();
    }
    
    private void setupUI() {
        setSpacing(15);
        setPadding(new Insets(20));
        
        Label title = new Label("My Profile");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Profile Information
        VBox profileBox = new VBox(10);
        profileBox.setPadding(new Insets(15));
        profileBox.setStyle("-fx-border-color: gray; -fx-border-radius: 5;");
        
        GridPane profileGrid = new GridPane();
        profileGrid.setHgap(15);
        profileGrid.setVgap(10);
        
        profileGrid.add(new Label("Username:"), 0, 0);
        profileGrid.add(new Label(currentUser.getUsername()), 1, 0);
        
        profileGrid.add(new Label("Email:"), 0, 1);
        profileGrid.add(new Label(currentUser.getEmail()), 1, 1);
        
        profileGrid.add(new Label("Role:"), 0, 2);
        profileGrid.add(new Label(currentUser.getRole()), 1, 2);
        
        profileBox.getChildren().addAll(new Label("Profile Information"), profileGrid);
        
        // Change Password Section
        VBox passwordBox = new VBox(10);
        passwordBox.setPadding(new Insets(15));
        passwordBox.setStyle("-fx-border-color: gray; -fx-border-radius: 5;");
        
        Label passwordTitle = new Label("Change Password");
        passwordTitle.setStyle("-fx-font-weight: bold;");
        
        GridPane passwordGrid = new GridPane();
        passwordGrid.setHgap(10);
        passwordGrid.setVgap(5);
        
        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current Password");
        
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");
        
        passwordGrid.add(new Label("Current Password:"), 0, 0);
        passwordGrid.add(currentPasswordField, 1, 0);
        passwordGrid.add(new Label("New Password:"), 0, 1);
        passwordGrid.add(newPasswordField, 1, 1);
        passwordGrid.add(new Label("Confirm Password:"), 0, 2);
        passwordGrid.add(confirmPasswordField, 1, 2);
        
        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setOnAction(e -> {
            // Password change logic would go here
            showAlert("Info", "Password change functionality not implemented yet.");
        });
        
        passwordBox.getChildren().addAll(passwordTitle, passwordGrid, changePasswordButton);
        
        getChildren().addAll(title, profileBox, passwordBox);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
