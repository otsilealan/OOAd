package com.banking.gui;

import com.banking.model.User;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class MainBankingPane extends BorderPane {
    private User currentUser;
    private TabPane tabPane;
    
    public MainBankingPane(User user) {
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
        
        // Main content tabs
        tabPane = new TabPane();
        
        // Customer Management Tab
        Tab customerTab = new Tab("Customers");
        customerTab.setContent(new CustomerManagementPane());
        customerTab.setClosable(false);
        
        // Account Management Tab
        Tab accountTab = new Tab("Accounts");
        accountTab.setContent(new AccountManagementPane(currentUser));
        accountTab.setClosable(false);
        
        tabPane.getTabs().addAll(customerTab, accountTab);
        setCenter(tabPane);
    }
    
    private void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            LoginPane loginPane = new LoginPane((javafx.stage.Stage) getScene().getWindow());
            getScene().setRoot(loginPane);
            ((javafx.stage.Stage) getScene().getWindow()).setTitle("Banking System - Login");
        }
    }
}
