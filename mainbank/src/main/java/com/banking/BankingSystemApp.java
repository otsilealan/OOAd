package com.banking;

import com.banking.gui.BankingApp;
import com.banking.view.BankingSystemView;

public class BankingSystemApp {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--console")) {
            // Console mode
            System.out.println("Welcome to the Banking System!");
            System.out.println("This system demonstrates OOP principles including:");
            System.out.println("- Inheritance (Account -> SavingsAccount, InvestmentAccount, ChequeAccount)");
            System.out.println("- Polymorphism (Different account types with different behaviors)");
            System.out.println("- Abstraction (Abstract Account class)");
            System.out.println("- Interface implementation (BankingOperations)");
            System.out.println("- Encapsulation (Private fields with getters/setters)");
            
            BankingSystemView view = new BankingSystemView();
            view.run();
        } else {
            // GUI mode (default)
            BankingApp.main(args);
        }
    }
}
