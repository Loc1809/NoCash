package com.org.cash.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
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

    @Query("SELECT * FROM trans WHERE id IN (:transactionIds)")
    Transaction findById(int transactionIds);

//    @Query("SELECT * FROM transaction WHERE first_name LIKE :first AND " +
//           "last_name LIKE :last LIMIT 1")
//    Transaction findByName(String first, String last);

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert()
    long insert(Transaction transaction);

    @Insert
    void insertAll(Transaction... transactions);

    @Delete
    void delete(Transaction transaction);
}
