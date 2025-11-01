package com.example.recipemanager.ui;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipemanager.R;
import com.example.recipemanager.data.Recipe;
import com.example.recipemanager.databinding.ItemRecipeBinding;

public class RecipeAdapter extends ListAdapter<Recipe, RecipeAdapter.VH> {

    public interface OnItemClickListener { void onClick(Recipe recipe); }

    private final OnItemClickListener listener;

    public RecipeAdapter(OnItemClickListener listener) {
        super(DIFF);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Recipe> DIFF = new DiffUtil.ItemCallback<Recipe>() {
        @Override
        public boolean areItemsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
            return oldItem.name.equals(newItem.name)
                    && oldItem.ingredients.equals(newItem.ingredients)
                    && oldItem.steps.equals(newItem.steps)
                    && ((oldItem.imageUri == null && newItem.imageUri == null) || (oldItem.imageUri != null && oldItem.imageUri.equals(newItem.imageUri)))
                    && ((oldItem.category == null && newItem.category == null) || (oldItem.category != null && oldItem.category.equals(newItem.category)))
                    && oldItem.favorite == newItem.favorite;
        }
    };

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemRecipeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Recipe r = getItem(position);
        holder.bind(r, listener);
    }

    static class VH extends RecyclerView.ViewHolder {
        private final ItemRecipeBinding binding;
        
        VH(ItemRecipeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        
        void bind(Recipe recipe, OnItemClickListener listener) {
            binding.recipeName.setText(recipe.name);
            
            // Set category if available
            if (recipe.category != null && !recipe.category.isEmpty()) {
                binding.recipeCategory.setText(recipe.category);
                binding.recipeCategory.setVisibility(View.VISIBLE);
            } else {
                binding.recipeCategory.setVisibility(View.GONE);
            }
            
            // Load image with Glide
            if (recipe.imageUri != null && !recipe.imageUri.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(Uri.parse(recipe.imageUri))
                        .centerCrop()
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_placeholder)
                        .into(binding.recipeImage);
            } else {
                binding.recipeImage.setImageResource(R.drawable.ic_placeholder);
            }
            
            // Set favorite state
            binding.favoriteButton.setImageResource(
                recipe.favorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border
            );
            
            // Set click listeners
            binding.getRoot().setOnClickListener(v -> listener.onClick(recipe));
            binding.favoriteButton.setOnClickListener(v -> {
                // Toggle favorite state
                recipe.favorite = !recipe.favorite;
                binding.favoriteButton.setImageResource(
                    recipe.favorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border
                );
            });
        }
    }
}
