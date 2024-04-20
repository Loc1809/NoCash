package com.org.cash.API;


import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("/")
    Call<Object> fetchData();
}
