package com.example.recipemanager.data.converters;

import androidx.room.TypeConverter;

import com.example.recipemanager.data.Recipe;

public class DifficultyConverter {
    @TypeConverter
    public static Recipe.Difficulty toDifficulty(String value) {
        return value == null ? null : Recipe.Difficulty.valueOf(value);
    }

    @TypeConverter
    public static String fromDifficulty(Recipe.Difficulty difficulty) {
        return difficulty == null ? null : difficulty.name();
    }
}
