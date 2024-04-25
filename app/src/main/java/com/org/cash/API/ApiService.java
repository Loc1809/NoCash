package com.org.cash.API;


import com.org.cash.model.ChangePwd;
import com.org.cash.model.User;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @PUT("/user/update/pwd")
    Call<User> updatePwdData(@Body ChangePwd pwd, @Header("Authorization") String auth);
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @PUT("/user/update")
    Call<User> updateData(@Body User user , @Header("Authorization") String auth);
   // @Headers({"Authorization", "Bearer "+ "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJbW1JPTEVfdXNlcl1dIiwidXNlcm5hbWUiOiJsb2N0ZXN0MyIsImV4cCI6MTcxNDAwNTEwMX0.iY6Ce7SN6tRcwstxc2DmKds9cQV70EvmkWNBIxAfabFHOVm_PUI1C3b4HWTG02TMxKT7HE3jEb6Cl24mU81d1Q"})
   @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @GET("/user/current")
    Call<User> fetchData(@Header("Authorization") String auth);
}
