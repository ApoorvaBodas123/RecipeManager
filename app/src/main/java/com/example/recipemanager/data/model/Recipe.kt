package com.example.recipemanager.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val instructions: String = "",
    val prepTime: Int = 0,
    val cookTime: Int = 0,
    val difficulty: String = "",
    val imageUrl: String = "",
    val isFavorite: Boolean = false
) : Parcelable
