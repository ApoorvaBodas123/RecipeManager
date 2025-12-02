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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModel;

import com.example.recipemanager.R;
import com.example.recipemanager.data.model.Ingredient;
import com.example.recipemanager.data.RecipeRepository;
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
import com.example.recipemanager.data.model.Recipe;
import kotlin.Result;
import kotlin.ResultKt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageIngredientsActivity extends AppCompatActivity {
    private static final String TAG = "ImageIngredients";
    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final int MAX_IMAGES = 5; // Maximum number of images to process
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

    /**
     * Checks if all required permissions are granted.
     * @return true if all permissions are granted, false otherwise
     */
    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private View progressBar;
    private MaterialCardView detectedIngredientsCard;
    private RecyclerView ingredientsList;
    private ExecutorService executorService;
    private MaterialButton selectImageButton;
    private RecipeViewModel viewModel;
    private RecipeRepository recipeRepository;
    private RecyclerView selectedImagesRecyclerView;
    private List<Ingredient> detectedIngredients = new ArrayList<>();
    private List<Uri> selectedImageUris = new ArrayList<>();
    private SelectedImagesAdapter selectedImagesAdapter;
    private IngredientsAdapter ingredientsAdapter;
    private int currentImageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_ingredients);

        
        // Initialize RecipeRepository and ViewModel
        recipeRepository = RecipeRepository.getInstance(getApplication());
        ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(RecipeViewModel.class)) {
                    return (T) new RecipeViewModel(recipeRepository);
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        };
        viewModel = new ViewModelProvider(this, factory).get(RecipeViewModel.class);

        progressBar = findViewById(R.id.progressBar);
        detectedIngredientsCard = findViewById(R.id.detectedIngredientsCard);
        ingredientsList = findViewById(R.id.ingredientsList);
        selectImageButton = findViewById(R.id.selectImageButton);
        selectedImagesRecyclerView = findViewById(R.id.selectedImagesRecyclerView);
        selectedImagesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        selectedImagesAdapter = new SelectedImagesAdapter();
        selectedImagesRecyclerView.setAdapter(selectedImagesAdapter);
        
        // Set click listener for the select image button
        selectImageButton.setOnClickListener(v -> {
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

        // Set up click listener for the select image button
        selectImageButton.setOnClickListener(v -> {
            if (allPermissionsGranted()) {
                openGallery();
            } else {
                requestStoragePermission();
            }
        });
        

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
            // First try with ACTION_GET_CONTENT which is more reliable for multiple selection
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            
            // Create a chooser to let the user pick which app to use
            Intent chooser = Intent.createChooser(intent, "Select images");
            startActivityForResult(chooser, REQUEST_IMAGE_PICK);
        } catch (Exception e) {
            Log.e(TAG, "Error opening gallery: " + e.getMessage());
            try {
                // Fallback to OPEN_DOCUMENT if GET_CONTENT fails
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | 
                              Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivityForResult(intent, REQUEST_IMAGE_PICK);
            } catch (Exception ex) {
                Log.e(TAG, "Error with fallback gallery intent: " + ex.getMessage());
                runOnUiThread(() -> 
                    Toast.makeText(this, "Error opening gallery: " + ex.getMessage(), 
                                 Toast.LENGTH_LONG).show()
                );
            }
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
            if (selectedImagesRecyclerView != null) {
                selectedImagesRecyclerView.setVisibility(View.GONE);
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
            if (selectedImagesRecyclerView != null) {
                selectedImagesRecyclerView.setVisibility(View.VISIBLE);
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
            if (selectedImagesRecyclerView != null) {
                selectedImagesRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUris.clear();
            
            try {
                if (data.getClipData() != null) {
                    // Multiple images selected
                    int count = Math.min(data.getClipData().getItemCount(), MAX_IMAGES);
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        if (imageUri != null) {
                            // Take persistable URI permission
                            getContentResolver().takePersistableUriPermission(
                                imageUri, 
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );
                            selectedImageUris.add(imageUri);
                        }
                    }
                } else if (data.getData() != null) {
                    // Single image selected
                    Uri imageUri = data.getData();
                    // Take persistable URI permission
                    getContentResolver().takePersistableUriPermission(
                        imageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );
                    selectedImageUris.add(imageUri);
                }
                
                if (!selectedImageUris.isEmpty()) {
                    selectedImagesAdapter.updateImages(selectedImageUris);
                    processSelectedImages();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing selected images: " + e.getMessage(), e);
                runOnUiThread(() -> 
                    Toast.makeText(this, "Error processing images: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }
    }

    private void updateSelectedImagesPreview() {
        if (selectedImagesAdapter != null) {
            selectedImagesAdapter.updateImages(selectedImageUris);
        }
    }

    private void processSelectedImages() {
        if (selectedImageUris.isEmpty()) {
            return;
        }

        showProcessingUI();
        detectedIngredients.clear();
        currentImageIndex = 0;
        
        // Show the selected images RecyclerView
        selectedImagesRecyclerView.setVisibility(View.VISIBLE);
        
        // Process each image one by one
        processNextImage(currentImageIndex);
    }

    private void processNextImage(int index) {
        if (index >= selectedImageUris.size()) {
            // All images processed, show results
            runOnUiThread(this::showResultsUI);
            return;
        }

        // Update current index
        currentImageIndex = index;
        
        // Show which image is being processed
        updateStatusText("Processing image " + (index + 1) + " of " + selectedImageUris.size());
        
        // Process the current image
        Uri imageUri = selectedImageUris.get(index);
        processSelectedImage(imageUri);
    }

    private void processSelectedImage(Uri imageUri) {
        try {
            // Load the image as a bitmap
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            
            // Convert to InputImage for ML Kit
            InputImage image = InputImage.fromBitmap(bitmap, 0);
            
            // Analyze the image
            analyzeImage(image, currentImageIndex == selectedImageUris.size() - 1);
        } catch (IOException e) {
            Log.e(TAG, "Failed to load image: " + e.getMessage());
            runOnUiThread(() -> {
                if (currentImageIndex == 0) {
                    showSelectImageUI();
                }
                Toast.makeText(this, 
                    "Failed to load image " + (currentImageIndex) + ": " + e.getMessage(), 
                    Toast.LENGTH_LONG).show();
                // Continue with next image even if one fails
                processNextImage(currentImageIndex);
            });
        } catch (Exception e) {
            Log.e(TAG, "Error analyzing image: " + e.getMessage());
            runOnUiThread(() -> {
                if (currentImageIndex == 0) {
                    showSelectImageUI();
                }
                Toast.makeText(ImageIngredientsActivity.this, 
                    "Error analyzing image " + (currentImageIndex) + ": " + e.getMessage(), 
                    Toast.LENGTH_LONG).show();
                // Continue with next image even if one fails
                processNextImage(currentImageIndex);
            });
        }
    }

    private void analyzeImage(InputImage image, boolean isLastImage) {
        runOnUiThread(() -> showProgress(true));
        
        // Lower confidence threshold to catch more potential ingredients
        ImageLabelerOptions options = new ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.4f)
            .build();
            
        ImageLabeling.getClient(options)
            .process(image)
            .addOnSuccessListener(labels -> {
                Log.d(TAG, "Processing image " + (currentImageIndex + 1) + " of " + selectedImageUris.size());
                
                for (ImageLabel label : labels) {
                    String text = label.getText().toLowerCase();
                    float confidence = label.getConfidence();
                    boolean isIngredient = isLikelyIngredient(text);
                    
                    Log.d(TAG, String.format("Label: '%s' (confidence: %.2f, ingredient: %b)", 
                        text, confidence, isIngredient));
                    
                    if (confidence > 0.4f && isIngredient) {
                        // Format the ingredient name (capitalize first letter)
                        String formattedName = text.substring(0, 1).toUpperCase() + 
                                            text.substring(1);
                        
                        // Check if we already have this ingredient
                        boolean exists = false;
                        for (Ingredient existing : detectedIngredients) {
                            if (existing.getName().equalsIgnoreCase(formattedName)) {
                                exists = true;
                                break;
                            }
                        }
                        
                        if (!exists) {
                            Log.i(TAG, "Adding ingredient: " + formattedName);
                            // Create a new Ingredient with default values for other fields
                            Ingredient newIngredient = new Ingredient(
                                "", // id will be generated later
                                formattedName,
                                "", // amount
                                ""  // unit
                            );
                            detectedIngredients.add(newIngredient);
                        }
                    }
                }
                
                // Process next image or show results
                if (currentImageIndex < selectedImageUris.size() - 1) {
                    // Process next image
                    processNextImage(currentImageIndex + 1);
                } else {
                    // All images processed
                    Log.i(TAG, "Finished processing all images. Total ingredients: " + detectedIngredients.size());
                    runOnUiThread(() -> {
                        // Update UI on main thread
                        updateUI();
                        showResultsUI();
                    });
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error analyzing image: " + e.getMessage());
                // Continue with next image even if one fails
                if (currentImageIndex < selectedImageUris.size() - 1) {
                    processNextImage(currentImageIndex + 1);
                } else {
                    runOnUiThread(() -> {
                        updateUI();
                        showResultsUI();
                    });
                }
            });
    }
    
    private boolean isLikelyIngredient(String label) {
        if (label == null || label.trim().isEmpty()) {
            return false;
        }
        
        // Convert to lowercase for case-insensitive comparison
        String lowerLabel = label.toLowerCase().trim();
        
        // Common non-ingredient labels to exclude
        String[] nonIngredients = {
            "food", "dish", "meal", "cuisine", "cooking", "plate", "bowl",
            "lunch", "dinner", "breakfast", "snack", "tableware", "cutlery",
            "utensil", "container", "bottle", "glass", "cup", "mug", "pan",
            "pot", "frying pan", "cutting board", "kitchen", "appliance",
            "appliances", "beverage", "drink", "cuisine", "dish"
        };
        
        // Check if the label is in our non-ingredient list
        for (String nonIng : nonIngredients) {
            if (lowerLabel.equals(nonIng)) {
                return false;
            }
        }
        
        // Check for specific patterns that indicate non-ingredients
        if (lowerLabel.matches(".*\\d.*") || // contains numbers
            lowerLabel.matches(".*[^a-zA-Z ].*") || // contains non-letter characters (except spaces)
            lowerLabel.length() > 30) { // too long to be a simple ingredient
            return false;
        }
        
        // Check confidence threshold (handled by the caller)
        
        // If we got here, it's likely an ingredient
        return true;
    }
    
    private void updateUI() {
        runOnUiThread(() -> {
            try {
                Log.d(TAG, "Updating UI with " + detectedIngredients.size() + " ingredients");
                
                // Update the ingredients list
                if (ingredientsAdapter != null) {
                    ingredientsAdapter.setIngredients(detectedIngredients);
                } else {
                    Log.e(TAG, "ingredientsAdapter is null!");
                }
                
                // Show/hide the "No ingredients detected" message
                if (detectedIngredientsCard != null) {
                    detectedIngredientsCard.setVisibility(
                        detectedIngredients.isEmpty() ? View.GONE : View.VISIBLE);
                } else {
                    Log.e(TAG, "detectedIngredientsCard is null!");
                }
                
                
                // Force a redraw of the RecyclerView
                if (ingredientsList != null) {
                    ingredientsList.getAdapter().notifyDataSetChanged();
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error updating UI: " + e.getMessage(), e);
            }
        });
    }
    
    private void showProgress(boolean show) {
        runOnUiThread(() -> {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            selectImageButton.setEnabled(!show);
        });
    }
    
    private void updateStatusText(String message) {
        runOnUiThread(() -> {
            if (progressBar != null) {
                // No need to set indeterminate or progress for CircularProgressIndicator
                // It's already set up in the XML
            }
            // If you have a TextView to show the status, you can set the text here
            // statusTextView.setText(message);
        });
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
