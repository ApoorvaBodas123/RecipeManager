package com.example.recipemanager.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.recipemanager.R;
import com.example.recipemanager.data.Recipe;
import com.example.recipemanager.databinding.ActivityRecipeDetailBinding;

public class RecipeDetailActivity extends AppCompatActivity {

    private ActivityRecipeDetailBinding binding;
    private RecipeViewModel vm;
    private long id;
    private Recipe current;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewModelFactory factory = new ViewModelFactory(getApplication());
        vm = new ViewModelProvider(this, factory).get(RecipeViewModel.class);
        id = getIntent().getLongExtra("id", 0);
        vm.getById(id).observe(this, r -> {
            current = r;
            if (r != null) {
                binding.tvName.setText(r.name);
                binding.tvIngredients.setText(r.ingredients);
                binding.tvSteps.setText(r.steps);
                if (r.imageUri != null && !r.imageUri.isEmpty()) {
                    try {
                        Glide.with(this)
                            .load(Uri.parse(r.imageUri))
                            .placeholder(R.drawable.ic_placeholder)
                            .error(R.drawable.ic_ingredient)
                            .into(binding.ivPhoto);
                    } catch (Exception e) {
                        binding.ivPhoto.setImageResource(R.drawable.ic_ingredient);
                    }
                } else {
                    binding.ivPhoto.setImageResource(R.drawable.ic_placeholder);
                }
                binding.btnFav.setText(r.favorite ? "Unfavourite" : "Favourite");
            }
        });

        binding.btnFav.setOnClickListener(v -> {
            if (current == null) return;
            current.favorite = !current.favorite;
            vm.save(current);
        });

        binding.btnEdit.setOnClickListener(v -> {
            if (current == null) return;
            Intent i = new Intent(this, AddEditRecipeActivity.class);
            i.putExtra("recipe_id", id);
            startActivity(i);
        });

        binding.btnDelete.setOnClickListener(v -> {
            if (current == null) return;
            // Show confirmation dialog
            new android.app.AlertDialog.Builder(this)
                .setTitle("Delete Recipe")
                .setMessage("Are you sure you want to delete this recipe?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    vm.delete(current);
                    finish(); // Close the detail activity after deletion
                })
                .setNegativeButton("Cancel", null)
                .show();
        });
    }
}
