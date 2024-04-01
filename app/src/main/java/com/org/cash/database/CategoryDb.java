package com.org.cash.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.org.cash.model.Category;
import com.org.cash.DAO.CategoryDao;
@Database(entities = {Category.class}, version = 1)
public abstract class CategoryDb extends RoomDatabase {
    public abstract CategoryDao categoryDao();
}