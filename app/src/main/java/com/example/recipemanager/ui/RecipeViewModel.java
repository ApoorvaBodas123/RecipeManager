package com.example.recipemanager.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.recipemanager.data.Recipe;
import com.example.recipemanager.data.RecipeRepository;

import java.util.List;

public class RecipeViewModel extends AndroidViewModel {
    private final RecipeRepository repo;

    private final MutableLiveData<String> query = new MutableLiveData<>("");
    private final MutableLiveData<String> category = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> favoritesOnly = new MutableLiveData<>(false);

    private final MediatorLiveData<List<Recipe>> recipes = new MediatorLiveData<>();

    public RecipeViewModel(@NonNull Application application) {
        super(application);
        repo = new RecipeRepository(application);
        // Default source
        recipes.addSource(repo.getAll(), recipes::setValue);
    }

    public LiveData<List<Recipe>> getRecipes() { return recipes; }

    public void refreshSources() {
        recipes.removeSource(repo.getAll());
        // Combine filters simply by favoring precedence: favorites > category > search > all
        if (Boolean.TRUE.equals(favoritesOnly.getValue())) {
            recipes.addSource(repo.getFavorites(), recipes::setValue);
        } else if (category.getValue() != null && !category.getValue().isEmpty()) {
            recipes.addSource(repo.filterByCategory(category.getValue()), recipes::setValue);
        } else if (query.getValue() != null && !query.getValue().isEmpty()) {
            recipes.addSource(repo.searchByName(query.getValue()), recipes::setValue);
        } else {
            recipes.addSource(repo.getAll(), recipes::setValue);
        }
    }

    public void setQuery(String q) { query.setValue(q); refreshSources(); }
    public void setCategory(String c) { category.setValue(c); refreshSources(); }
    public void setFavoritesOnly(boolean fav) { favoritesOnly.setValue(fav); refreshSources(); }

    public LiveData<Recipe> getById(long id) { return repo.getById(id); }

    public void save(Recipe r) { if (r.id == 0) repo.insert(r); else repo.update(r); }
    public void delete(Recipe r) { repo.delete(r); }
}
