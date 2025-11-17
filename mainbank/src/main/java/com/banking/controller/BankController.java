package com.banking.controller;

import com.banking.model.Account;
import com.banking.model.Customer;
import com.banking.service.BankService;

public class BankController {
    private BankService bankService;

    public BankController() {
        this.bankService = new BankService();
    }

    public Customer createCustomer(String customerId, String firstName, String surname, 
                                 String address, String phoneNumber, String email) {
        return new Customer(customerId, firstName, surname, address, phoneNumber, email);
    }

    public Account openAccount(Customer customer, String accountType, double initialDeposit) {
        try {
            return bankService.openAccount(customer, accountType, initialDeposit);
        } catch (IllegalArgumentException e) {
            System.err.println("Error opening account: " + e.getMessage());
            return null;
        }
    }

    public boolean makeDeposit(String accountNumber, double amount) {
        if (amount <= 0) {
            System.err.println("Deposit amount must be positive");
            return false;
        }
        return bankService.deposit(accountNumber, amount);
    }

    public boolean makeWithdrawal(String accountNumber, double amount) {
        if (amount <= 0) {
            System.err.println("Withdrawal amount must be positive");
            return false;
        }
        return bankService.withdraw(accountNumber, amount);
    }

    public double checkBalance(String accountNumber) {
        return bankService.getBalance(accountNumber);
    }

    public void processMonthlyInterest() {
        bankService.payInterest();
        System.out.println("Monthly interest has been processed for all eligible accounts.");
    }

    public Account getAccountDetails(String accountNumber) {
        return bankService.getAccount(accountNumber);
    }

    public Customer getCustomerDetails(String customerId) {
        try {
            return bankService.getCustomer(Long.parseLong(customerId));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
