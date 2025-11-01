package com.example.recipemanager.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.recipemanager.R;
import com.example.recipemanager.data.Recipe;
import com.example.recipemanager.data.RecipeRepository;
import com.example.recipemanager.databinding.ActivityAddEditRecipeBinding;
import com.example.recipemanager.ui.RecipeViewModelFactory;
import com.google.android.material.chip.Chip;

import java.util.HashSet;
import java.util.Set;

public class AddEditRecipeActivity extends AppCompatActivity {

    private ActivityAddEditRecipeBinding binding;
    private RecipeViewModel vm;
    private String imageUri;
    private long editingId = 0;
    private ArrayAdapter<String> difficultyAdapter;

    private final ActivityResultLauncher<String[]> pickImage = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    imageUri = uri.toString();
                    binding.ivPreview.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize RecipeViewModel with factory
        RecipeRepository recipeRepository = RecipeRepository.getInstance(getApplication());
        RecipeViewModelFactory factory = new RecipeViewModelFactory(recipeRepository);
        vm = new ViewModelProvider(this, factory).get(RecipeViewModel.class);

        // Setup category spinner
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.categories,
                android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(categoryAdapter);

        // Setup difficulty dropdown
        String[] difficultyLevels = getResources().getStringArray(R.array.difficulty_levels);
        difficultyAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_dropdown_item_1line, 
                difficultyLevels);
        ((AutoCompleteTextView) binding.etDifficulty).setAdapter(difficultyAdapter);

        // Check if we're editing an existing recipe
        if (getIntent().hasExtra("recipe_id")) {
            editingId = getIntent().getLongExtra("recipe_id", 0);
            vm.getById(editingId).observe(this, recipe -> {
                if (recipe != null) {
                    populateForm(recipe);
                }
            });
        }

        binding.btnPickImage.setOnClickListener(v -> {
            pickImage.launch(new String[]{"image/*"});
        });

        binding.btnSave.setOnClickListener(v -> saveRecipe());
    }

    private void populateForm(Recipe recipe) {
        binding.etName.setText(recipe.name);
        binding.etIngredients.setText(recipe.ingredients);
        binding.etSteps.setText(recipe.steps);
        imageUri = recipe.imageUri;
        
        if (recipe.imageUri != null) {
            binding.ivPreview.setImageURI(Uri.parse(recipe.imageUri));
        }
        
        // Set category spinner
        if (recipe.category != null) {
            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) binding.spinnerCategory.getAdapter();
            int position = adapter.getPosition(recipe.category);
            if (position >= 0) {
                binding.spinnerCategory.setSelection(position);
            }
        }
        
        // Set prep and cook times
        if (recipe.prepTime > 0) {
            binding.etPrepTime.setText(String.valueOf(recipe.prepTime));
        }
        if (recipe.cookTime > 0) {
            binding.etCookTime.setText(String.valueOf(recipe.cookTime));
        }
        
        // Set difficulty
        if (recipe.difficulty != null) {
            ((AutoCompleteTextView) binding.etDifficulty).setText(recipe.difficulty.toString());
        }
        
        // Set dietary restrictions
        if (recipe.dietaryRestrictions != null) {
            for (String restriction : recipe.dietaryRestrictions) {
                switch (restriction) {
                    case "VEGETARIAN":
                        binding.chipVegetarian.setChecked(true);
                        break;
                    case "VEGAN":
                        binding.chipVegan.setChecked(true);
                        break;
                    case "GLUTEN_FREE":
                        binding.chipGlutenFree.setChecked(true);
                        break;
                    case "DAIRY_FREE":
                        binding.chipDairyFree.setChecked(true);
                        break;
                    case "NUT_FREE":
                        binding.chipNutFree.setChecked(true);
                        break;
                }
            }
        }
        
        // Set nutritional information
        if (recipe.nutritionInfo != null) {
            Recipe.NutritionInfo info = recipe.nutritionInfo;
            if (info.calories > 0) binding.etCalories.setText(String.valueOf(info.calories));
            if (info.protein > 0) binding.etProtein.setText(String.valueOf(info.protein));
            if (info.carbs > 0) binding.etCarbs.setText(String.valueOf(info.carbs));
            if (info.fat > 0) binding.etFat.setText(String.valueOf(info.fat));
            if (info.fiber > 0) binding.etFiber.setText(String.valueOf(info.fiber));
            if (info.sugar > 0) binding.etSugar.setText(String.valueOf(info.sugar));
            if (info.sodium > 0) binding.etSodium.setText(String.valueOf(info.sodium));
        }
    }

    private void saveRecipe() {
        String name = binding.etName.getText().toString().trim();
        String ingredients = binding.etIngredients.getText().toString().trim();
        String steps = binding.etSteps.getText().toString().trim();
        String category = (String) binding.spinnerCategory.getSelectedItem();

        if (name.isEmpty() || ingredients.isEmpty() || steps.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get prep and cook times
        int prepTime = 0;
        try {
            String prepTimeStr = binding.etPrepTime.getText().toString();
            if (!prepTimeStr.isEmpty()) {
                prepTime = Integer.parseInt(prepTimeStr);
            }
        } catch (NumberFormatException e) {
            // Use default value of 0
        }

        int cookTime = 0;
        try {
            String cookTimeStr = binding.etCookTime.getText().toString();
            if (!cookTimeStr.isEmpty()) {
                cookTime = Integer.parseInt(cookTimeStr);
            }
        } catch (NumberFormatException e) {
            // Use default value of 0
        }

        // Get difficulty
        Recipe.Difficulty difficulty = null;
        String difficultyStr = binding.etDifficulty.getText().toString();
        if (!difficultyStr.isEmpty()) {
            try {
                difficulty = Recipe.Difficulty.valueOf(difficultyStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid difficulty, will be null
            }
        }

        // Get dietary restrictions
        Set<String> dietaryRestrictions = new HashSet<>();
        if (binding.chipVegetarian.isChecked()) dietaryRestrictions.add("VEGETARIAN");
        if (binding.chipVegan.isChecked()) dietaryRestrictions.add("VEGAN");
        if (binding.chipGlutenFree.isChecked()) dietaryRestrictions.add("GLUTEN_FREE");
        if (binding.chipDairyFree.isChecked()) dietaryRestrictions.add("DAIRY_FREE");
        if (binding.chipNutFree.isChecked()) dietaryRestrictions.add("NUT_FREE");

        // Get nutritional information
        Recipe.NutritionInfo nutritionInfo = new Recipe.NutritionInfo(
                parseIntSafely(binding.etCalories.getText().toString()),
                parseIntSafely(binding.etProtein.getText().toString()),
                parseIntSafely(binding.etCarbs.getText().toString()),
                parseIntSafely(binding.etFat.getText().toString()),
                parseIntSafely(binding.etFiber.getText().toString()),
                parseIntSafely(binding.etSugar.getText().toString()),
                parseIntSafely(binding.etSodium.getText().toString())
        );

        Recipe recipe = new Recipe(
                name, 
                ingredients, 
                steps, 
                imageUri, 
                category, 
                false, 
                prepTime, 
                cookTime, 
                difficulty, 
                dietaryRestrictions, 
                nutritionInfo
        );
        
        if (editingId > 0) {
            recipe.id = editingId;
        }
        vm.save(recipe);
        Toast.makeText(this, "Recipe " + (editingId > 0 ? "updated" : "saved"), Toast.LENGTH_SHORT).show();
        finish();
    }
    
    private int parseIntSafely(String value) {
        try {
            return value.isEmpty() ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
