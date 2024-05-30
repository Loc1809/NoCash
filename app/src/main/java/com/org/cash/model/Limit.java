package com.org.cash.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "limit")
public class Limit {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    Integer id;

    @ColumnInfo(name="amount")
    double amount;

    @ColumnInfo(name="category")
    String category;

    @ColumnInfo(name="startDate")
    long startDate;

    @ColumnInfo(name="endDate")
    long endDate;

    @ColumnInfo(name = "direction")
    Integer direction; // 0 for income, 1 for outcome

    public Limit() {
    }

    public Limit(double amount, String category, long startDate, long endDate, Integer type) {
        this.amount = amount;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.direction = type;
    }

    @Ignore
    public Limit(Integer id, double amount, String category, long startDate, long endDate, Integer type) {
        this.id = id;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getType() {
        return direction;
    }

    public void setType(Integer type) {
        this.direction = type;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }
}
