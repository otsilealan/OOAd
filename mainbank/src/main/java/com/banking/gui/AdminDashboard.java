package com.banking.gui;

import com.banking.model.User;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AdminDashboard extends BorderPane {
    private User currentUser;
    
    public AdminDashboard(User user) {
        this.currentUser = user;
        setupUI();
    }
    
    private void setupUI() {
        // Top menu bar
        MenuBar menuBar = new MenuBar();
        Menu userMenu = new Menu("Admin: " + currentUser.getUsername());
        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(e -> logout());
        userMenu.getItems().add(logoutItem);
        menuBar.getMenus().add(userMenu);
        
        setTop(menuBar);
        
        // Admin tabs
        TabPane tabPane = new TabPane();
        
        // User Management Tab
        Tab userTab = new Tab("User Management");
        userTab.setContent(new UserManagementPane());
        userTab.setClosable(false);
        
        // Customer Management Tab
        Tab customerTab = new Tab("Customer Management");
        customerTab.setContent(new CustomerManagementPane());
        customerTab.setClosable(false);
        
        // Account Management Tab
        Tab accountTab = new Tab("Account Management");
        accountTab.setContent(new AccountManagementPane(currentUser));
        accountTab.setClosable(false);
        
        // Reports Tab
        Tab reportsTab = new Tab("Reports");
        reportsTab.setContent(new Label("System Reports - Coming Soon"));
        reportsTab.setClosable(false);
        
        tabPane.getTabs().addAll(userTab, customerTab, accountTab, reportsTab);
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
