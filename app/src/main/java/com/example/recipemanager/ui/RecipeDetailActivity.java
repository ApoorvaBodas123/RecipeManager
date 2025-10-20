package com.example.recipemanager.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

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

        vm = new ViewModelProvider(this).get(RecipeViewModel.class);
        id = getIntent().getLongExtra("id", 0);
        vm.getById(id).observe(this, r -> {
            current = r;
            if (r != null) {
                binding.tvName.setText(r.name);
                binding.tvIngredients.setText(r.ingredients);
                binding.tvSteps.setText(r.steps);
                if (r.imageUri != null) binding.ivPhoto.setImageURI(Uri.parse(r.imageUri));
                binding.btnFav.setText(r.favorite ? "Unfavourite" : "Favourite");
            }
        });

        binding.btnFav.setOnClickListener(v -> {
            if (current == null) return;
            current.favorite = !current.favorite;
            vm.save(current);
        });

        binding.btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(this, AddEditRecipeActivity.class);
            i.putExtra("id", id);
            startActivity(i);
        });

        binding.btnShare.setOnClickListener(v -> {
            if (current == null) return;
            String text = current.name + "\n\nIngredients:\n" + current.ingredients + "\n\nSteps:\n" + current.steps;
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(share, "Share recipe"));
        });
    }
}
