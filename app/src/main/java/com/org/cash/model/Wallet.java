package com.org.cash.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "wallet")
public class Wallet {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    int id;

    @ColumnInfo(name="name")
    String name;

    @ColumnInfo(name="amount")
    double amount;

    public Wallet(Integer id, String name, double amount) {
        this.id = id;
        this.name = name;
        this.amount = amount;
    }

    @Ignore
    public Wallet(String name, double amount) {
        this.name = name;
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
