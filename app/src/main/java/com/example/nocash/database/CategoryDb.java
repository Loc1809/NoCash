package com.example.nocash.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.nocash.model.Category;
import com.example.nocash.DAO.CategoryDao;
@Database(entities = {Category.class}, version = 1)
public abstract class CategoryDb extends RoomDatabase {
    public abstract CategoryDao categoryDao();
}