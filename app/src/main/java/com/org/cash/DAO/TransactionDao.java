package com.org.cash.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.org.cash.model.Transaction;

import java.util.Collection;
import java.util.List;

@Dao
public interface TransactionDao {
    @Query("SELECT * FROM trans")
    LiveData<List<Transaction>> getAll();

    @Query("SELECT * FROM trans")
    List<Transaction> getTransactions();

    @Query("SELECT * FROM trans WHERE id IN (:transactionIds)")
    LiveData<List<Transaction>> loadAllByIds(Collection<Integer> transactionIds);

    @Query("SELECT * FROM trans WHERE time BETWEEN (:start) AND (:end)")
    List<Transaction> getTransactionsByMonth(long start, long end);

    @Query("SELECT * FROM trans WHERE id IN (:transactionIds)")
    Transaction findById(int transactionIds);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Transaction transaction);

    @Insert()
    void insertAll(Transaction... transactions);

    @Delete()
    void delete(Transaction transaction);

    default void deleteById(int transactionId){
        delete(findById(transactionId));
    };
}
