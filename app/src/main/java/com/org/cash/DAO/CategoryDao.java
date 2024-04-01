package com.org.cash.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.org.cash.model.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM category")
    List<Category> getAll();

    @Query("SELECT * FROM category WHERE id IN (:categoryIds)")
    List<Category> loadAllByIds(int[] categoryIds);

//    @Query("SELECT * FROM category WHERE first_name LIKE :first AND " +
//           "last_name LIKE :last LIMIT 1")
//    Category findByName(String first, String last);

    @Insert
    void insertAll(Category... categorys);

    @Delete
    void delete(Category category);
}
