package com.example.recipemanager.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ingredient(
    val id: String = "",
    val name: String = "",
    val amount: String = "",
    val unit: String = ""
) : Parcelable
