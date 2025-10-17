package com.example.banking.controllers;

import com.example.banking.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Window;
import java.io.IOException;
import java.util.List;

public class MainController extends BaseController {
    @FXML private ListView<Customer> customerList;
    @FXML private ListView<Account> accountList;
    @FXML private VBox root;
    @FXML private TextField customerSearchField;
    @FXML private TextField accountSearchField;
    @FXML private ComboBox<String> accountTypeFilter;
    
    private final ObservableList<Customer> customers = FXCollections.observableArrayList();
    private final ObservableList<Account> accounts = FXCollections.observableArrayList();
    private final BooleanProperty noCustomerSelected = new SimpleBooleanProperty(true);
    private final BooleanProperty noAccountSelected = new SimpleBooleanProperty(true);
    
    public BooleanProperty noCustomerSelectedProperty() {
        return noCustomerSelected;
    }
    
    public BooleanProperty noAccountSelectedProperty() {
        return noAccountSelected;
    }

    @FXML
    private void initialize() {
        customerList.setItems(customers);
        accountList.setItems(accounts);
        
        // Initialize account type filter
        accountTypeFilter.getItems().addAll("All", "Savings", "Cheque", "Investment");
        accountTypeFilter.setValue("All");
        
        // Set accessibility properties
        customerList.setAccessibleRole(AccessibleRole.LIST_VIEW);
        customerList.setAccessibleRoleDescription("List of bank customers");
        customerList.setAccessibleHelp("Use up and down arrows to navigate, Enter to select a customer");
        
        accountList.setAccessibleRole(AccessibleRole.LIST_VIEW);
        accountList.setAccessibleRoleDescription("List of accounts for the selected customer");
        accountList.setAccessibleHelp("Use up and down arrows to navigate, Enter to select an account");
        
        // Set up search functionality
        setupSearchFilters();
        
        // Set cell factories for custom display with accessibility
        customerList.setCellFactory(lv -> new ListCell<Customer>() {
            @Override
            protected void updateItem(Customer customer, boolean empty) {
                super.updateItem(customer, empty);
                if (empty || customer == null) {
                    setText(null);
                    setAccessibleText(null);
                } else {
                    setText(customer.getName());
                    setAccessibleText("Customer " + customer.getName());
                }
            }
        });
        
        accountList.setCellFactory(lv -> new ListCell<Account>() {
            @Override
            protected void updateItem(Account account, boolean empty) {
                super.updateItem(account, empty);
                if (empty || account == null) {
                    setText(null);
                    setAccessibleText(null);
                } else {
                    String displayText = String.format("%s - Balance: $%.2f", 
                        account.getAccountType(), 
                        account.getBalance());
                    setText(displayText);
                    setAccessibleText(displayText);
                }
            }
        });
        
        // Add keyboard event handlers
        customerList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleNewAccount();
                event.consume();
            }
        });
        
        accountList.setOnKeyPressed(event -> {
            Customer selectedCustomer = customerList.getSelectionModel().getSelectedItem();
            if (selectedCustomer == null) return;
            
            switch (event.getCode()) {
                case ENTER -> handleViewAccountDetails();
                case D -> {
                    if (event.isControlDown()) handleDeposit();
                }
                case W -> {
                    if (event.isControlDown()) handleWithdraw();
                }
                case T -> {
                    if (event.isControlDown()) handleTransfer();
                }
                case S -> {
                    if (event.isControlDown()) handleViewStatement();
                }
            }
        });
        
        // Listen to customer selection changes
        customerList.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> updateAccountList(newVal)
        );
        
        refreshCustomerList();
    }
    
    private void setupSearchFilters() {
        // Customer search
        customerSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            customers.setAll(Bank.getAllCustomers().stream()
                .filter(c -> newVal == null || newVal.isEmpty() ||
                    c.getName().toLowerCase().contains(newVal.toLowerCase()))
                .toList());
        });
        
        // Account search and type filter
        accountSearchField.textProperty().addListener((obs, oldVal, newVal) -> filterAccounts());
        accountTypeFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterAccounts());
    }
    
    private void filterAccounts() {
        Customer selectedCustomer = customerList.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            accounts.clear();
            return;
        }
        
        String searchText = accountSearchField.getText();
        String typeFilter = accountTypeFilter.getValue();
        
        accounts.setAll(Bank.getAccountsForCustomer(selectedCustomer.getId()).stream()
            .filter(a -> searchText == null || searchText.isEmpty() ||
                a.getId().toLowerCase().contains(searchText.toLowerCase()))
            .filter(a -> typeFilter == null || typeFilter.equals("All") ||
                a.getAccountType().equalsIgnoreCase(typeFilter))
            .toList());
    }
    
    private void refreshCustomerList() {
        String searchText = customerSearchField.getText();
        customers.setAll(Bank.getAllCustomers().stream()
            .filter(c -> searchText == null || searchText.isEmpty() ||
                c.getName().toLowerCase().contains(searchText.toLowerCase()))
            .toList());
    }
    
    private void updateAccountList(Customer customer) {
        accounts.clear();
        if (customer != null) {
            List<Account> customerAccounts = Bank.getAccountsForCustomer(customer.getId());
            String searchText = accountSearchField.getText();
            String typeFilter = accountTypeFilter.getValue();
            
            accounts.addAll(customerAccounts.stream()
                .filter(a -> searchText == null || searchText.isEmpty() ||
                    a.getId().toLowerCase().contains(searchText.toLowerCase()))
                .filter(a -> typeFilter == null || typeFilter.equals("All") ||
                    a.getAccountType().equalsIgnoreCase(typeFilter))
                .toList());
        }
        
        // Update selection state
        noCustomerSelected.set(customer == null);
    }
    
    @FXML
    private void handleNewCustomer() throws IOException {
        Stage stage = createStage("New Customer", "/com/example/banking/views/customer.fxml");
        stage.showAndWait();
        refreshCustomerList();
    }
    
    @FXML
    private void handleNewAccount() throws IOException {
        Customer selected = customerList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Error", "Please select a customer first");
            return;
        }
        
        Stage stage = createStage("New Account", "/com/example/banking/views/account.fxml");
        AccountController controller = stage.getUserData();
        controller.setCustomer(selected);
        stage.showAndWait();
        updateAccountList(selected);
    }
    
    @FXML
    private void handleDeposit() throws IOException {
        Account selected = accountList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        Stage stage = createStage("Deposit", "/com/example/banking/views/transaction.fxml");
        TransactionController controller = stage.getUserData();
        controller.initializeDeposit(selected);
        stage.showAndWait();
        updateAccountList(customerList.getSelectionModel().getSelectedItem());
    }
    
    @FXML
    private void handleWithdraw() throws IOException {
        Account selected = accountList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        Stage stage = createStage("Withdraw", "/com/example/banking/views/transaction.fxml");
        TransactionController controller = stage.getUserData();
        controller.initializeWithdrawal(selected);
        stage.showAndWait();
        updateAccountList(customerList.getSelectionModel().getSelectedItem());
    }
    
    @FXML
    private void handleTransfer() throws IOException {
        Account selected = accountList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        Stage stage = createStage("Transfer", "/com/example/banking/views/transaction.fxml");
        TransactionController controller = stage.getUserData();
        controller.initializeTransfer(selected);
        stage.showAndWait();
        updateAccountList(customerList.getSelectionModel().getSelectedItem());
    }
    
    @FXML
    private void handleViewStatement() throws IOException {
        Account selected = accountList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        Stage stage = createStage("Account Statement", "/com/example/banking/views/statement.fxml");
        StatementController controller = stage.getUserData();
        controller.initializeStatement(selected);
        stage.show();
    }
    
    @FXML
    private void handleViewCustomers() {
        refreshCustomerList();
    }
    
    @FXML
    private void handleViewAccounts() {
        Customer selected = customerList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            updateAccountList(selected);
        }
    }
    
    @FXML
    private void handleViewAccountDetails() {
        Account selected = accountList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Error", "Please select an account first");
            return;
        }
        
        // TODO: Show account details dialog
    }
    
    @FXML
    private void handleHelp() throws IOException {
        Stage stage = createStage("Banking System Help", "/com/example/banking/views/help.fxml");
        stage.show();
    }

    @FXML
    private void handleExit() {
        if (DialogUtils.showConfirm(getWindow(), "Exit", "Are you sure you want to exit?")) {
            closeCurrentWindow();
        }
    }

    @FXML
    private void handleAbout() {
        DialogUtils.showInfo(getWindow(), "About Banking System", 
            "Banking System v1.0\n\n" +
            "A demonstration application for managing bank accounts and transactions.\n\n" +
            "Features:\n" +
            "• Customer management\n" +
            "• Multiple account types\n" +
            "• Transaction processing\n" +
            "• Account statements\n" +
            "• Real-time balance updates\n\n" +
            "© 2025 Banking System Demo");
    }
    
    @Override
    protected Window getWindow() {
        return root.getScene().getWindow();
    }
}