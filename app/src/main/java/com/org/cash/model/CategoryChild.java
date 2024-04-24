package com.org.cash.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "category_child",
        primaryKeys = {"parentId", "childId"},
        foreignKeys = {
            @ForeignKey(entity = Category.class,
                        parentColumns = "id",
                        childColumns = "parentId",
                        onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = Category.class,
                        parentColumns = "id",
                        childColumns = "childId",
                        onDelete = ForeignKey.CASCADE)},
        indices = {@Index("childId")})
public class CategoryChild {
    public int parentId;
    public int childId;

    public CategoryChild(int parentId, int childId) {
        this.parentId = parentId;
        this.childId = childId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }
}

