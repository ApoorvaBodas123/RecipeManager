package com.example.recipemanager.ui;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipemanager.data.Recipe;
import com.example.recipemanager.databinding.ItemRecipeBinding;
import com.squareup.picasso.Picasso;

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
        private final ItemRecipeBinding b;
        VH(ItemRecipeBinding b) {
            super(b.getRoot());
            this.b = b;
        }
        void bind(Recipe r, OnItemClickListener listener) {
            b.tvName.setText(r.name);
            if (r.imageUri != null && !r.imageUri.isEmpty()) {
                Picasso.get().load(Uri.parse(r.imageUri)).fit().centerCrop().into(b.ivPhoto);
            } else {
                b.ivPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
            }
            b.getRoot().setOnClickListener(v -> listener.onClick(r));
        }
    }
}
