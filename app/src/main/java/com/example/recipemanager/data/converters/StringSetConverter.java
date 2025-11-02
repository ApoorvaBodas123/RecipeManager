package com.example.recipemanager.data.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class StringSetConverter {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static Set<String> fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return new HashSet<>();
        }
        try {
            Type setType = TypeToken.getParameterized(HashSet.class, String.class).getType();
            return gson.fromJson(value, setType);
        } catch (Exception e) {
            // In case of any parsing error, return an empty set
            return new HashSet<>();
        }
    }

    @TypeConverter
    public static String fromSet(Set<String> set) {
        return gson.toJson(set);
    }
}
