package com.example.recipemanager.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipes")
public class Recipe {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String name;

    @NonNull
    public String ingredients;

    @NonNull
    public String steps;

    // Persisted content URI string from ACTION_OPEN_DOCUMENT
    public String imageUri;

    // Optional: category (Dessert, Snack, Main-Course, Beverage)
    public String category;

    public boolean favorite;

    public Recipe(@NonNull String name, @NonNull String ingredients, @NonNull String steps, String imageUri, String category, boolean favorite) {
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.imageUri = imageUri;
        this.category = category;
        this.favorite = favorite;
    }
}
