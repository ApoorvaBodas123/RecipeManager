package com.example.recipemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.example.recipemanager.ui.ViewModelFactory;

import com.example.recipemanager.databinding.ActivityMainBinding;
import com.example.recipemanager.ui.AddEditRecipeActivity;
import com.example.recipemanager.ui.LoginActivity;
import com.example.recipemanager.data.AppDatabase;
import com.example.recipemanager.ui.RecipeListFragment;
import com.example.recipemanager.ui.UserViewModel;
import com.example.recipemanager.ui.UserViewModelFactory;
import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize the database
        AppDatabase.init(getApplicationContext());
        
        // Initialize ViewModel with factory
        UserViewModelFactory factory = new UserViewModelFactory(getApplication());
        userViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);
        
        // Check if user is logged in
        userViewModel.getCurrentUser().observe(this, user -> {
            if (user == null) {
                // User is not logged in, redirect to LoginActivity
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }
            // User is logged in, proceed with normal app flow
            initializeUI();
        });
        
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
    
    private void initializeUI() {
        // Any UI initialization that should happen after successful authentication
        // For example, you might want to update the UI based on the logged-in user
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // Handle logout
            userViewModel.logout();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
