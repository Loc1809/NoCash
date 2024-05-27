package com.org.cash.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private int id;
    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;
    private String phoneNumber;
    @SerializedName("email")
    private String email;
    @SerializedName("name")
    private String name;
    @SerializedName("identify_code")
    private String identifyCode;
    @SerializedName("date_of_birth")
    private String dateOfBirth;
    @SerializedName("role")
    private String role;
    @SerializedName("money")
    private Float money;
    @SerializedName("enabled")
    private Boolean enabled;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {
    }

    public User(String username,String email, String password) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = username;
        this.phoneNumber = "";
        this.dateOfBirth = "today";
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



    public Float getMoney() {
        return money;
    }

    public void setMoney(Float money) {
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

    public String getPassword() {
        return password;
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
