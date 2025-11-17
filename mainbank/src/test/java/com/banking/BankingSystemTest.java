package com.banking;

import com.banking.controller.BankController;
import com.banking.model.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class BankingSystemTest {
    
    @Test
    public void testCustomerCreation() {
        BankController controller = new BankController();
        Customer customer = controller.createCustomer("C001", "John", "Doe", "123 Main St", "1234567890", "john@email.com");
        
        assertNotNull(customer);
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getSurname());
    }
    
    @Test
    public void testSavingsAccountCreation() {
        BankController controller = new BankController();
        Customer customer = controller.createCustomer("C002", "Jane", "Smith", "456 Oak Ave", "0987654321", "jane@email.com");
        
        Account account = controller.openAccount(customer, "savings", 1000.0);
        
        assertNotNull(account);
        assertTrue(account instanceof SavingsAccount);
        assertEquals(1000.0, account.getBalance(), 0.01);
    }
    
    @Test
    public void testInvestmentAccountMinimumBalance() {
        BankController controller = new BankController();
        Customer customer = controller.createCustomer("C003", "Bob", "Johnson", "789 Pine St", "5555555555", "bob@email.com");
        
        // Should fail with less than BWP500
        Account account1 = controller.openAccount(customer, "investment", 400.0);
        assertNull(account1);
        
        // Should succeed with BWP500 or more
        Account account2 = controller.openAccount(customer, "investment", 500.0);
        assertNotNull(account2);
        assertTrue(account2 instanceof InvestmentAccount);
    }
    
    @Test
    public void testSavingsAccountNoWithdrawal() {
        BankController controller = new BankController();
        Customer customer = controller.createCustomer("C004", "Alice", "Brown", "321 Elm St", "1111111111", "alice@email.com");
        
        Account account = controller.openAccount(customer, "savings", 1000.0);
        assertNotNull(account);
        
        // Savings account should not allow withdrawals
        boolean result = controller.makeWithdrawal(account.getAccountNumber(), 100.0);
        assertFalse(result);
        assertEquals(1000.0, account.getBalance(), 0.01);
    }
    
    @Test
    public void testInvestmentAccountWithdrawal() {
        BankController controller = new BankController();
        Customer customer = controller.createCustomer("C005", "Charlie", "Wilson", "654 Maple Ave", "2222222222", "charlie@email.com");
        
        Account account = controller.openAccount(customer, "investment", 1000.0);
        assertNotNull(account);
        
        // Investment account should allow withdrawals
        boolean result = controller.makeWithdrawal(account.getAccountNumber(), 200.0);
        assertTrue(result);
        assertEquals(800.0, account.getBalance(), 0.01);
    }
}
