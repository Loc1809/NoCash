package com.org.cash.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {
    @PrimaryKey
    @ColumnInfo(name="id")
    int id;

    @ColumnInfo(name = "username")
    String username;

    @ColumnInfo(name = "password")
    String password; // encoded password

    @ColumnInfo(name = "phone_number")
    String phoneNumber;

    @ColumnInfo(name = "email")
    String email;

    @ColumnInfo(name = "name")
    String name;

    @ColumnInfo(name = "identify_code")
    String identifyCode;

    @ColumnInfo(name = "date_of_birth")
    String dateOfBirth; // stored date as string

    @ColumnInfo(name = "role")
    String role;

    @ColumnInfo(name = "money")
    Double money;

    @ColumnInfo(name = "enabled")
    Boolean enabled;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {
    }

    public User(String username, String password, String phoneNumber, String email, String name, String identifyCode, String dateOfBirth, String role) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.name = name;
        this.identifyCode = identifyCode;
        this.dateOfBirth = dateOfBirth;
        this.role = role;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean active) {
        this.enabled = active;
    }

//    public User parent(){return parent;}

//    public String getParent(){
//        try {
//            return parent.getName();
//        } catch (Exception e) {
//            return "";
//        }
//    }
//
//    public int parentId() {
//        try {
//            return parent.getId();
//        } catch (Exception e) {
//            return 0;
//        }
//    }

//    public void setParent(User parent) {
//        this.parent = parent;
//    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String password(){
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifyCode() {
        return identifyCode;
    }

    public void setIdentifyCode(String identifyCode) {
        this.identifyCode = identifyCode;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
