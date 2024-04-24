package com.org.cash.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "trans")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "amount")
    private Double amount;

    @ColumnInfo(name = "time")
    private Long time;

    @ColumnInfo(name = "desc")
    private String desc;

    @ColumnInfo(name = "wallet")
    private String wallet;

    @ColumnInfo(name = "direction")
    private int direction; // 0 for income, 1 for outcome

    @ColumnInfo(name = "category")
    private String category; // Foreign key referencing Category table

    @Ignore
    private User user;

    @ColumnInfo(name = "active")
    private Boolean active;

    public Transaction(Double amount, Long time, String desc, String category, int direction) {
        this.amount = amount;
        this.time = time;
        this.desc = desc;
        this.category = category;
        this.direction = direction;
        this.active = true;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTimeString() {
        return convertEpochToDateString(this.time);
    }

    public Long getTime() {
        return this.time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public User getUser() {
        return user;
    }

//    public User getUserInfo() { return this.user; }

    public void setUser(User user) {
        this.user = user;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public int direction(){
        return direction;
    }

    public String getDirection() {
        if (direction == 0)
            return "income";
        return "outcome";
    }

    public int actualDirection(){
        return this.direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category.getName();
    }


    public String convertEpochToDateString(Long epoch) {
        if (epoch == null) {
            return null;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = new Date(epoch);
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
