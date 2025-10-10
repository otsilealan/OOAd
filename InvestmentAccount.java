/**
 * Investment Account - high interest rate, withdrawals allowed, minimum deposit required
 */
public class InvestmentAccount extends Account {
    private static final double INTEREST_RATE = 0.05; // 5% monthly
    private static final double MIN_INITIAL_DEPOSIT = 500.00;
    
    public InvestmentAccount(String accountNumber, double initialBalance, String branch) {
        super(accountNumber, initialBalance, branch);
        if (initialBalance < MIN_INITIAL_DEPOSIT) {
            throw new IllegalArgumentException("Investment Account requires minimum BWP500.00");
        }
    }
    
    public boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }
    
    @Override
    public void calculateInterest() {
        double interest = balance * INTEREST_RATE;
        balance += interest;
    }
    
    @Override
    public String getAccountType() { 
        return "Investment"; 
    }
}