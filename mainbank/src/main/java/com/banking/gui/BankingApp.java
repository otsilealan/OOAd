package com.banking.gui;

import com.banking.database.DatabaseManager;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class BankingApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        DatabaseManager.initializeDatabase();
        
        primaryStage.setTitle("Banking System - Login");
        
        // Create login pane centered in the window
        StackPane root = new StackPane();
        LoginPane loginPane = new LoginPane(primaryStage);
        root.getChildren().add(loginPane);
        root.setAlignment(Pos.CENTER);
        
        Scene scene = new Scene(root, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
