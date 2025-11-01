package com.example.recipemanager.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.recipemanager.data.converters.DifficultyConverter;
import com.example.recipemanager.data.converters.NutritionInfoConverter;
import com.example.recipemanager.data.converters.StringSetConverter;

@Database(entities = {Recipe.class, User.class}, version = 2, exportSchema = false)
@TypeConverters({StringSetConverter.class, DifficultyConverter.class, NutritionInfoConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "recipe_database";
    private static volatile AppDatabase INSTANCE;

    public abstract RecipeDao recipeDao();
    public abstract UserDao userDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, DATABASE_NAME)
                            .addMigrations(MIGRATION_1_2)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Database migration from version 1 to 2
    private static final androidx.room.migration.Migration MIGRATION_1_2 = new androidx.room.migration.Migration(1, 2) {
        @Override
        public void migrate(androidx.sqlite.db.SupportSQLiteDatabase database) {
            // Add new columns
            database.execSQL("ALTER TABLE recipes ADD COLUMN prepTime INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE recipes ADD COLUMN cookTime INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE recipes ADD COLUMN difficulty TEXT");
            database.execSQL("ALTER TABLE recipes ADD COLUMN dietaryRestrictions TEXT");
            
            // Add columns for nutrition info
            database.execSQL("ALTER TABLE recipes ADD COLUMN nutritionInfo_calories INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE recipes ADD COLUMN nutritionInfo_protein INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE recipes ADD COLUMN nutritionInfo_carbs INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE recipes ADD COLUMN nutritionInfo_fat INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE recipes ADD COLUMN nutritionInfo_fiber INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE recipes ADD COLUMN nutritionInfo_sugar INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE recipes ADD COLUMN nutritionInfo_sodium INTEGER NOT NULL DEFAULT 0");
        }
    };

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
