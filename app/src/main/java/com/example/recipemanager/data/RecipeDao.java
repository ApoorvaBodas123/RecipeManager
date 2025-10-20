package com.example.recipemanager.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Recipe recipe);

    @Update
    void update(Recipe recipe);

    @Delete
    void delete(Recipe recipe);

    @Query("SELECT * FROM recipes ORDER BY name ASC")
    LiveData<List<Recipe>> getAll();

    @Query("SELECT * FROM recipes WHERE favorite = 1 ORDER BY name ASC")
    LiveData<List<Recipe>> getFavorites();

    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    LiveData<List<Recipe>> searchByName(String query);

    @Query("SELECT * FROM recipes WHERE category = :category ORDER BY name ASC")
    LiveData<List<Recipe>> filterByCategory(String category);

    @Query("SELECT * FROM recipes WHERE id = :id LIMIT 1")
    LiveData<Recipe> getById(long id);
}
