package com.org.cash.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


import java.util.List;

@Entity(tableName = "category")
public class Category {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id = -1;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "type")
    private int type;

    @ColumnInfo(name = "icon")
    private int icon;

    public Category(String name, int type, int icon) {
        this.name = name;
        this.type = type;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
