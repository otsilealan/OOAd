package com.banking.view;

import com.banking.controller.BankController;
import com.banking.model.Account;
import com.banking.model.Customer;
import java.util.Scanner;

public class BankingSystemView {
    private BankController controller;
    private Scanner scanner;

    public BankingSystemView() {
        this.controller = new BankController();
        this.scanner = new Scanner(System.in);
    }

    public void displayMainMenu() {
        System.out.println("\n=== BANKING SYSTEM ===");
        System.out.println("1. Create Customer");
        System.out.println("2. Open Account");
        System.out.println("3. Make Deposit");
        System.out.println("4. Make Withdrawal");
        System.out.println("5. Check Balance");
        System.out.println("6. Process Monthly Interest");
        System.out.println("7. Exit");
        System.out.print("Choose an option: ");
    }

    public void run() {
        while (true) {
            displayMainMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    createCustomer();
                    break;
                case 2:
                    openAccount();
                    break;
                case 3:
                    makeDeposit();
                    break;
                case 4:
                    makeWithdrawal();
                    break;
                case 5:
                    checkBalance();
                    break;
                case 6:
                    controller.processMonthlyInterest();
                    break;
                case 7:
                    System.out.println("Thank you for using Banking System!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void createCustomer() {
        System.out.print("Enter Customer ID: ");
        String customerId = scanner.nextLine();
        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter Surname: ");
        String surname = scanner.nextLine();
        System.out.print("Enter Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter Phone Number: ");
        String phoneNumber = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        Customer customer = controller.createCustomer(customerId, firstName, surname, address, phoneNumber, email);
        System.out.println("Customer created successfully: " + customer.getFirstName() + " " + customer.getSurname());
    }

    private void openAccount() {
        System.out.print("Enter Customer ID: ");
        String customerId = scanner.nextLine();
        Customer customer = controller.getCustomerDetails(customerId);
        
        if (customer == null) {
            System.out.println("Customer not found. Please create customer first.");
            return;
        }

        System.out.println("Account Types:");
        System.out.println("1. Savings (0.05% monthly interest, no withdrawals)");
        System.out.println("2. Investment (5% monthly interest, BWP500 minimum)");
        System.out.println("3. Cheque (no interest, for salary payments)");
        System.out.print("Choose account type (1-3): ");
        
        int accountTypeChoice = scanner.nextInt();
        scanner.nextLine();
        
        String accountType = "";
        switch (accountTypeChoice) {
            case 1: accountType = "savings"; break;
            case 2: accountType = "investment"; break;
            case 3: accountType = "cheque"; break;
            default:
                System.out.println("Invalid account type.");
                return;
        }

        System.out.print("Enter initial deposit amount: ");
        double initialDeposit = scanner.nextDouble();
        scanner.nextLine();

        Account account = controller.openAccount(customer, accountType, initialDeposit);
        if (account != null) {
            System.out.println("Account opened successfully! Account Number: " + account.getAccountNumber());
        } else {
            System.out.println("Failed to open account.");
        }
    }

    private void makeDeposit() {
        System.out.print("Enter Account Number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter deposit amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        if (controller.makeDeposit(accountNumber, amount)) {
            System.out.println("Deposit successful! New balance: BWP" + controller.checkBalance(accountNumber));
        } else {
            System.out.println("Deposit failed.");
        }
    }

    private void makeWithdrawal() {
        System.out.print("Enter Account Number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter withdrawal amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        if (controller.makeWithdrawal(accountNumber, amount)) {
            System.out.println("Withdrawal successful! New balance: BWP" + controller.checkBalance(accountNumber));
        } else {
            System.out.println("Withdrawal failed. Check account type and balance.");
        }
    }

    private void checkBalance() {
        System.out.print("Enter Account Number: ");
        String accountNumber = scanner.nextLine();
        double balance = controller.checkBalance(accountNumber);
        System.out.println("Current balance: BWP" + balance);
    }
}
