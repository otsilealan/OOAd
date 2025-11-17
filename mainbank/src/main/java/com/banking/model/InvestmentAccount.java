package com.banking.model;

public class InvestmentAccount extends Account {
    private static final double INTEREST_RATE = 0.05; // 5% monthly
    private static final double MINIMUM_OPENING_BALANCE = 500.00;

    public InvestmentAccount(String accountNumber, String branch, Customer customer, double initialDeposit) {
        super(accountNumber, branch, customer);
        if (initialDeposit >= MINIMUM_OPENING_BALANCE) {
            this.balance = initialDeposit;
        } else {
            throw new IllegalArgumentException("Investment account requires minimum BWP500.00 opening balance");
        }
    }

    @Override
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        }
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
}
