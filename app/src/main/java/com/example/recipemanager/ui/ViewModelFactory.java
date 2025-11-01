package com.example.recipemanager.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.recipemanager.data.RecipeRepository;
import com.example.recipemanager.data.UserRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public ViewModelFactory(Application application) {
        this.application = application;
        this.recipeRepository = RecipeRepository.getInstance(application);
        this.userRepository = UserRepository.getInstance(application);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RecipeViewModel.class)) {
            return (T) new RecipeViewModel(recipeRepository);
        } else if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(userRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
