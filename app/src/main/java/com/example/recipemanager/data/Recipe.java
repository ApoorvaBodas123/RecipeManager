package com.example.recipemanager.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.HashSet;
import java.util.Set;

@Entity(tableName = "recipes")
public class Recipe {
    public enum Difficulty {
        EASY, MEDIUM, HARD, EXPERT
    }

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

    // New fields
    public int prepTime; // in minutes
    public int cookTime; // in minutes
    public Difficulty difficulty;
    public Set<String> dietaryRestrictions; // e.g., VEGETARIAN, VEGAN, GLUTEN_FREE, DAIRY_FREE, NUT_FREE
    public NutritionInfo nutritionInfo;

    public static class NutritionInfo {
        public int calories;
        public int protein; // in grams
        public int carbs;   // in grams
        public int fat;     // in grams
        public int fiber;   // in grams
        public int sugar;   // in grams
        public int sodium;  // in mg

        public NutritionInfo() {
            // Default constructor required for Room
        }

        public NutritionInfo(int calories, int protein, int carbs, int fat, int fiber, int sugar, int sodium) {
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.fat = fat;
            this.fiber = fiber;
            this.sugar = sugar;
            this.sodium = sodium;
        }
    }

    public Recipe() {
        // Default constructor required for Room
        this.name = "";
        this.ingredients = "";
        this.steps = "";
        this.dietaryRestrictions = new HashSet<>();
        this.nutritionInfo = new NutritionInfo();
    }

    @Ignore
    public Recipe(@NonNull String name, @NonNull String ingredients, @NonNull String steps, 
                 String imageUri, String category, boolean favorite, int prepTime, int cookTime, 
                 Difficulty difficulty, Set<String> dietaryRestrictions, NutritionInfo nutritionInfo) {
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.imageUri = imageUri;
        this.category = category;
        this.favorite = favorite;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.difficulty = difficulty;
        this.dietaryRestrictions = dietaryRestrictions != null ? dietaryRestrictions : new HashSet<>();
        this.nutritionInfo = nutritionInfo != null ? nutritionInfo : new NutritionInfo();
    }
}
