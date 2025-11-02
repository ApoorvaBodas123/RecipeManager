package com.example.recipemanager.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipemanager.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SelectedImagesAdapter extends RecyclerView.Adapter<SelectedImagesAdapter.ImageViewHolder> {
    private static final String TAG = "SelectedImagesAdapter";
    private List<Uri> imageUris = new ArrayList<>();
    private Context context;

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_selected_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri imageUri = imageUris.get(position);
        try {
            // Try to load the image using Glide first
            Glide.with(context)
                    .load(imageUri)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_ingredient)
                    .centerCrop()
                    .into(holder.imageView);
        } catch (SecurityException e) {
            Log.e(TAG, "Security Exception loading image: " + e.getMessage());
            // Fallback to loading bitmap directly
            try {
                ContentResolver resolver = context.getContentResolver();
                // Request the permission again if needed
                context.grantUriPermission(context.getPackageName(), imageUri, 
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
                
                try (InputStream inputStream = resolver.openInputStream(imageUri)) {
                    if (inputStream != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        holder.imageView.setImageBitmap(bitmap);
                    }
                } catch (FileNotFoundException fnfe) {
                    Log.e(TAG, "File not found: " + imageUri, fnfe);
                    holder.imageView.setImageResource(R.drawable.ic_ingredient);
                }
            } catch (Exception ex) {
                Log.e(TAG, "Error loading image: " + ex.getMessage(), ex);
                holder.imageView.setImageResource(R.drawable.ic_ingredient);
            }
        }
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public void updateImages(List<Uri> newImageUris) {
        this.imageUris = new ArrayList<>(newImageUris);
        notifyDataSetChanged();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
