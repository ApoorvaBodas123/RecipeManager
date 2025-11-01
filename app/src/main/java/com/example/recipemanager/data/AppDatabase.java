package com.example.recipemanager.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Recipe.class, User.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;
    private static final Object LOCK = new Object();

    public abstract RecipeDao recipeDao();
    public abstract UserDao userDao();
    
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LOCK) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "recipes_db"
                    )
                    .fallbackToDestructiveMigration()  // This will clear the database on version mismatch
                    .build();
                }
            }
        }
        return INSTANCE;
    }
    
    // For backward compatibility
    @Deprecated
    public static void init(Context context) {
        getInstance(context);
    }
    
    @Deprecated
    public static AppDatabase getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("AppDatabase not initialized. Call getInstance(context) or init(context) first.");
        }
        return INSTANCE;
    }
}
