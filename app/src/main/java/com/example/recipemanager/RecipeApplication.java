package com.example.recipemanager;

import android.app.Application;

public class RecipeApplication extends Application {
    private static RecipeApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static RecipeApplication getInstance() {
        return instance;
    }
}
