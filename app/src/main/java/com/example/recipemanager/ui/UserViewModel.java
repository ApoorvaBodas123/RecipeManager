package com.example.recipemanager.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.recipemanager.data.User;
import com.example.recipemanager.data.UserRepository;

public class UserViewModel extends ViewModel {
    private final UserRepository repository;
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public UserViewModel(UserRepository repository) {
        this.repository = repository;
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void login(String username, String password) {
        isLoading.setValue(true);
        repository.login(username, password, new UserRepository.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser.postValue(user);
                error.postValue(null);
                isLoading.postValue(false);
            }

            @Override
            public void onError(String errorMessage) {
                error.postValue(errorMessage);
                currentUser.postValue(null);
                isLoading.postValue(false);
            }
        });
    }

    public void register(String username, String email, String password, String fullName) {
        isLoading.postValue(true);
        
        // First check if username exists
        repository.checkUsernameExists(username, usernameExists -> {
            if (usernameExists) {
                error.postValue("Username already exists");
                isLoading.postValue(false);
                return;
            }

            // Then check if email exists
            repository.checkEmailExists(email, emailExists -> {
                if (emailExists) {
                    error.postValue("Email already in use");
                    isLoading.postValue(false);
                    return;
                }

                // If both checks pass, create new user
                User newUser = new User(username, email, password, fullName);
                repository.insert(newUser);
                
                // Auto-login after registration
                login(username, password);
            });
        });
    }

    public void logout() {
        currentUser.setValue(null);
    }
}
