package com.org.cash.API;

import android.os.AsyncTask;
import com.org.cash.ui.profile.ProfileFragment;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public class FetchDataAsyncTask extends AsyncTask<Void, Void, Object> {

    private ApiService apiService;
    private ProfileFragment fragment;

    public FetchDataAsyncTask(ApiService apiService, ProfileFragment fragment) {
        this.apiService = apiService;
        this.fragment = fragment;
    }

    @Override
    protected Object doInBackground(Void... voids) {
        try {
            Call<Object> call = apiService.fetchData();
            Response<Object> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                // Xử lý lỗi
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    @Override
    protected void onPostExecute(Object responseData) {
        super.onPostExecute(responseData);
        if (responseData != null) {
            // Xử lý dữ liệu trả về
            fragment.handleData(responseData);
        } else {
            fragment.handleDacta();
        }
    }
}
