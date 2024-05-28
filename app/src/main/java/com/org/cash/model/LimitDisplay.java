package com.org.cash.model;

public class LimitDisplay {
    int id = -1;
    double limitAmount;
    double actualAmount;
    String details;
    String category;
    int icon;
    int direction;
    int progress;

    public LimitDisplay(int id, double limitAmount, double actualAmount, String details, String category, int icon, int direction, int progress) {
        this.id = id;
        this.limitAmount = limitAmount;
        this.actualAmount = actualAmount;
        this.details = details;
        this.category = category;
        this.icon = icon;
        this.direction = direction;
        this.progress = progress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(double limitAmount) {
        this.limitAmount = limitAmount;
    }

    public double getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(double actualAmount) {
        this.actualAmount = actualAmount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
