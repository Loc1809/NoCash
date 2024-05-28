package com.org.cash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.LiveData;
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
import com.org.cash.API.TokenManager;
import com.org.cash.DAO.WalletDao;
import com.org.cash.R;
import com.org.cash.database.MoneyDb;
import com.org.cash.databinding.ActivityMainBinding;
import com.org.cash.model.Transaction;
import com.org.cash.model.User;
import com.org.cash.model.Wallet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;
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
                Random rand = new Random();
        Context context = this;
        MoneyDb db = MoneyDb.getDatabase(context);
        MoneyDb.databaseWriteExecutor.execute(() -> {
             List<Transaction> list  = db.transactionDao().getTransactions();
             try {
                 if (list.size() < 1) {
                     for (int i = 0; i < 10; i++) {
                         Transaction newTransaction = new Transaction(rand.nextInt(10) * 6000.0, 1714536703000L, "aabbcc", "cate"+rand.nextInt(20), "abc", rand.nextInt(1));
                         MoneyDb.databaseWriteExecutor.execute(() -> {
                             db.transactionDao().insert(newTransaction);
                         });
                     }
                 }
             }
             catch (Exception e){
                 for (int i = 0; i < 10; i++) {
                         Transaction newTransaction = new Transaction(rand.nextInt(10) * 6000.0, 1714536703000L, "aabbcc", "cate"+rand.nextInt(20), "abc", rand.nextInt(1));
                         MoneyDb.databaseWriteExecutor.execute(() -> {
                             db.transactionDao().insert(newTransaction);
                         });
                     }
             }
        });
        MoneyDb.databaseWriteExecutor.execute(() -> {
             List<Wallet> list  = db.walletDao().getWallets();
             try {
                 if (list.size() < 3) {
                     for (int i = 0; i < 10; i++) {
                         Wallet newWallet = new Wallet( "Wallet"+rand.nextInt(20), rand.nextInt(10)*10000.0);
                         MoneyDb.databaseWriteExecutor.execute(() -> {
                             db.walletDao().insert(newWallet);
                         });
                     }
                 }
             }
             catch (Exception e){
                 for (int i = 0; i < 10; i++) {
                         Wallet newWallet = new Wallet( "Wallet"+rand.nextInt(20), rand.nextInt(10)*10000.0);
                         MoneyDb.databaseWriteExecutor.execute(() -> {
                             db.walletDao().insert(newWallet);
                         });
                     }
             }
        });
        File dexOutputDir = getCodeCacheDir();
        dexOutputDir.setReadOnly();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_statistics, R.id.navigation_add_record,  R.id.navigation_budget,  R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        navView.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.navigation_home){
                navController.navigate(R.id.navigation_home);
            }
            else if(item.getItemId() == R.id.navigation_statistics){
                navController.navigate(R.id.navigation_statistics);
            }
            else if(item.getItemId() == R.id.navigation_add_record){
                navController.navigate(R.id.navigation_add_record);
            }
            else if(item.getItemId() == R.id.navigation_budget){
                navController.navigate(R.id.navigation_budget);
            }
            else if(item.getItemId() == R.id.navigation_home){
                navController.navigate(R.id.navigation_home);
            }
            else if(item.getItemId() == R.id.navigation_profile) {
                if (TokenManager.getInstance().getToken()!=null) {
                    navController.navigate(R.id.navigation_profile);
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                return false;
            } else {
                return NavigationUI.onNavDestinationSelected(item, navController)
                        || super.onOptionsItemSelected(item);
            }
            return false;
        });

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
        sharedPreferences.edit().putBoolean(PREF_STORAGE_PERMISSION_REQUESTED, true).apply();

//        if (!permissionRequested) {
//            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
//            startActivityForResult(intent, REQUEST_CODE_STORAGE_PERMISSION);
//        }
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && resultCode == RESULT_OK) {
            if (data != null) {
                Uri treeUri = data.getData();

                final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(treeUri, takeFlags);

            }
        }
    }

    private void createFolderInDirectory(Uri treeUri) {
        DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
        if (pickedDir != null && pickedDir.canWrite()) {
            pickedDir.findFile("NewFolder");
            DocumentFile newFolder = pickedDir.createDirectory("NewFolder");
            if (newFolder != null) {
                Toast.makeText(this, "Folder created successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to create folder", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Cannot write to the selected directory", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel any ongoing tasks or cleanup resources
    }

}