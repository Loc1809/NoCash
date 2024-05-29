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
    private Integer id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "type")
    private Integer type;

    @ColumnInfo(name = "icon")
    private Integer icon;

    public Category(Integer id, String name, Integer type, Integer icon) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.icon = icon;
    }

    @Ignore
    public Category(String name, Integer type, Integer icon) {
        this.name = name;
        this.type = type;
        this.icon = icon;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }
}
