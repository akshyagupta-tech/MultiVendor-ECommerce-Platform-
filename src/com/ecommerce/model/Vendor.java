package com.ecommerce.model;

public class Vendor extends User {
    private String businessRegNo;
    private double balance;
    private double commissionRate;

    public Vendor(String id, String name, String email, String businessRegNo, double commissionRate) {
        super(id, name, email, "VENDOR");
        this.businessRegNo = businessRegNo;
        this.commissionRate = commissionRate;
        this.balance = 0.0;
    }

    public String getBusinessRegNo() { return businessRegNo; }
    public double getBalance() { return balance; }
    public double getCommissionRate() { return commissionRate; }

    public synchronized void creditBalance(double amount) {
        if (amount > 0) this.balance += amount;
    }

    @Override
    public String getRoleDetails() {
        return String.format("Vendor Reg: %s | Fee: %.1f%% | Balance: $%.2f",
                businessRegNo, commissionRate * 100, balance);
    }
}
