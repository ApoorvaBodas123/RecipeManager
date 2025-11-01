package com.example.recipemanager.ui;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.recipemanager.data.UserRepository;

public class UserViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;

    public UserViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            UserRepository repository = UserRepository.getInstance(application);
            return (T) new UserViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
