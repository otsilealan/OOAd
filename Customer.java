import java.util.ArrayList;

/**
 * Customer entity - holds customer information and their accounts
 */
public class Customer {
    private String firstName;
    private String lastName;
    private String address;
    private ArrayList<Account> accounts;
    
    public Customer(String firstName, String lastName, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.accounts = new ArrayList<>();
    }
    
    public void addAccount(Account account) {
        accounts.add(account);
    }
    
    public ArrayList<Account> getAccounts() { 
        return accounts; 
    }
    
    public String getFirstName() { 
        return firstName; 
    }
    
    public String getLastName() { 
        return lastName; 
    }
    
    public String getAddress() { 
        return address; 
    }
    
    public String getFullName() { 
        return firstName + " " + lastName; 
    }
}