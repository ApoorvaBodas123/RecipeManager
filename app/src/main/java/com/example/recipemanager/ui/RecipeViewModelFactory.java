package com.example.recipemanager.ui;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.recipemanager.data.RecipeRepository;

public class RecipeViewModelFactory implements ViewModelProvider.Factory {
    private final RecipeRepository repository;

    public RecipeViewModelFactory(RecipeRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RecipeViewModel.class)) {
            return (T) new RecipeViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
