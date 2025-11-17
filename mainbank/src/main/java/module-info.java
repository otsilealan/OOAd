module banking.system {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    
    exports com.banking.gui;
    exports com.banking.model;
    exports com.banking.database;
    exports com.banking.service;
}
