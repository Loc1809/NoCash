package com.org.cash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import com.org.cash.API.ApiService;
import com.org.cash.API.RetroFitConnection;
import com.org.cash.model.TokenResponse;
import com.org.cash.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.concurrent.atomic.AtomicReference;

public class RegisterActivity extends AppCompatActivity {
    ApiService apiService = RetroFitConnection.getInstance().getRetrofit().create(ApiService.class);

    NavigationView backToInstructNavigationView;
    EditText name;
    EditText mail;
    EditText password;
    Switch confirm;
    Button register;
    Button login;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.name = findViewById(R.id.editTextName);
        this.mail = findViewById(R.id.editTextEmail);
        this.password = findViewById(R.id.editTextPassword);
        this.login = findViewById(R.id.buttonLogin);
        this.register = findViewById(R.id.buttonRegister);
        this.confirm = findViewById(R.id.switch1);

        // Set click listener for register button
        register.setOnClickListener(view -> {
            String inputName = name.getText().toString().trim();
            String inputMail = mail.getText().toString().trim();
            String inputPassword = password.getText().toString().trim();
            boolean isConfirmed = confirm.isChecked();

            if (inputName.isEmpty() || inputMail.isEmpty() || inputPassword.isEmpty() || !isConfirmed) {
                // Show appropriate toast if any field is empty or confirm is unchecked
                if (inputName.isEmpty() || inputMail.isEmpty() || inputPassword.isEmpty()) {
                    showToast("Please fill in all fields");
                } else {
                    showToast("Please confirm before registering");
                }
            } else {
                //TODO Create a Login object with entered data
                //TODO Login loginInfo = new Login(inputName, inputMail, inputPassword);

                // Call API or perform desired action based on confirmation
                if (isConfirmed) {
                    performRegister(inputName, inputMail, inputPassword);
                    //TODO Call your API here with loginInfo
                    //TODO callAPI(loginInfo);
                } else {
                    showToast("Please confirm before registering");
                }
            }
        });

        // Set click listener for login button to navigate to LoginActivity
        login.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });


        backToInstructNavigationView = findViewById(R.id.back_to_instruct);

        backToInstructNavigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.back_to_instruct_item) {
                navigateToMainActivity();
                return true;
            }
            return false;
        });

    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Finish the LoginActivity to prevent returning back to it
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Finish the LoginActivity to prevent returning back to it
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void performRegister(String name, String email, String password) {
        User user = new User();
        user.setUsername(name);
        user.setEmail(email);
        user.setPassword(password);
        AtomicReference<Call<User>> call = new AtomicReference<>(apiService.signIn(user));
        call.get().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User resource = response.body();
                if (resource != null) {
                    showToast("Register successful, please login");
                    navigateToLoginActivity();
                } else {
                    showToast("Register failed: duplicated account");
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showToast("Register failed: duplicated account");
                call.cancel();
            }
        });
    }

}