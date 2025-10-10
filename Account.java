/**
 * Abstract base class for all account types
 */
public abstract class Account {
    protected String accountNumber;
    protected double balance;
    protected String branch;
    
    public Account(String accountNumber, double initialBalance, String branch) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.branch = branch;
    }
    
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    public double getBalance() { 
        return balance; 
    }
    
    public String getAccountNumber() { 
        return accountNumber; 
    }
    
    public String getBranch() { 
        return branch; 
    }
    
    public abstract void calculateInterest();
    public abstract String getAccountType();
}