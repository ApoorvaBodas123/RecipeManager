package com.example.recipemanager.data;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private final UserDao userDao;
    private final ExecutorService executorService;
    private static volatile UserRepository instance;

    private UserRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        this.userDao = database.userDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public static synchronized UserRepository getInstance(Application application) {
        if (instance == null) {
            instance = new UserRepository(application);
        }
        return instance;
    }

    public void insert(User user) {
        executorService.execute(() -> userDao.insert(user));
    }

    public void login(String username, String password, LoginCallback callback) {
        executorService.execute(() -> {
            User user = userDao.login(username, password);
            if (user != null) {
                callback.onSuccess(user);
            } else {
                callback.onError("Invalid username or password");
            }
        });
    }

    public void checkUsernameExists(String username, CheckUserCallback callback) {
        executorService.execute(() -> {
            User user = userDao.findByUsername(username);
            callback.onResult(user != null);
        });
    }

    public void checkEmailExists(String email, CheckUserCallback callback) {
        executorService.execute(() -> {
            User user = userDao.findByEmail(email);
            callback.onResult(user != null);
        });
    }

    public interface LoginCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    public interface CheckUserCallback {
        void onResult(boolean exists);
    }
}
