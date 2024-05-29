package com.org.cash.DAO;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.org.cash.CustomToast;
import com.org.cash.model.Category;
import com.org.cash.model.Limit;

import java.util.Collection;
import java.util.List;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM category")
    List<Category> findAll();

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

    @Query("SELECT * FROM category WHERE type = :type AND name = :name")
    List<Category> findAllByTypeAndName(int type, String name);

    @Query("SELECT * FROM category WHERE name IN (:name)")
    List<Category> findAllByNames(Collection<String> name);

    @Query("SELECT * FROM category WHERE name = (:name)")
    Category findByName(String name);

    @Query("SELECT * FROM category WHERE type = :type")
    List<Category> getCategoriesIsInAndType(int type);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Category category);

    @Insert
    void insertAll(Category... categorys);

    @Delete
    void delete(Category category);

    default void deleteById(int id){
        delete(findById(id));
    };

    default void checkBeforeInsert(Category category, Context context, Handler handler){
        List<Category> list = findAllByTypeAndName(category.getType(), category.getName());
        if (list.isEmpty() || category.getId() != -1){
            insert(category);
        } else {
            handler.post(() -> {
                CustomToast.makeText(context, "Category existed", Toast.LENGTH_SHORT).show();
            });
        }
    }

    default boolean findSimilarCategory(Category category){
        if (category.getId() == null)
            return false;
        List<Category> list = findAllByTypeAndName(category.getType(), category.getName());
        return !list.isEmpty();
    }
}
