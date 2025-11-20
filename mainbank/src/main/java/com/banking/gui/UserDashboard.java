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
        UserAccountsPane accountsPane = new UserAccountsPane(currentUser);
        Tab accountsTab = new Tab("My Accounts");
        accountsTab.setContent(accountsPane);
        accountsTab.setClosable(false);
        
        // Refresh accounts when tab is selected
        accountsTab.setOnSelectionChanged(e -> {
            if (accountsTab.isSelected()) {
                accountsPane.refresh();
            }
        });
        
        // Transactions Tab
        Tab transactionsTab = new Tab("Transactions");
        transactionsTab.setContent(new TransactionPane());
        transactionsTab.setClosable(false);
        
        tabPane.getTabs().addAll(profileTab, accountsTab, transactionsTab);
        setCenter(tabPane);
    }
    
    private void logout() {
        javafx.scene.Scene scene = getScene();
        if (scene == null || scene.getWindow() == null) return;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            javafx.stage.Stage stage = (javafx.stage.Stage) scene.getWindow();
            LoginPane loginPane = new LoginPane(stage);
            scene.setRoot(loginPane);
            stage.setTitle("Banking System - Login");
        }
    }
}
