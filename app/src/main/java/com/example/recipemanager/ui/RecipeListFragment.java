package com.example.recipemanager.ui;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.recipemanager.data.Recipe;
import com.example.recipemanager.databinding.FragmentRecipeListBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

public class RecipeListFragment extends Fragment {

    private FragmentRecipeListBinding binding;
    private RecipeViewModel vm;
    private RecipeAdapter adapter;
    private Recipe lastDeleted;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipeListBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Get application context
        Application application = requireActivity().getApplication();
        
        // Create ViewModel using ViewModelFactory with application context
        ViewModelFactory factory = new ViewModelFactory(application);
        vm = new ViewModelProvider(requireActivity(), factory).get(RecipeViewModel.class);
        
        // Setup FABs
        binding.fabAddRecipe.setOnClickListener(v -> startActivity(new Intent(requireContext(), AddEditRecipeActivity.class)));
        
        binding.fabImageRecognition.setOnClickListener(v -> checkPermissionsAndStartImageRecognition());
        
        adapter = new RecipeAdapter(recipe -> {
            Intent i = new Intent(requireContext(), RecipeDetailActivity.class);
            i.putExtra("id", recipe.id);
            startActivity(i);
        });
        binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recycler.setAdapter(adapter);

        vm.getRecipes().observe(getViewLifecycleOwner(), list -> adapter.submitList(list));

        binding.chipAll.setOnClickListener(v -> vm.setCategory(null));
        binding.chipDessert.setOnClickListener(v -> vm.setCategory("Dessert"));
        binding.chipSnack.setOnClickListener(v -> vm.setCategory("Snack"));
        binding.chipMain.setOnClickListener(v -> vm.setCategory("Main-Course"));
        binding.chipBeverage.setOnClickListener(v -> vm.setCategory("Beverage"));

        binding.switchFav.setOnCheckedChangeListener((buttonView, isChecked) -> vm.setFavoritesOnly(isChecked));

        // Long-press to delete with undo
        binding.recycler.addOnItemTouchListener(new SimpleRecyclerItemClickListener(requireContext(), new SimpleRecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view1, int position) {
                // No-op on tap, handled by adapter click
            }

            @Override
            public void onItemLongClick(View view12, int position) {
                if (position == RecyclerView.NO_POSITION) return;
                Recipe r = adapter.getCurrentList().get(position);
                lastDeleted = r;
                vm.delete(r);
                Snackbar.make(binding.getRoot(), "Deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> vm.save(lastDeleted))
                        .show();
            }
        }));
    }
    
    private void checkPermissionsAndStartImageRecognition() {
        try {
            // For emulator/desktop, we'll only request storage permission
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                requestPermissionsWithDexter(
                    Manifest.permission.READ_MEDIA_IMAGES
                );
            } else {
                requestPermissionsWithDexter(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                );
            }
        } catch (Exception e) {
            Log.e("PermissionError", "Error requesting permissions", e);
            Snackbar.make(binding.getRoot(), "Error requesting permissions: " + e.getMessage(), 
                Snackbar.LENGTH_LONG).show();
        }
    }
    
    private void requestPermissionsWithDexter(String... permissions) {
        Dexter.withContext(requireContext())
            .withPermissions(permissions)
            .withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted()) {
                        // All permissions granted, start the image recognition activity
                        startActivity(new Intent(requireContext(), ImageIngredientsActivity.class));
                    } else if (report.isAnyPermissionPermanentlyDenied()) {
                        // Show dialog explaining that permission was permanently denied
                        showPermissionDeniedDialog();
                    } else {
                        // Some permissions were denied but not permanently
                        Snackbar.make(binding.getRoot(), 
                            "Permission denied. The feature requires camera and storage access.", 
                            Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    // Show rationale and ask again
                    showPermissionRationale(token);
                }
            })
            .withErrorListener(error -> {
                Log.e("DexterError", "Error requesting permissions: " + error.name());
                Snackbar.make(binding.getRoot(), 
                    "Error requesting permissions. Please try again.", 
                    Snackbar.LENGTH_LONG).show();
            })
            .check();
    }

    // Removed handlePermissionResult as it's now handled in requestPermissionsWithDexter

    private void showPermissionRationale(PermissionToken token) {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Permissions Required")
            .setMessage("This feature requires camera and storage permissions to work properly.")
            .setPositiveButton("Continue", (dialog, which) -> token.continuePermissionRequest())
            .setNegativeButton("Cancel", (dialog, which) -> token.cancelPermissionRequest())
            .setOnDismissListener(dialog -> token.cancelPermissionRequest())
            .show();
    }

    private void showPermissionDeniedDialog() {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Permissions Required")
            .setMessage("Camera and storage permissions are required to use image recognition. Please enable them in app settings.")
            .setPositiveButton("Open Settings", (dialog, which) -> openAppSettings())
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(com.example.recipemanager.R.menu.menu_search, menu);
        MenuItem item = menu.findItem(com.example.recipemanager.R.id.action_search);
        SearchView sv = (SearchView) item.getActionView();
        sv.setQueryHint("Search recipes");
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                vm.setQuery(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                vm.setQuery(newText);
                return true;
            }
        });
    }
}
