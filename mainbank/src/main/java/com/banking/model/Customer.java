package com.banking.model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private Long customerId;
    private Long userId;
    private String firstName;
    private String surname;
    private String address;
    private String phoneNumber;
    private String email;
    private List<Account> accounts;

    public Customer(String name, String email, String phone) {
        String[] nameParts = name.split(" ", 2);
        this.firstName = nameParts[0];
        this.surname = nameParts.length > 1 ? nameParts[1] : "";
        this.email = email;
        this.phoneNumber = phone;
        this.accounts = new ArrayList<>();
    }

    public Customer(String customerId, String firstName, String surname, String address, String phoneNumber, String email) {
        this.customerId = Long.parseLong(customerId);
        this.firstName = firstName;
        this.surname = surname;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.accounts = new ArrayList<>();
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
    }

    // Getters and Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getName() { return firstName + " " + surname; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getPhone() { return phoneNumber; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public List<Account> getAccounts() { return accounts; }
    
    @Override
    public String toString() {
        return getName() + " (" + email + ")";
    }
}
