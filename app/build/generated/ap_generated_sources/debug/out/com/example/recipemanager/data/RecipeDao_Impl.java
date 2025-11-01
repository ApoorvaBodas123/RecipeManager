package com.example.recipemanager.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.recipemanager.data.converters.DifficultyConverter;
import com.example.recipemanager.data.converters.NutritionInfoConverter;
import com.example.recipemanager.data.converters.StringSetConverter;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class RecipeDao_Impl implements RecipeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Recipe> __insertionAdapterOfRecipe;

  private final EntityDeletionOrUpdateAdapter<Recipe> __deletionAdapterOfRecipe;

  private final EntityDeletionOrUpdateAdapter<Recipe> __updateAdapterOfRecipe;

  public RecipeDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRecipe = new EntityInsertionAdapter<Recipe>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `recipes` (`id`,`name`,`ingredients`,`steps`,`imageUri`,`category`,`favorite`,`prepTime`,`cookTime`,`difficulty`,`dietaryRestrictions`,`nutritionInfo`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Recipe entity) {
        statement.bindLong(1, entity.id);
        if (entity.name == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.name);
        }
        if (entity.ingredients == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.ingredients);
        }
        if (entity.steps == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.steps);
        }
        if (entity.imageUri == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.imageUri);
        }
        if (entity.category == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.category);
        }
        final int _tmp = entity.favorite ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindLong(8, entity.prepTime);
        statement.bindLong(9, entity.cookTime);
        final String _tmp_1 = DifficultyConverter.fromDifficulty(entity.difficulty);
        if (_tmp_1 == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, _tmp_1);
        }
        final String _tmp_2 = StringSetConverter.fromSet(entity.dietaryRestrictions);
        if (_tmp_2 == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, _tmp_2);
        }
        final String _tmp_3 = NutritionInfoConverter.toString(entity.nutritionInfo);
        if (_tmp_3 == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, _tmp_3);
        }
      }
    };
    this.__deletionAdapterOfRecipe = new EntityDeletionOrUpdateAdapter<Recipe>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `recipes` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Recipe entity) {
        statement.bindLong(1, entity.id);
      }
    };
    this.__updateAdapterOfRecipe = new EntityDeletionOrUpdateAdapter<Recipe>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `recipes` SET `id` = ?,`name` = ?,`ingredients` = ?,`steps` = ?,`imageUri` = ?,`category` = ?,`favorite` = ?,`prepTime` = ?,`cookTime` = ?,`difficulty` = ?,`dietaryRestrictions` = ?,`nutritionInfo` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Recipe entity) {
        statement.bindLong(1, entity.id);
        if (entity.name == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.name);
        }
        if (entity.ingredients == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.ingredients);
        }
        if (entity.steps == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.steps);
        }
        if (entity.imageUri == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.imageUri);
        }
        if (entity.category == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.category);
        }
        final int _tmp = entity.favorite ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindLong(8, entity.prepTime);
        statement.bindLong(9, entity.cookTime);
        final String _tmp_1 = DifficultyConverter.fromDifficulty(entity.difficulty);
        if (_tmp_1 == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, _tmp_1);
        }
        final String _tmp_2 = StringSetConverter.fromSet(entity.dietaryRestrictions);
        if (_tmp_2 == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, _tmp_2);
        }
        final String _tmp_3 = NutritionInfoConverter.toString(entity.nutritionInfo);
        if (_tmp_3 == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, _tmp_3);
        }
        statement.bindLong(13, entity.id);
      }
    };
  }

  @Override
  public long insert(final Recipe recipe) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfRecipe.insertAndReturnId(recipe);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final Recipe recipe) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfRecipe.handle(recipe);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final Recipe recipe) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfRecipe.handle(recipe);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<Recipe>> getAll() {
    final String _sql = "SELECT * FROM recipes ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"recipes"}, false, new Callable<List<Recipe>>() {
      @Override
      @Nullable
      public List<Recipe> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIngredients = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredients");
          final int _cursorIndexOfSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "steps");
          final int _cursorIndexOfImageUri = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUri");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "favorite");
          final int _cursorIndexOfPrepTime = CursorUtil.getColumnIndexOrThrow(_cursor, "prepTime");
          final int _cursorIndexOfCookTime = CursorUtil.getColumnIndexOrThrow(_cursor, "cookTime");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfDietaryRestrictions = CursorUtil.getColumnIndexOrThrow(_cursor, "dietaryRestrictions");
          final int _cursorIndexOfNutritionInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "nutritionInfo");
          final List<Recipe> _result = new ArrayList<Recipe>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Recipe _item;
            _item = new Recipe();
            _item.id = _cursor.getLong(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfName)) {
              _item.name = null;
            } else {
              _item.name = _cursor.getString(_cursorIndexOfName);
            }
            if (_cursor.isNull(_cursorIndexOfIngredients)) {
              _item.ingredients = null;
            } else {
              _item.ingredients = _cursor.getString(_cursorIndexOfIngredients);
            }
            if (_cursor.isNull(_cursorIndexOfSteps)) {
              _item.steps = null;
            } else {
              _item.steps = _cursor.getString(_cursorIndexOfSteps);
            }
            if (_cursor.isNull(_cursorIndexOfImageUri)) {
              _item.imageUri = null;
            } else {
              _item.imageUri = _cursor.getString(_cursorIndexOfImageUri);
            }
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _item.category = null;
            } else {
              _item.category = _cursor.getString(_cursorIndexOfCategory);
            }
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfFavorite);
            _item.favorite = _tmp != 0;
            _item.prepTime = _cursor.getInt(_cursorIndexOfPrepTime);
            _item.cookTime = _cursor.getInt(_cursorIndexOfCookTime);
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfDifficulty)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfDifficulty);
            }
            _item.difficulty = DifficultyConverter.toDifficulty(_tmp_1);
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfDietaryRestrictions)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfDietaryRestrictions);
            }
            _item.dietaryRestrictions = StringSetConverter.fromString(_tmp_2);
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfNutritionInfo)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfNutritionInfo);
            }
            _item.nutritionInfo = NutritionInfoConverter.fromString(_tmp_3);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<Recipe>> getFavorites() {
    final String _sql = "SELECT * FROM recipes WHERE favorite = 1 ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"recipes"}, false, new Callable<List<Recipe>>() {
      @Override
      @Nullable
      public List<Recipe> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIngredients = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredients");
          final int _cursorIndexOfSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "steps");
          final int _cursorIndexOfImageUri = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUri");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "favorite");
          final int _cursorIndexOfPrepTime = CursorUtil.getColumnIndexOrThrow(_cursor, "prepTime");
          final int _cursorIndexOfCookTime = CursorUtil.getColumnIndexOrThrow(_cursor, "cookTime");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfDietaryRestrictions = CursorUtil.getColumnIndexOrThrow(_cursor, "dietaryRestrictions");
          final int _cursorIndexOfNutritionInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "nutritionInfo");
          final List<Recipe> _result = new ArrayList<Recipe>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Recipe _item;
            _item = new Recipe();
            _item.id = _cursor.getLong(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfName)) {
              _item.name = null;
            } else {
              _item.name = _cursor.getString(_cursorIndexOfName);
            }
            if (_cursor.isNull(_cursorIndexOfIngredients)) {
              _item.ingredients = null;
            } else {
              _item.ingredients = _cursor.getString(_cursorIndexOfIngredients);
            }
            if (_cursor.isNull(_cursorIndexOfSteps)) {
              _item.steps = null;
            } else {
              _item.steps = _cursor.getString(_cursorIndexOfSteps);
            }
            if (_cursor.isNull(_cursorIndexOfImageUri)) {
              _item.imageUri = null;
            } else {
              _item.imageUri = _cursor.getString(_cursorIndexOfImageUri);
            }
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _item.category = null;
            } else {
              _item.category = _cursor.getString(_cursorIndexOfCategory);
            }
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfFavorite);
            _item.favorite = _tmp != 0;
            _item.prepTime = _cursor.getInt(_cursorIndexOfPrepTime);
            _item.cookTime = _cursor.getInt(_cursorIndexOfCookTime);
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfDifficulty)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfDifficulty);
            }
            _item.difficulty = DifficultyConverter.toDifficulty(_tmp_1);
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfDietaryRestrictions)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfDietaryRestrictions);
            }
            _item.dietaryRestrictions = StringSetConverter.fromString(_tmp_2);
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfNutritionInfo)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfNutritionInfo);
            }
            _item.nutritionInfo = NutritionInfoConverter.fromString(_tmp_3);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<Recipe>> searchByName(final String query) {
    final String _sql = "SELECT * FROM recipes WHERE name LIKE '%' || ? || '%' ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"recipes"}, false, new Callable<List<Recipe>>() {
      @Override
      @Nullable
      public List<Recipe> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIngredients = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredients");
          final int _cursorIndexOfSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "steps");
          final int _cursorIndexOfImageUri = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUri");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "favorite");
          final int _cursorIndexOfPrepTime = CursorUtil.getColumnIndexOrThrow(_cursor, "prepTime");
          final int _cursorIndexOfCookTime = CursorUtil.getColumnIndexOrThrow(_cursor, "cookTime");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfDietaryRestrictions = CursorUtil.getColumnIndexOrThrow(_cursor, "dietaryRestrictions");
          final int _cursorIndexOfNutritionInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "nutritionInfo");
          final List<Recipe> _result = new ArrayList<Recipe>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Recipe _item;
            _item = new Recipe();
            _item.id = _cursor.getLong(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfName)) {
              _item.name = null;
            } else {
              _item.name = _cursor.getString(_cursorIndexOfName);
            }
            if (_cursor.isNull(_cursorIndexOfIngredients)) {
              _item.ingredients = null;
            } else {
              _item.ingredients = _cursor.getString(_cursorIndexOfIngredients);
            }
            if (_cursor.isNull(_cursorIndexOfSteps)) {
              _item.steps = null;
            } else {
              _item.steps = _cursor.getString(_cursorIndexOfSteps);
            }
            if (_cursor.isNull(_cursorIndexOfImageUri)) {
              _item.imageUri = null;
            } else {
              _item.imageUri = _cursor.getString(_cursorIndexOfImageUri);
            }
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _item.category = null;
            } else {
              _item.category = _cursor.getString(_cursorIndexOfCategory);
            }
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfFavorite);
            _item.favorite = _tmp != 0;
            _item.prepTime = _cursor.getInt(_cursorIndexOfPrepTime);
            _item.cookTime = _cursor.getInt(_cursorIndexOfCookTime);
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfDifficulty)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfDifficulty);
            }
            _item.difficulty = DifficultyConverter.toDifficulty(_tmp_1);
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfDietaryRestrictions)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfDietaryRestrictions);
            }
            _item.dietaryRestrictions = StringSetConverter.fromString(_tmp_2);
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfNutritionInfo)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfNutritionInfo);
            }
            _item.nutritionInfo = NutritionInfoConverter.fromString(_tmp_3);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<Recipe>> filterByCategory(final String category) {
    final String _sql = "SELECT * FROM recipes WHERE category = ? ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (category == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, category);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"recipes"}, false, new Callable<List<Recipe>>() {
      @Override
      @Nullable
      public List<Recipe> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIngredients = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredients");
          final int _cursorIndexOfSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "steps");
          final int _cursorIndexOfImageUri = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUri");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "favorite");
          final int _cursorIndexOfPrepTime = CursorUtil.getColumnIndexOrThrow(_cursor, "prepTime");
          final int _cursorIndexOfCookTime = CursorUtil.getColumnIndexOrThrow(_cursor, "cookTime");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfDietaryRestrictions = CursorUtil.getColumnIndexOrThrow(_cursor, "dietaryRestrictions");
          final int _cursorIndexOfNutritionInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "nutritionInfo");
          final List<Recipe> _result = new ArrayList<Recipe>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Recipe _item;
            _item = new Recipe();
            _item.id = _cursor.getLong(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfName)) {
              _item.name = null;
            } else {
              _item.name = _cursor.getString(_cursorIndexOfName);
            }
            if (_cursor.isNull(_cursorIndexOfIngredients)) {
              _item.ingredients = null;
            } else {
              _item.ingredients = _cursor.getString(_cursorIndexOfIngredients);
            }
            if (_cursor.isNull(_cursorIndexOfSteps)) {
              _item.steps = null;
            } else {
              _item.steps = _cursor.getString(_cursorIndexOfSteps);
            }
            if (_cursor.isNull(_cursorIndexOfImageUri)) {
              _item.imageUri = null;
            } else {
              _item.imageUri = _cursor.getString(_cursorIndexOfImageUri);
            }
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _item.category = null;
            } else {
              _item.category = _cursor.getString(_cursorIndexOfCategory);
            }
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfFavorite);
            _item.favorite = _tmp != 0;
            _item.prepTime = _cursor.getInt(_cursorIndexOfPrepTime);
            _item.cookTime = _cursor.getInt(_cursorIndexOfCookTime);
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfDifficulty)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfDifficulty);
            }
            _item.difficulty = DifficultyConverter.toDifficulty(_tmp_1);
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfDietaryRestrictions)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfDietaryRestrictions);
            }
            _item.dietaryRestrictions = StringSetConverter.fromString(_tmp_2);
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfNutritionInfo)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfNutritionInfo);
            }
            _item.nutritionInfo = NutritionInfoConverter.fromString(_tmp_3);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<Recipe> getById(final long id) {
    final String _sql = "SELECT * FROM recipes WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return __db.getInvalidationTracker().createLiveData(new String[] {"recipes"}, false, new Callable<Recipe>() {
      @Override
      @Nullable
      public Recipe call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIngredients = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredients");
          final int _cursorIndexOfSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "steps");
          final int _cursorIndexOfImageUri = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUri");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "favorite");
          final int _cursorIndexOfPrepTime = CursorUtil.getColumnIndexOrThrow(_cursor, "prepTime");
          final int _cursorIndexOfCookTime = CursorUtil.getColumnIndexOrThrow(_cursor, "cookTime");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfDietaryRestrictions = CursorUtil.getColumnIndexOrThrow(_cursor, "dietaryRestrictions");
          final int _cursorIndexOfNutritionInfo = CursorUtil.getColumnIndexOrThrow(_cursor, "nutritionInfo");
          final Recipe _result;
          if (_cursor.moveToFirst()) {
            _result = new Recipe();
            _result.id = _cursor.getLong(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfName)) {
              _result.name = null;
            } else {
              _result.name = _cursor.getString(_cursorIndexOfName);
            }
            if (_cursor.isNull(_cursorIndexOfIngredients)) {
              _result.ingredients = null;
            } else {
              _result.ingredients = _cursor.getString(_cursorIndexOfIngredients);
            }
            if (_cursor.isNull(_cursorIndexOfSteps)) {
              _result.steps = null;
            } else {
              _result.steps = _cursor.getString(_cursorIndexOfSteps);
            }
            if (_cursor.isNull(_cursorIndexOfImageUri)) {
              _result.imageUri = null;
            } else {
              _result.imageUri = _cursor.getString(_cursorIndexOfImageUri);
            }
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _result.category = null;
            } else {
              _result.category = _cursor.getString(_cursorIndexOfCategory);
            }
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfFavorite);
            _result.favorite = _tmp != 0;
            _result.prepTime = _cursor.getInt(_cursorIndexOfPrepTime);
            _result.cookTime = _cursor.getInt(_cursorIndexOfCookTime);
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfDifficulty)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfDifficulty);
            }
            _result.difficulty = DifficultyConverter.toDifficulty(_tmp_1);
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfDietaryRestrictions)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfDietaryRestrictions);
            }
            _result.dietaryRestrictions = StringSetConverter.fromString(_tmp_2);
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfNutritionInfo)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfNutritionInfo);
            }
            _result.nutritionInfo = NutritionInfoConverter.fromString(_tmp_3);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
