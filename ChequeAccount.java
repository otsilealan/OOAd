/**
 * Cheque Account - for business use, no interest, withdrawals allowed
 */
public class ChequeAccount extends Account {
    private String companyName;
    private String companyAddress;
    
    public ChequeAccount(String accountNumber, double initialBalance, String branch, 
                        String companyName, String companyAddress) {
        super(accountNumber, initialBalance, branch);
        this.companyName = companyName;
        this.companyAddress = companyAddress;
    }
    
    public boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }
    
    public String getCompanyName() { 
        return companyName; 
    }
    
    public String getCompanyAddress() { 
        return companyAddress; 
    }
    
    @Override
    public void calculateInterest() {
        // No interest for cheque accounts
    }
    
    @Override
    public String getAccountType() { 
        return "Cheque"; 
    }
}