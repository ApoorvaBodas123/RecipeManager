package com.example.recipemanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.recipemanager.RecipeApplication;

import com.example.recipemanager.MainActivity;
import com.example.recipemanager.R;
import com.example.recipemanager.data.User;
import com.example.recipemanager.databinding.ActivityLoginBinding;
import com.example.recipemanager.ui.UserViewModel;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText, passwordEditText;
    private TextInputLayout usernameLayout, passwordLayout;
    private ProgressBar progressBar;
    private UserViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UserViewModelFactory factory = new UserViewModelFactory(getApplication());
        viewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);

        initializeViews();
        setupClickListeners();
        observeViewModel();
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        usernameLayout = findViewById(R.id.usernameLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> attemptLogin());

        TextView signupLink = findViewById(R.id.signupLink);
        signupLink.setOnClickListener(v -> navigateToSignup());
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                showError(error);
            }
        });

        viewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                navigateToMain();
            }
        });
    }

    private void attemptLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (validateInputs(username, password)) {
            viewModel.login(username, password);
        }
    }

    private boolean validateInputs(String username, String password) {
        boolean isValid = true;

        if (username.isEmpty()) {
            usernameLayout.setError("Username is required");
            isValid = false;
        } else {
            usernameLayout.setError(null);
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Password is required");
            isValid = false;
        } else {
            passwordLayout.setError(null);
        }

        return isValid;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToSignup() {
        startActivity(new Intent(this, SignupActivity.class));
    }

    private void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
