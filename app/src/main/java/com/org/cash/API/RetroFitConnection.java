package com.org.cash.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroFitConnection {
    private static RetroFitConnection instance;
    private Retrofit retrofit;

    private RetroFitConnection() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetroFitConnection getInstance() {
        if (instance == null) {
            instance = new RetroFitConnection();
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
