package com.banking.gui;

import com.banking.model.User;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class UserDashboard extends BorderPane {
    private User currentUser;
    
    public UserDashboard(User user) {
        this.currentUser = user;
        setupUI();
    }
    
    private void setupUI() {
        // Top menu bar
        MenuBar menuBar = new MenuBar();
        Menu userMenu = new Menu("User: " + currentUser.getUsername());
        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(e -> logout());
        userMenu.getItems().add(logoutItem);
        menuBar.getMenus().add(userMenu);
        
        setTop(menuBar);
        
        // User tabs (limited access)
        TabPane tabPane = new TabPane();
        
        // My Profile Tab
        Tab profileTab = new Tab("My Profile");
        profileTab.setContent(new UserProfilePane(currentUser));
        profileTab.setClosable(false);
        
        // My Accounts Tab
        Tab accountsTab = new Tab("My Accounts");
        accountsTab.setContent(new UserAccountsPane(currentUser));
        accountsTab.setClosable(false);
        
        tabPane.getTabs().addAll(profileTab, accountsTab);
        setCenter(tabPane);
    }
    
    private void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            LoginPane loginPane = new LoginPane((javafx.stage.Stage) getScene().getWindow());
            getScene().setRoot(loginPane);
            ((javafx.stage.Stage) getScene().getWindow()).setTitle("Banking System - Login");
        }
    }
}
