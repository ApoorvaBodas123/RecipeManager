package com.example.recipemanager.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.recipemanager.data.Recipe;
import com.example.recipemanager.databinding.ActivityAddEditRecipeBinding;

public class AddEditRecipeActivity extends AppCompatActivity {

    private ActivityAddEditRecipeBinding binding;
    private RecipeViewModel vm;
    private String imageUri;
    private long editingId = 0;

    private final ActivityResultLauncher<String[]> pickImage = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
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

        vm = new ViewModelProvider(this).get(RecipeViewModel.class);

        // Setup category spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                com.example.recipemanager.R.array.categories,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(adapter);

        if (getIntent().hasExtra("id")) {
            editingId = getIntent().getLongExtra("id", 0);
            vm.getById(editingId).observe(this, r -> {
                if (r != null) {
                    binding.etName.setText(r.name);
                    binding.etIngredients.setText(r.ingredients);
                    binding.etSteps.setText(r.steps);
                    imageUri = r.imageUri;
                    if (imageUri != null) binding.ivPreview.setImageURI(Uri.parse(imageUri));
                    if (r.category != null) {
                        switch (r.category) {
                            case "Dessert": binding.spinnerCategory.setSelection(1); break;
                            case "Snack": binding.spinnerCategory.setSelection(2); break;
                            case "Main-Course": binding.spinnerCategory.setSelection(3); break;
                            case "Beverage": binding.spinnerCategory.setSelection(4); break;
                            default: binding.spinnerCategory.setSelection(0);
                        }
                    }
                }
            });
        }

        binding.btnPickImage.setOnClickListener(v -> pickImage.launch(new String[]{"image/*"}));

        binding.btnSave.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String ing = binding.etIngredients.getText().toString().trim();
            String steps = binding.etSteps.getText().toString().trim();
            String category = binding.spinnerCategory.getSelectedItemPosition() == 0 ? null : binding.spinnerCategory.getSelectedItem().toString();
            if (name.isEmpty()) { binding.etName.setError("Required"); return; }
            Recipe r = new Recipe(name, ing, steps, imageUri, category, false);
            r.id = editingId;
            vm.save(r);
            finish();
        });
    }
}
