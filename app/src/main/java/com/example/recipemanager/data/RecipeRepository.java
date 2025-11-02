package com.example.recipemanager.data;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipeRepository {
    private final RecipeDao dao;
    private final ExecutorService io;
    private static volatile RecipeRepository instance;

    private RecipeRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        dao = database.recipeDao();
        io = Executors.newSingleThreadExecutor();
    }

    public static synchronized RecipeRepository getInstance(Application application) {
        if (instance == null) {
            instance = new RecipeRepository(application);
        }
        return instance;
    }

    public LiveData<List<Recipe>> getAll() { return dao.getAll(); }
    public LiveData<List<Recipe>> getFavorites() { return dao.getFavorites(); }
    public LiveData<List<Recipe>> searchByName(String q) { return dao.searchByName(q); }
    public LiveData<List<Recipe>> filterByCategory(String c) { return dao.filterByCategory(c); }
    public LiveData<Recipe> getById(long id) { return dao.getById(id); }

    public LiveData<Long> insert(Recipe r) {
        MutableLiveData<Long> result = new MutableLiveData<>();
        io.execute(() -> {
            try {
                long id = dao.insert(r);
                result.postValue(id);
            } catch (Exception e) {
                result.postValue(-1L);
            }
        });
        return result;
    }
    public void update(Recipe r) { io.execute(() -> dao.update(r)); }
    public void delete(Recipe r) { io.execute(() -> dao.delete(r)); }
}
