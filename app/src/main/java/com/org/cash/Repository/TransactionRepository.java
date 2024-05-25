package com.org.cash.Repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import com.org.cash.DAO.TransactionDao;
import com.org.cash.database.DatabaseHelper;
import com.org.cash.database.MoneyDb;
import com.org.cash.model.Transaction;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionRepository {
        private TransactionDao transactionDao;
    private ExecutorService executorService;
    private MoneyDb db;


    public TransactionRepository(Context context) {
        db = DatabaseHelper.getInstance(context).getDatabase();
        transactionDao = db.transactionDao();
        executorService = Executors.newSingleThreadExecutor();
    }


    public TransactionRepository(TransactionDao transactionDao){
        this.transactionDao = transactionDao;
    }

    public LiveData<Transaction> findTransactionById(int transactionId) {
        final MutableLiveData<Transaction> transactionData =
            new MutableLiveData<>();
            transactionData.setValue(
                 transactionDao.findById(transactionId));
        return transactionData;
    }


    public LiveData<List<Transaction>> getTransactions() {
        final MutableLiveData<List<Transaction>> transactionData =
            new MutableLiveData<>();
            transactionData.setValue(transactionDao.getTransactions());
        return transactionData;
    }

    public LiveData<Long> insertTransaction(Transaction transaction) {
        executorService = Executors.newSingleThreadExecutor();
        MutableLiveData<Long> insertionResult = new MutableLiveData<>();

        executorService.execute(() -> {
            long insertedRowId = transactionDao.insert(transaction);

        });

        return insertionResult;
    }
}
