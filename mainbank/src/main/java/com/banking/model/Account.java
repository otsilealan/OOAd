package com.banking.model;

import java.time.LocalDateTime;

public abstract class Account {
    protected String accountNumber;
    protected double balance;
    protected String branch;
    protected Customer customer;
    protected LocalDateTime dateOpened;

    public Account(String accountNumber, String branch, Customer customer) {
        this.accountNumber = accountNumber;
        this.branch = branch;
        this.customer = customer;
        this.balance = 0.0;
        this.dateOpened = LocalDateTime.now();
    }

    public abstract void deposit(double amount);
    public abstract boolean withdraw(double amount);
    public abstract double calculateInterest();

    // Getters and Setters
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public String getBranch() { return branch; }
    public Customer getCustomer() { return customer; }
    public LocalDateTime getDateOpened() { return dateOpened; }
    
    protected void setBalance(double balance) { this.balance = balance; }
}
