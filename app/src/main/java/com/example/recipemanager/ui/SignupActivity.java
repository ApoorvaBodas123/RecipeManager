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

import com.example.recipemanager.R;
import com.example.recipemanager.data.UserRepository;
import com.google.android.material.textfield.TextInputLayout;

public class SignupActivity extends AppCompatActivity {
    private EditText fullNameEditText, emailEditText, usernameEditText, passwordEditText, confirmPasswordEditText;
    private TextInputLayout fullNameLayout, emailLayout, usernameLayout, passwordLayout, confirmPasswordLayout;
    private ProgressBar progressBar;
    private UserViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Use UserViewModelFactory to create the ViewModel with its dependencies
        UserViewModelFactory factory = new UserViewModelFactory(getApplication());
        viewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);

        initializeViews();
        setupClickListeners();
        observeViewModel();
    }

    private void initializeViews() {
        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        
        fullNameLayout = findViewById(R.id.fullNameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        usernameLayout = findViewById(R.id.usernameLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        Button signupButton = findViewById(R.id.signupButton);
        signupButton.setOnClickListener(v -> attemptSignup());

        TextView loginLink = findViewById(R.id.loginLink);
        loginLink.setOnClickListener(v -> navigateToLogin());
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
                showSuccess(getString(R.string.registration_successful));
                navigateToLogin();
            }
        });
    }

    private void attemptSignup() {
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (validateInputs(fullName, email, username, password, confirmPassword)) {
            viewModel.register(username, email, password, fullName);
        }
    }

    private boolean validateInputs(String fullName, String email, String username, 
                                 String password, String confirmPassword) {
        boolean isValid = true;

        if (fullName.isEmpty()) {
            fullNameLayout.setError("Full name is required");
            isValid = false;
        } else {
            fullNameLayout.setError(null);
        }

        if (email.isEmpty()) {
            emailLayout.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Please enter a valid email");
            isValid = false;
        } else {
            emailLayout.setError(null);
        }

        if (username.isEmpty()) {
            usernameLayout.setError("Username is required");
            isValid = false;
        } else {
            usernameLayout.setError(null);
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            isValid = false;
        } else {
            passwordLayout.setError(null);
        }

        if (!confirmPassword.equals(password)) {
            confirmPasswordLayout.setError("Passwords do not match");
            isValid = false;
        } else {
            confirmPasswordLayout.setError(null);
        }

        return isValid;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
