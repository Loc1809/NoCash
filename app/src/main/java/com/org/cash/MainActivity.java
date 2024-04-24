package com.org.cash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.org.cash.DAO.WalletDao;
import com.org.cash.R;
import com.org.cash.database.MoneyDb;
import com.org.cash.databinding.ActivityMainBinding;
import com.org.cash.model.User;
import com.org.cash.model.Wallet;

import java.io.File;
import java.util.List;

import com.org.cash.ui.home.HomeViewModel;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.GlobalScope;
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private static final String PREF_STORAGE_PERMISSION_REQUESTED = "storage_permission_requested";
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File dexOutputDir = getCodeCacheDir();
        dexOutputDir.setReadOnly();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_statistics, R.id.navigation_add_record, R.id.navigation_budget, R.id.navigation_profile, R.id.profile_navigation_contact,R.id.profile_navigation_setting,R.id.profile_navigation_my_account,R.id.profile_navigation_help_center)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        requestStoragePermission();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


    private void requestStoragePermission() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean permissionRequested = sharedPreferences.getBoolean(PREF_STORAGE_PERMISSION_REQUESTED, false);

        if (!permissionRequested) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, REQUEST_CODE_STORAGE_PERMISSION);

            // Update the flag to indicate that permission has been requested
            sharedPreferences.edit().putBoolean(PREF_STORAGE_PERMISSION_REQUESTED, true).apply();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && resultCode == Activity.RESULT_OK && data != null) {
            Uri treeUri = data.getData();
        }
    }
}