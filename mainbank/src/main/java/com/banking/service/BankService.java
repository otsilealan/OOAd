package com.banking.service;

import com.banking.interfaces.BankingOperations;
import com.banking.model.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BankService implements BankingOperations {
    private Map<String, Account> accounts;
    private Map<Long, Customer> customers;
    private Random random;

    public BankService() {
        this.accounts = new HashMap<>();
        this.customers = new HashMap<>();
        this.random = new Random();
    }

    @Override
    public Account openAccount(Customer customer, String accountType, double initialDeposit) {
        String accountNumber = generateAccountNumber();
        Account account = null;

        switch (accountType.toLowerCase()) {
            case "savings":
                account = new SavingsAccount(accountNumber, "Main Branch", customer);
                if (initialDeposit > 0) {
                    account.deposit(initialDeposit);
                }
                break;
            case "investment":
                account = new InvestmentAccount(accountNumber, "Main Branch", customer, initialDeposit);
                break;
            case "cheque":
                // For demo purposes, using default employer info
                account = new ChequeAccount(accountNumber, "Main Branch", customer, "Default Employer", "Default Address");
                if (initialDeposit > 0) {
                    account.deposit(initialDeposit);
                }
                break;
        }

        if (account != null) {
            accounts.put(accountNumber, account);
            customer.addAccount(account);
            customers.put(customer.getCustomerId(), customer);
        }

        return account;
    }

    @Override
    public boolean deposit(String accountNumber, double amount) {
        Account account = accounts.get(accountNumber);
        if (account != null) {
            account.deposit(amount);
            return true;
        }
        return false;
    }

    @Override
    public boolean withdraw(String accountNumber, double amount) {
        Account account = accounts.get(accountNumber);
        if (account != null) {
            return account.withdraw(amount);
        }
        return false;
    }

    @Override
    public double getBalance(String accountNumber) {
        Account account = accounts.get(accountNumber);
        return account != null ? account.getBalance() : 0.0;
    }

    @Override
    public void payInterest() {
        for (Account account : accounts.values()) {
            if (account instanceof SavingsAccount) {
                ((SavingsAccount) account).payInterest();
            } else if (account instanceof InvestmentAccount) {
                ((InvestmentAccount) account).payInterest();
            }
        }
    }

    private String generateAccountNumber() {
        return "ACC" + (100000 + random.nextInt(900000));
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public Customer getCustomer(Long customerId) {
        return customers.get(customerId);
    }
}
