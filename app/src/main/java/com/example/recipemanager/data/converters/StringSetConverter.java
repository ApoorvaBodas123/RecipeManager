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
        if (value == null) {
            return new HashSet<>();
        }
        Type setType = new TypeToken<HashSet<String>>() {}.getType();
        return gson.fromJson(value, setType);
    }

    @TypeConverter
    public static String fromSet(Set<String> set) {
        return gson.toJson(set);
    }
}
