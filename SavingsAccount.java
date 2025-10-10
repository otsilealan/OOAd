/**
 * Savings Account - earns interest, no withdrawals allowed
 */
public class SavingsAccount extends Account {
    private static final double INTEREST_RATE = 0.0005; // 0.05% monthly
    
    public SavingsAccount(String accountNumber, double initialBalance, String branch) {
        super(accountNumber, initialBalance, branch);
    }
    
    @Override
    public void calculateInterest() {
        double interest = balance * INTEREST_RATE;
        balance += interest;
    }
    
    @Override
    public String getAccountType() { 
        return "Savings"; 
    }
}