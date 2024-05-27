package com.org.cash.DAO;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;
import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.org.cash.CustomToast;
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

    @Query("SELECT * FROM `limit` WHERE direction = :type AND category = :cate AND startDate BETWEEN (:start) AND (:end)")
    List<Limit> getLimitsByMonthTypeAndCate(long start, long end, int type, String cate);

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

    default void checkBeforeInsert(Limit limit, Context context, Handler handler){
        List<Limit> list = getLimitsByMonthTypeAndCate(Long.valueOf(limit.getStartDate()),
                Long.valueOf(limit.getEndDate()), limit.getType(), limit.getCategory());
        if (list.isEmpty() || limit.getId() != -1){
            insert(limit);
        } else {
            handler.post(() -> {
                CustomToast.makeText(context, "Category existed", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
