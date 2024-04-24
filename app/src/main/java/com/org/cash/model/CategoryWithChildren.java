package com.org.cash.model;

import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.List;

public class CategoryWithChildren {
    @Embedded
    public Category category;

    @Relation(
        parentColumn = "id",
        entityColumn = "parentId"
    )
    public List<Category> children;

    public CategoryWithChildren(Category category, List<Category> children) {
        this.category = category;
        this.children = children;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }
}