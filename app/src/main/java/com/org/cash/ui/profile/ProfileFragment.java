package com.org.cash.ui.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.org.cash.API.ApiService;
import com.org.cash.API.RetroFitConnection;
import com.org.cash.API.TokenManager;
import com.org.cash.CustomToast;
import com.org.cash.LoginActivity;
import com.org.cash.MainActivity;
import com.org.cash.R;

import com.google.android.material.navigation.NavigationView;
import com.org.cash.model.ChangePwd;
import com.org.cash.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.atomic.AtomicReference;


public class ProfileFragment extends Fragment {

    ApiService apiService = RetroFitConnection.getInstance().getRetrofit().create(ApiService.class);

    User user;

    private TextView name;
    private TextView mail;

    private EditText editText_Name;
    private EditText editText_Email;
    private EditText editText_Phone;
    private Button save;

    private EditText editText_old_pwd;
    private EditText editText_reType_pwd;
    private EditText editText_new_pwd;
    private Button save_pwd;

    private Button logout;


//    private EditText editText_Name;
//    private EditText editText_Email;
//    private EditText editText_Phone;
//
//    private Button save;

    public ProfileFragment() {
    }

    public void onButtonClick_save_account(View view) {

    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @SuppressLint("WrongConstant")
    public void hidenAll(View view) {
        view.findViewById(R.id.profile_main_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.profile_account_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.profile_setting_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.header_back_to_main).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.header_back_to_setting).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.profile_setting_password_fragment).setVisibility(View.INVISIBLE);
    }

    @SuppressLint("WrongConstant")
    public void hidenMainNav(View view) {
        view.findViewById(R.id.profile_main_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.profile_account_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.profile_setting_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.header_back_to_setting).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.profile_setting_password_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.header_back_to_main).setVisibility(View.VISIBLE);
    }

    @SuppressLint("WrongConstant")
    public void hidenSettingNav(View view) {
        view.findViewById(R.id.profile_main_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.profile_account_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.profile_setting_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.header_back_to_setting).setVisibility(View.VISIBLE);
        view.findViewById(R.id.profile_setting_password_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.header_back_to_main).setVisibility(View.INVISIBLE);
    }

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        this.name = view.findViewById(R.id.textView2);
        this.mail = view.findViewById(R.id.textView3);
        this.editText_Name = view.findViewById(R.id.editText_Name);
        this.editText_Email = view.findViewById(R.id.editText_Email);
        this.editText_Phone = view.findViewById(R.id.editText_Phone);
        this.save = view.findViewById(R.id.button2);
        this.logout = view.findViewById(R.id.button3);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TokenManager.getInstance().setToken(null);
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                user.setName(String.valueOf(editText_Name.getText()));
                user.setEmail(String.valueOf(editText_Email.getText()));
                user.setPhoneNumber(editText_Phone.getText().toString());
                //Log.e("ProfileFragment", "Error: " + editText_Phone.getText());
                AtomicReference<Call<User>> call = new AtomicReference<>(apiService.updateData(user, TokenManager.getInstance().getToken()));
                call.get().enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        //  User resource = response.body();
                        try {
                            if (response.code() == 200)
                                showToast("Update successful");
                            else {
                                showToast("UPDATE NOT SUCCESSFUL");
                            }
                        } catch (Exception e) {
                            showToast("UPDATE NOT SUCCESSFUL");
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        //throw new RuntimeException(t);
                        showToast("UPDATE NOT SUCCESSFUL");
                        call.cancel();
                    }
                });
            }
        });


        this.editText_new_pwd = view.findViewById(R.id.editTextTextPassword_New);
        this.editText_old_pwd = view.findViewById(R.id.editTextTextPassword_Old);
        this.editText_reType_pwd = view.findViewById(R.id.editTextTextPassword_ReType);
        this.save_pwd = view.findViewById(R.id.button4);

        save_pwd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ChangePwd pwd = new ChangePwd();
                pwd.newPwd = String.valueOf(editText_new_pwd.getText());
                pwd.oldPwd = String.valueOf(editText_old_pwd.getText());
                pwd.reTypePwd = String.valueOf(editText_reType_pwd.getText());

                AtomicReference<Call<User>> call = new AtomicReference<>(apiService.updatePwdData(pwd, TokenManager.getInstance().getToken()));
                call.get().enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        //  User resource = response.body();
                        Log.e("ProfileFragment", "Error: " + response.body());
                        try {
                            if (response.code() == 200) {

                                showToast("Update successful");
                                editText_reType_pwd.setText("");
                                editText_new_pwd.setText("");
                                editText_old_pwd.setText("");
                                hidenAll(view);
                                view.findViewById(R.id.profile_main_fragment).setVisibility(View.VISIBLE);
                            } else {
                                showToast("UPDATE NOT SUCCESSFUL");
                            }
                        } catch (Exception e) {
                            showToast("UPDATE NOT SUCCESSFUL");
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        //throw new RuntimeException(t);
                        showToast("UPDATE NOT SUCCESSFUL");
                        call.cancel();
                    }
                });
            }
        });

        AtomicReference<Call<User>> call = new AtomicReference<>(apiService.fetchData(TokenManager.getInstance().getToken()));
        call.get().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User resource = response.body();
                try {
                    user = resource;
                    name.setText(resource.getUsername());
                    mail.setText(resource.getEmail());
                    editText_Name.setText(resource.getUsername());
                    editText_Email.setText(resource.getEmail());
                    editText_Phone.setText(resource.getPhoneNumber());
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                call.cancel();
            }
        });

        NavigationView mainNavigationView = view.findViewById(R.id.profile_nav_view);
        NavigationView setting01NavigationView = view.findViewById(R.id.profile_setting_01_nav_view);
        NavigationView setting02NavigationView = view.findViewById(R.id.profile_setting_02_nav_view);
        NavigationView backToMainNavigationView = view.findViewById(R.id.profile_back_to_main);
        NavigationView backToSettingNavigationView = view.findViewById(R.id.profile_back_to_setting);

        backToSettingNavigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.profile_back_to_setting) {
                hidenMainNav(view);
                view.findViewById(R.id.profile_setting_fragment).setVisibility(View.VISIBLE);
            }
            return false;
        });

        backToMainNavigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.profile_back_to_main) {
                hidenAll(view);
                view.findViewById(R.id.profile_main_fragment).setVisibility(View.VISIBLE);
            }
            return false;
        });

        setting02NavigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.profile_privacy_policy) {
//                hidenMainNav(view);
//                view.findViewById(R.id.profile_account_fragment).setVisibility(View.VISIBLE);
            }
            return false;
        });

        setting01NavigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.profile_setting_password) {
                hidenSettingNav(view);
                view.findViewById(R.id.profile_setting_password_fragment).setVisibility(View.VISIBLE);
            } else if (itemId == R.id.profile_setting_notification) {
//                hidenMainNav(view);
//                view.findViewById(R.id.profile_account_fragment).setVisibility(View.VISIBLE);
            }
            return false;
        });

        mainNavigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.profile_navigation_my_account) {

                call.set(apiService.fetchData(TokenManager.getInstance().getToken()));
                call.get().enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User resource = response.body();
                        user = resource;
                        editText_Name.setText(resource.getName());
                        editText_Email.setText(resource.getEmail());
                        editText_Phone.setText(resource.getPhoneNumber());
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        call.cancel();
                    }
                });
                hidenMainNav(view);
                view.findViewById(R.id.profile_account_fragment).setVisibility(View.VISIBLE);

            } else if (itemId == R.id.profile_navigation_setting) {
                hidenMainNav(view);
                view.findViewById(R.id.profile_setting_fragment).setVisibility(View.VISIBLE);
            } else if (itemId == R.id.profile_navigation_help_center) {
//                navController.navigate(R.id.fragment_profile_main);
//                return true;
            } else if (itemId == R.id.profile_navigation_contact) {
//                navController.navigate(R.id.fragment_profile_main);
//                return true;
            } else {
                // navController.navigate(R.id.fragment_profile_main);
                return true;
            }

            return false;
        });

        return view;
    }


    private void showToast(String message) {
        CustomToast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}