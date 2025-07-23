package org.mql.java.tests;


public class OrderAnalyzer {

    public double calculateDiscount(Customer customer) {
        double total = customer.getTotalPurchases();
        int loyaltyYears = customer.getLoyaltyYears();

        if (loyaltyYears > 5 && total > 1000) {
            return total * 0.15;
        } else if (total > 500) {
            return total * 0.10;
        } else {
            return 0;
        }
    }
    public void bar() {
        int x = 10;
        System.out.println(x);
    }

}
