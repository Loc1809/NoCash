package com.org.cash.Repository;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import com.org.cash.DAO.WalletDao;
import com.org.cash.database.DatabaseHelper;
import com.org.cash.database.MoneyDb;
import com.org.cash.model.Wallet;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WalletRepository {
    private WalletDao walletDao;
    private ExecutorService executorService;
    private MoneyDb db;


    public WalletRepository(Context context) {
        db = DatabaseHelper.getInstance(context).getDatabase();
        walletDao = db.walletDao();
        executorService = Executors.newSingleThreadExecutor();
    }


    public WalletRepository(WalletDao walletDao){
        this.walletDao = walletDao;
    }

//    public WalletRepository(Context context){
//        MoneyDb db = MoneyDb.getDatabase(context);
//        walletDao = db.walletDao();
//        executorService = Executors.newSingleThreadExecutor();
//    }

    public LiveData<Wallet> findWalletById(int walletId) {
        final MutableLiveData<Wallet> walletData =
            new MutableLiveData<>();
            walletData.setValue(
                 walletDao.findById(walletId));
        return walletData;
    }


    public LiveData<List<Wallet>> getWallets() {
        final MutableLiveData<List<Wallet>> walletData =
            new MutableLiveData<>();
            walletData.setValue(walletDao.getWallets());
        return walletData;
    }

    public LiveData<Long> insertWallet(Wallet wallet) {
        executorService = Executors.newSingleThreadExecutor();
        MutableLiveData<Long> insertionResult = new MutableLiveData<>();

        executorService.execute(() -> {
            long insertedRowId = walletDao.insert(wallet);

        });

        return insertionResult;
    }

//    public void insertWallet(Wallet wallet) {
//        executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(() -> walletDao.insertOne(wallet));
//    }

    public LiveData<List<Wallet>> getAllWallets() {
        Wallet newWallet = new Wallet( 6,"Test", 10000.0);
        insertWallet(newWallet);

        // Observe the insertion operation completion using LiveData
        MutableLiveData<Boolean> insertionComplete = new MutableLiveData<>();
        insertionComplete.postValue(false);

        // Observe the insertion completion
        insertWallet(newWallet).observeForever(new Observer<Long>() {
            @Override
            public void onChanged(Long result) {
                // If the insertion operation is completed
                if (result != null && result > 0) {
                    // Notify that insertion is complete
                    insertionComplete.postValue(true);
                }
            }
        });

        // Return LiveData representing all wallets, but wait until insertion is complete
        return Transformations.switchMap(insertionComplete, inserted -> walletDao.getAll());
    }


//    public LiveData<List<Wallet>> getAllWallets() {
//        Wallet newWallet = new Wallet(1, "Test", 10000.0);
//        insertWallet(newWallet);
//        return walletDao.getAll();
//    }

//    public void insertWallet(Wallet wallet) {
//        // Perform database operation asynchronously
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                walletDao.insertOne(wallet);
//                return null;
//            }
//        }.execute();
//    }
}
