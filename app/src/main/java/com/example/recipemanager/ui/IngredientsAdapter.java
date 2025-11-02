package com.example.recipemanager.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipemanager.R;
import com.example.recipemanager.data.model.Ingredient;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class IngredientsAdapter extends ListAdapter<Ingredient, IngredientsAdapter.IngredientViewHolder> {

    public IngredientsAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ingredient = getItem(position);
        holder.bind(ingredient);
    }

    @Override
    public void submitList(List<Ingredient> list) {
        super.submitList(list != null ? new ArrayList<>(list) : null);
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final Chip chip;

        IngredientViewHolder(View itemView) {
            super(itemView);
            chip = (Chip) itemView;
        }

        void bind(Ingredient ingredient) {
            // Capitalize first letter of ingredient name
            String name = ingredient.getName();
            if (name != null && !name.isEmpty()) {
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
            }
            chip.setText(name);
        }
    }

    private static final DiffUtil.ItemCallback<Ingredient> DIFF_CALLBACK = new DiffUtil.ItemCallback<Ingredient>() {
        @Override
        public boolean areItemsTheSame(@NonNull Ingredient oldItem, @NonNull Ingredient newItem) {
            return oldItem.getName().equals(newItem.getName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Ingredient oldItem, @NonNull Ingredient newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                   oldItem.getAmount().equals(newItem.getAmount()) &&
                   oldItem.getUnit().equals(newItem.getUnit());
        }
    };
}
