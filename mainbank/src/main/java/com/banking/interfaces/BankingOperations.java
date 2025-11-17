package com.banking.interfaces;

import com.banking.model.Account;
import com.banking.model.Customer;

public interface BankingOperations {
    Account openAccount(Customer customer, String accountType, double initialDeposit);
    boolean deposit(String accountNumber, double amount);
    boolean withdraw(String accountNumber, double amount);
    double getBalance(String accountNumber);
    void payInterest();
}
