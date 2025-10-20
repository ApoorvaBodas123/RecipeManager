package com.example.recipemanager.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipeRepository {
    private final RecipeDao dao;
    private final ExecutorService io;

    public RecipeRepository(Application app) {
        dao = AppDatabase.getInstance(app).recipeDao();
        io = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Recipe>> getAll() { return dao.getAll(); }
    public LiveData<List<Recipe>> getFavorites() { return dao.getFavorites(); }
    public LiveData<List<Recipe>> searchByName(String q) { return dao.searchByName(q); }
    public LiveData<List<Recipe>> filterByCategory(String c) { return dao.filterByCategory(c); }
    public LiveData<Recipe> getById(long id) { return dao.getById(id); }

    public void insert(Recipe r) { io.execute(() -> dao.insert(r)); }
    public void update(Recipe r) { io.execute(() -> dao.update(r)); }
    public void delete(Recipe r) { io.execute(() -> dao.delete(r)); }
}
