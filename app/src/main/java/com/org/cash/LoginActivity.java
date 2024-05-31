package com.org.cash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.org.cash.API.ApiService;
import com.org.cash.API.RetroFitConnection;
import com.org.cash.API.TokenManager;
import com.org.cash.model.Login;
import com.org.cash.model.TokenResponse;
import com.org.cash.model.User;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class LoginActivity extends AppCompatActivity {

    ApiService apiService = RetroFitConnection.getInstance().getRetrofit().create(ApiService.class);
    NavigationView backToRegisterNavigationView;
    EditText mail;
    EditText password;
    Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.mail = findViewById(R.id.editTextEmail_login);
        this.password = findViewById(R.id.editTextPassword_login);
        this.login = findViewById(R.id.login);
        //performLogin(null, null);


        login.setOnClickListener(view -> {
            String inputMail = mail.getText().toString().trim();
            String inputPassword = password.getText().toString().trim();

            // Validate inputs (You can add more validation if needed)
            if (inputMail.isEmpty() || inputPassword.isEmpty()) {
                showToast("Please fill in all fields");
            } else {
                // Call API for login

                performLogin(inputMail, inputPassword);

            }
        });


        NavigationView backToRegisterNavigationView = findViewById(R.id.back_to_register);

        backToRegisterNavigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.back_to_register_item) {
                navigateToRegisterActivity();
                return true;
            }
            return false;
        });

    }


    // Helper method to perform login (simulated API call)
    private void performLogin(String email, String password) {
//        navigateToMainActivity();
        AtomicReference<Call<TokenResponse>> call = new AtomicReference<>(apiService.login(email,password));
        call.get().enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                //Log.e("ProfileFragment", "Error: " + response);
                TokenResponse resource = response.body();

                    if(simulateLoginAPI(resource)) {
                        showToast("Login success");
                        navigateToMainActivity();
                    }
                    else {
                        showToast("Login failed. Please check your credentials.");
                    }

            }
            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                showToast("Login failed.");
                call.cancel();
            }
        });
    }

    // Simulated API call (replace this with actual API integration)
    private boolean simulateLoginAPI(TokenResponse resource){
        // Simulate successful login for demonstration

        if(resource==null){
            Log.e("gg","eror");
            return false;
        }
        else{

            //Log.e("ProfileFragment", "Error: "+ resource.token);
//            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("token",resource.token);
//            editor.apply();
            TokenManager.getInstance().setToken(resource.token);
            return true;
        }
    }

    // Helper method to navigate to RegisterActivity
    private void navigateToRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish(); // Finish the LoginActivity to prevent returning back to it
    }

    // Helper method to navigate to MainActivity
    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Finish the LoginActivity to prevent returning back to it
    }

    // Helper method to show toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}