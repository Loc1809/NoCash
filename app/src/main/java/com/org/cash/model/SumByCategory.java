package com.org.cash.model;

public class SumByCategory {
    private double sum;
    private String category;

    public SumByCategory(double sum, String category) {
        this.sum = sum;
        this.category = category;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
