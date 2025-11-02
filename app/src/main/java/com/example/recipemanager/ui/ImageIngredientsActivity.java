package com.example.recipemanager.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
// Camera imports removed as we're only using gallery
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipemanager.R;
import com.example.recipemanager.data.model.Ingredient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.IOException;
import com.example.recipemanager.ai.RecipeGenerator;
import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.launch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageIngredientsActivity extends AppCompatActivity {
    private static final String TAG = "ImageIngredients";
    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final String[] REQUIRED_PERMISSIONS;
    
    static {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            REQUIRED_PERMISSIONS = new String[]{
                Manifest.permission.READ_MEDIA_IMAGES
            };
        } else {
            REQUIRED_PERMISSIONS = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
            };
        }
    }
    private static final int PERMISSIONS_REQUEST_CODE = 10;

    private View progressBar;
    private MaterialCardView detectedIngredientsCard;
    private RecyclerView ingredientsList;
    private ExecutorService executorService;
    private MaterialButton selectImageButton;
    private MaterialButton generateRecipeButton;
    private ImageView selectedImageView;
    private View selectImageArea;
    private List<Ingredient> detectedIngredients = new ArrayList<>();
    private IngredientsAdapter ingredientsAdapter;
    private RecipeGenerator recipeGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_ingredients);

        // Initialize RecipeGenerator with your OpenAI API key
        // TODO: Replace "YOUR_OPENAI_API_KEY" with your actual OpenAI API key
        // For production, use a secure way to store API keys (e.g., Android Keystore, remote config)
        String openAiApiKey = "";
        recipeGenerator = new RecipeGenerator(openAiApiKey);

        progressBar = findViewById(R.id.progressBar);
        detectedIngredientsCard = findViewById(R.id.detectedIngredientsCard);
        ingredientsList = findViewById(R.id.ingredientsList);
        selectImageButton = findViewById(R.id.selectImageButton);
        generateRecipeButton = findViewById(R.id.generateRecipeButton);
        selectedImageView = findViewById(R.id.selectedImage);
        
        // Set up Generate Recipe button click listener
        generateRecipeButton.setOnClickListener(v -> generateRecipe());
        selectImageArea = findViewById(R.id.selectImageArea);
        
        // Set click listener for the entire select image area
        selectImageArea.setOnClickListener(v -> {
            if (allPermissionsGranted()) {
                openGallery();
            } else {
                requestStoragePermission();
            }
        });

        // Setup RecyclerView
        ingredientsAdapter = new IngredientsAdapter();
        ingredientsList.setLayoutManager(new GridLayoutManager(this, 3));
        ingredientsList.setAdapter(ingredientsAdapter);

        // Set up click listeners
        selectImageButton.setOnClickListener(v -> {
            if (allPermissionsGranted()) {
                openGallery();
            } else {
                requestStoragePermission();
            }
        });
        
        findViewById(R.id.generateRecipeButton).setOnClickListener(v -> generateRecipe());

        executorService = Executors.newSingleThreadExecutor();
        
        // Show the select image button by default
        showSelectImageUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // We'll handle permissions in onCreate
    }

    // Camera functionality removed, using gallery only

    private void openGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        } catch (Exception e) {
            Log.e(TAG, "Error opening gallery: " + e.getMessage());
            Toast.makeText(this, "Error opening gallery: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }
    
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this, 
            REQUIRED_PERMISSIONS,
            PERMISSIONS_REQUEST_CODE
        );
    }
    
    private void showSelectImageUI() {
        runOnUiThread(() -> {
            if (detectedIngredientsCard != null) {
                detectedIngredientsCard.setVisibility(View.GONE);
            }
            if (selectImageArea != null) {
                selectImageArea.setVisibility(View.VISIBLE);
            }
            if (selectedImageView != null) {
                selectedImageView.setVisibility(View.GONE);
            }
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    
    private void showProcessingUI() {
        runOnUiThread(() -> {
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
            if (detectedIngredientsCard != null) {
                detectedIngredientsCard.setVisibility(View.GONE);
            }
            if (selectImageArea != null) {
                selectImageArea.setVisibility(View.GONE);
            }
            if (selectedImageView != null) {
                selectedImageView.setVisibility(View.VISIBLE);
            }
        });
    }
    
    private void showResultsUI() {
        runOnUiThread(() -> {
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            if (detectedIngredientsCard != null) {
                detectedIngredientsCard.setVisibility(View.VISIBLE);
            }
            if (selectImageArea != null) {
                selectImageArea.setVisibility(View.GONE);
            }
            if (selectedImageView != null) {
                selectedImageView.setVisibility(View.VISIBLE);
            }
            // Show and enable Generate Recipe button if we have ingredients
            if (generateRecipeButton != null) {
                generateRecipeButton.setVisibility(View.VISIBLE);
                generateRecipeButton.setEnabled(!detectedIngredients.isEmpty());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                processImage(imageUri);
            }
        }
    }

    private void processImage(Uri imageUri) {
        showProcessingUI();
        
        // Display the selected image
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            selectedImageView.setImageBitmap(bitmap);
            selectedImageView.setVisibility(View.VISIBLE);
            
            // Process the image in background
            executorService.execute(() -> {
                try {
                    InputImage image = InputImage.fromBitmap(bitmap, 0);
                    analyzeImage(image);
                } catch (Exception e) {
                    Log.e(TAG, "Error analyzing image: " + e.getMessage());
                    runOnUiThread(() -> {
                        showSelectImageUI();
                        Toast.makeText(ImageIngredientsActivity.this, 
                            "Error analyzing image: " + e.getMessage(), 
                            Toast.LENGTH_LONG).show();
                    });
                }
            });
        } catch (IOException e) {
                Log.e(TAG, "Failed to load image: " + e.getMessage());
                runOnUiThread(() -> {
                    showSelectImageUI();
                    Toast.makeText(this, "Failed to load image: " + e.getMessage(), 
                        Toast.LENGTH_LONG).show();
                });
            }
    }

    private void analyzeImage(InputImage image) {
        showProgress(true);
        
        ImageLabelerOptions options = new ImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.7f)
                .build();
        
        ImageLabeler labeler = ImageLabeling.getClient(options);
        
        labeler.process(image)
                .addOnSuccessListener(labels -> {
                    detectedIngredients.clear();
                    for (ImageLabel label : labels) {
                        String text = label.getText().toLowerCase();
                        float confidence = label.getConfidence();
                        
                        // Filter out non-food items and low confidence labels
                        if (isLikelyIngredient(text) && confidence > 0.7) {
                            detectedIngredients.add(new Ingredient(text, "1", ""));
                        }
                    }
                    
                    runOnUiThread(this::updateUI);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Image labeling failed: " + e.getMessage());
                    runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(this, "Failed to analyze image", Toast.LENGTH_SHORT).show();
                    });
                });
    }
    
    private boolean isLikelyIngredient(String label) {
        // Common food-related terms that might appear in image labels
        String[] foodTerms = {"food", "fruit", "vegetable", "meat", "chicken", "beef", "pork", 
            "fish", "rice", "pasta", "bread", "cheese", "egg", "milk", "soup", "salad", "sauce", 
            "spice", "herb", "nut", "bean", "grain", "dairy", "dessert", "cake", "cookie"};
        
        // Check if the label contains any food-related terms
        for (String term : foodTerms) {
            if (label.contains(term)) {
                return true;
            }
        }
        return false;
    }
    
    private void updateUI() {
        showProgress(false);
        if (detectedIngredients.isEmpty()) {
            // Show the results UI even when no ingredients are detected
            showResultsUI();
            Toast.makeText(this, "No ingredients detected. Try a clearer image.", 
                Toast.LENGTH_SHORT).show();
        } else {
            // Show the results with detected ingredients
            showResultsUI();
            ingredientsAdapter.submitList(new ArrayList<>(detectedIngredients));
        }
    }
    
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        selectImageButton.setEnabled(!show);
    }
    
    private void generateRecipe() {
        if (detectedIngredients.isEmpty()) {
            Toast.makeText(this, "No ingredients detected", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show progress
        showProgress(true);
        generateRecipeButton.setEnabled(false);
        
        // Use Kotlin coroutines to call the suspend function
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Call the recipe generator with the detected ingredients
                Result<Recipe> result = recipeGenerator.generateRecipe(detectedIngredients);
                
                if (result instanceof Result.Success) {
                    // Success - navigate to the recipe editor with the generated recipe
                    Recipe generatedRecipe = ((Result.Success<Recipe>) result).getValue();
                    Intent intent = new Intent(ImageIngredientsActivity.this, AddEditRecipeActivity.class);
                    intent.putExtra(AddEditRecipeActivity.EXTRA_RECIPE, generatedRecipe);
                    startActivity(intent);
                } else if (result instanceof Result.Failure) {
                    // Handle failure
                    Exception exception = ((Result.Failure) result).exception;
                    Log.e(TAG, "Failed to generate recipe: " + exception.getMessage(), exception);
                    Toast.makeText(ImageIngredientsActivity.this, 
                        "Failed to generate recipe: " + exception.getMessage(), 
                        Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error generating recipe: " + e.getMessage(), e);
                Toast.makeText(ImageIngredientsActivity.this, 
                    "Error: " + e.getMessage(), 
                    Toast.LENGTH_LONG).show();
            } finally {
                // Always hide progress and re-enable the button
                showProgress(false);
                generateRecipeButton.setEnabled(true);
            }
        };
        
        // Show a toast to indicate that recipe generation has started
        Toast.makeText(this, "Generating recipe... This may take a moment.", Toast.LENGTH_SHORT).show();
    }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) 
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (allPermissionsGranted()) {
                openGallery();
            } else {
                Toast.makeText(this,
                        "Storage permission is required to select images. Please enable it in app settings.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
