package com.org.cash.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.org.cash.model.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM category WHERE id = :id LIMIT 1")
    Category findById(int id);

    @Query("SELECT * FROM category WHERE id = :id OR name = :name LIMIT 1")
    Category findByIdOrName(int id, String name);

    @Query("SELECT * FROM category WHERE name = :name AND type = :type LIMIT 1")
    Category findByNameType(String name, int type);

    @Query("SELECT * FROM category WHERE id = :id AND type = :type LIMIT 1")
    Category findByIdType(int id, int type);

    @Query("SELECT * FROM category WHERE type = :type")
    List<Category> findByTypeIn(int type);

    @Query("SELECT * FROM category WHERE type = :type")
    List<Category> findAllByType(int type);

    @Query("SELECT * FROM category WHERE type = :type")
    List<Category> getCategoriesIsInAndType(int type);

//    @Query("SELECT * FROM category WHERE id NOT IN (SELECT childId FROM categorychild) AND active = 1 AND type = :type")
//    List<Category> findParentCategories(int type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Category category);

    @Insert
    void insertAll(Category... categorys);

    @Delete
    void delete(Category category);

    default void deleteById(int id){
        delete(findById(id));
    };
}
