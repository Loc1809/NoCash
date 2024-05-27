package com.org.cash.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.org.cash.model.Limit;

import java.util.Collection;
import java.util.List;

@Dao
public interface LimitDao {
    @Query("SELECT * FROM `limit`")
    LiveData<List<Limit>> getAll();

    @Query("SELECT * FROM `limit`")
    List<Limit> getLimits();

    @Query("SELECT * FROM `limit` WHERE id IN (:limitIds)")
    LiveData<List<Limit>> loadAllByIds(Collection<Integer> limitIds);

    @Query("SELECT * FROM `limit` WHERE startDate BETWEEN (:start) AND (:end)")
    List<Limit> getLimitsByMonth(long start, long end);

    @Query("SELECT * FROM `limit` WHERE id IN (:limitIds)")
    Limit findById(int limitIds);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Limit limit);

    @Insert()
    void insertAll(Limit... limits);

    @Delete()
    void delete(Limit limit);

    default void deleteById(int limitId){
        delete(findById(limitId));
    };
}
