package org.mql.java.tests;


public class Customer {
    private String name;
    private int loyaltyYears;
    private double totalPurchases;

    public Customer(String name, int loyaltyYears, double totalPurchases) {
        this.name = name;
        this.loyaltyYears = loyaltyYears;
        this.totalPurchases = totalPurchases;
    }

    public int getLoyaltyYears() {
        return loyaltyYears;
    }

    public double getTotalPurchases() {
        return totalPurchases;
    }

    public String getName() {
        return name;
    }
}
