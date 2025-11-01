package com.example.recipemanager.ui;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import com.example.recipemanager.data.Recipe;
import com.example.recipemanager.databinding.FragmentRecipeListBinding;
import com.google.android.material.snackbar.Snackbar;

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
