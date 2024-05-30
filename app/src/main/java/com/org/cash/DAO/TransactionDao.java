package com.org.cash.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.org.cash.model.SumByCategory;
import com.org.cash.model.Transaction;
import java.util.List;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Dao
public interface TransactionDao {
    @Query("SELECT * FROM trans")
    LiveData<List<Transaction>> getAll();

    @Query("SELECT * FROM trans")
    List<Transaction> getTransactions();

    @Query("SELECT * FROM trans WHERE id IN (:transactionIds)")
    LiveData<List<Transaction>> loadAllByIds(Collection<Integer> transactionIds);

    @Query("SELECT * FROM trans WHERE time BETWEEN (:start) AND (:end) ORDER BY time")
    List<Transaction> getTransactionsByMonth(long start, long end);

    @Query("SELECT * FROM trans WHERE category = :category AND time BETWEEN (:start) AND (:end) ORDER BY time")
    List<Transaction> getTransactionsByMonthCategory(long start, long end, String category);

    @Query("SELECT * FROM trans WHERE category = :cate AND time BETWEEN (:start) AND (:end)")
    List<Transaction> getTransactionsByMonthAndCate(long start, long end, String cate);

    @Query("SELECT SUM(amount) FROM trans WHERE category = :cate AND time BETWEEN (:start) AND (:end)")
    Double getSumByMonthAndCate(long start, long end, String cate);

    @Query("SELECT SUM(amount) FROM trans WHERE direction = :direction AND time BETWEEN (:start) AND (:end)")
    Double getSumByMonth(long start, long end, int direction);

    @Query("SELECT SUM(amount) as sum, category FROM trans WHERE direction = :direction AND time BETWEEN (:start) AND (:end) GROUP BY category")
    List<SumByCategory> getSumByMonthDirection(long start, long end, int direction);

    @Query("SELECT * FROM trans WHERE id IN (:transactionIds)")
    Transaction findById(int transactionIds);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Transaction transaction);

    @Insert()
    void insertAll(Transaction... transactions);

    @Delete()
    void delete(Transaction transaction);

    @Query("SELECT DISTINCT time FROM trans ORDER BY time")
    List<Long> getUniqueTransactionTimes();

    @Query("SELECT * FROM trans WHERE direction = :direction AND time BETWEEN (:startTime) AND (:endTime)")
    List<Transaction> getTransactionsByTime(long startTime, long endTime, int direction);

    @Query("SELECT * FROM trans WHERE time = :transactionTime AND category = :category")
    List<Transaction> getTransactionsByTimeAndCategory(long transactionTime, String category);

    @Query("SELECT DISTINCT strftime('%d', datetime(time / 1000, 'unixepoch', 'localtime')) AS day " +
           "FROM trans " +
           "WHERE strftime('%Y', datetime(time / 1000, 'unixepoch', 'localtime')) = :year " +
           "AND strftime('%m', datetime(time / 1000, 'unixepoch', 'localtime')) = :month")
    List<Integer> getDaysWithTransactions(int month, int year);

    @Query("SELECT strftime('%d', datetime(time / 1000, 'unixepoch', 'localtime')) AS day " +
            "FROM trans WHERE direction = :direction AND time BETWEEN (:start) AND (:end) GROUP BY day HAVING day != '00'  ORDER BY time ")
    List<Integer> getDaysByMonth(long start, long end, int direction);

    @Query("SELECT time FROM trans WHERE time BETWEEN (:start) AND (:end) ORDER BY time ")
    List<Integer> getDaysByMonths(long start, long end);

    default void deleteById(int transactionId){
        delete(findById(transactionId));
    };

}
