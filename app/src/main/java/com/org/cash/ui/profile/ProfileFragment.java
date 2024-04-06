package com.org.cash.ui.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.widget.LinearLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import com.org.cash.R;

import com.google.android.material.navigation.NavigationView;


public class ProfileFragment extends Fragment {


    public ProfileFragment() {
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


    public void hidenAll(View view) {
        view.findViewById(R.id.profile_main_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.profile_account_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.profile_setting_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.header_back_to_main).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.header_back_to_setting).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.profile_setting_password_fragment).setVisibility(View.INVISIBLE);
    }

    public void hidenMainNav(View view) {
        view.findViewById(R.id.profile_main_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.profile_account_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.profile_setting_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.header_back_to_setting).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.profile_setting_password_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.header_back_to_main).setVisibility(View.VISIBLE);
    }

    public void hidenSettingNav(View view) {
        view.findViewById(R.id.profile_main_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.profile_account_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.profile_setting_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.header_back_to_setting).setVisibility(View.VISIBLE);
        view.findViewById(R.id.profile_setting_password_fragment).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.header_back_to_main).setVisibility(View.INVISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

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
            } else if(itemId == R.id.profile_setting_notification){
//                hidenMainNav(view);
//                view.findViewById(R.id.profile_account_fragment).setVisibility(View.VISIBLE);
            }
            return false;
        });

        mainNavigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.profile_navigation_my_account) {
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
}