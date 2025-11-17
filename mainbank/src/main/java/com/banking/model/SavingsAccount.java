package com.banking.model;

public class SavingsAccount extends Account {
    private static final double INTEREST_RATE = 0.0005; // 0.05% monthly

    public SavingsAccount(String accountNumber, String branch, Customer customer) {
        super(accountNumber, branch, customer);
    }

    @Override
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    @Override
    public boolean withdraw(double amount) {
        // Savings account does not allow withdrawals
        return false;
    }

    @Override
    public double calculateInterest() {
        return balance * INTEREST_RATE;
    }

    public void payInterest() {
        double interest = calculateInterest();
        balance += interest;
    }
    
    public double getInterestRate() {
        return INTEREST_RATE;
    }
}
