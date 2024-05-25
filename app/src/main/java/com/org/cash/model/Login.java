package com.org.cash.model;

public class Login {
    public String password;

    public Login(String username, String password) {
        this.password = password;
        this.username = username;
    }

    public String username;
}
