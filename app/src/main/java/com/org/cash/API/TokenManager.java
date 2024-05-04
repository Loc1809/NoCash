package com.org.cash.API;

public class TokenManager {
    private static TokenManager instance;
    private String token;

    private TokenManager() {
        // Private constructor to prevent instantiation from outside
    }

    public static synchronized TokenManager getInstance() {
        if (instance == null) {
            instance = new TokenManager();
        }
        return instance;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
