package com.org.cash.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import com.org.cash.model.Category;
import com.org.cash.model.CategoryWithChildren;

import java.util.Collection;
import java.util.List;

@Dao
public interface CategoryDao {
    @Transaction
    @Query("SELECT * FROM category")
    LiveData<List<CategoryWithChildren>> getCategoriesWithChildren();

    @Query("SELECT * FROM category WHERE id = :id AND active = :active LIMIT 1")
    Category findByIdActive(int id, boolean active);

    @Query("SELECT * FROM category WHERE id = :id OR name = :name LIMIT 1")
    Category findByIdOrName(int id, String name);

    @Query("SELECT * FROM category WHERE name = :name AND type = :type AND active = :active LIMIT 1")
    Category findByNameTypeActive(String name, int type, Boolean active);

    @Query("SELECT * FROM category WHERE id = :id AND type = :type AND active = :active LIMIT 1")
    Category findByIdTypeActive(int id, int type, Boolean active);

    @Query("SELECT * FROM category WHERE type = :type AND active = :active")
    List<Category> findByTypeInAndActive(int type, boolean active);

    @Query("SELECT * FROM category WHERE type = :type")
    List<Category> findAllByType(int type);

    @Query("SELECT * FROM category WHERE type = :type AND active = true")
    List<Category> getCategoriesIsInAndTypeAndActiveOrder(int type);

//    @Query("SELECT * FROM category WHERE id NOT IN (SELECT childId FROM categorychild) AND active = 1 AND type = :type")
//    List<Category> findParentCategories(int type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategory(Category category);

    @Insert
    void insertAll(Category... categorys);

    @Delete
    void delete(Category category);
}
