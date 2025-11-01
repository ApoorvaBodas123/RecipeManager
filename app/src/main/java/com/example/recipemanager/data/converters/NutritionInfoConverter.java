package com.example.recipemanager.data.converters;

import androidx.room.TypeConverter;

import com.example.recipemanager.data.Recipe;
import com.google.gson.Gson;

public class NutritionInfoConverter {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static Recipe.NutritionInfo fromString(String value) {
        if (value == null) {
            return new Recipe.NutritionInfo();
        }
        return gson.fromJson(value, Recipe.NutritionInfo.class);
    }

    @TypeConverter
    public static String toString(Recipe.NutritionInfo info) {
        return gson.toJson(info);
    }
}
