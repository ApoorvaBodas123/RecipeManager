package com.example.recipemanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.recipemanager.databinding.ActivityMainBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.example.recipemanager.ui.AddEditRecipeActivity;
import com.example.recipemanager.ui.RecipeListFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MaterialToolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        binding.fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, AddEditRecipeActivity.class));
        });

        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(binding.fragmentContainer.getId(), new RecipeListFragment());
            ft.commit();
        }
    }
}
