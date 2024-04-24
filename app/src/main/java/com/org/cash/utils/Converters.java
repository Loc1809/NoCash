package com.org.cash.utils;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.org.cash.model.Category;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static List<Category> fromJson(String value) {
        Type listType = new TypeToken<List<Category>>() {}.getType();
        return gson.fromJson(value, listType);
    }

    @TypeConverter
    public static String toJson(List<Category> list) {
        return gson.toJson(list);
    }
}
