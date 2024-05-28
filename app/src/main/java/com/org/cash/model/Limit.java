package com.org.cash.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "limit")
public class Limit {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    int id = -1;

    @ColumnInfo(name="amount")
    double amount;

    @ColumnInfo(name="category")
    String category;

    @ColumnInfo(name="startDate")
    long startDate;

    @ColumnInfo(name="endDate")
    long endDate;

    @ColumnInfo(name = "direction")
    int direction; // 0 for income, 1 for outcome

    public Limit() {
    }

    public Limit(double amount, String category, long startDate, long endDate, int type) {
        this.amount = amount;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.direction = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getType() {
        return direction;
    }

    public void setType(int type) {
        this.direction = type;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
