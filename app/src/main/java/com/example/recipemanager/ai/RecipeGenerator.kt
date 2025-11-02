package com.example.recipemanager.ai

import com.example.recipemanager.data.model.Ingredient
import com.example.recipemanager.data.model.Recipe
import kotlinx.coroutines.*
import kotlin.random.Random

class RecipeGenerator {

    interface Callback {
        fun onSuccess(recipe: Recipe)
        fun onError(exception: Exception)
    }

    private val recipeTemplates = listOf(
        RecipeTemplate(
            "Stir-fry",
            "A quick and easy stir-fry with your ingredients",
            listOf("1 tbsp oil", "2 cloves garlic, minced", "1 tbsp soy sauce"),
            listOf("Heat oil in a pan over medium-high heat.",
                  "Add garlic and stir for 30 seconds until fragrant.",
                  "Add your ingredients and stir-fry for 5-7 minutes.",
                  "Add soy sauce and any other seasonings.",
                  "Serve hot over rice or noodles."),
            5, 10, "Easy"
        ),
        RecipeTemplate(
            "Omelette",
            "A fluffy omelette with your ingredients",
            listOf("3 eggs", "1 tbsp butter", "Salt and pepper to taste"),
            listOf("Whisk eggs in a bowl with salt and pepper.",
                  "Melt butter in a non-stick pan over medium heat.",
                  "Pour in the eggs and let them set slightly.",
                  "Add your ingredients on one half of the omelette.",
                  "Fold the other half over and cook for another minute.",
                  "Serve hot with toast."),
            5, 5, "Easy"
        ),
        RecipeTemplate(
            "Pasta",
            "A simple pasta dish with your ingredients",
            listOf("200g pasta", "2 tbsp olive oil", "2 cloves garlic, minced", "Salt and pepper to taste"),
            listOf("Cook pasta according to package instructions.",
                  "Heat olive oil in a pan and sauté garlic until fragrant.",
                  "Add your ingredients and cook for 3-4 minutes.",
                  "Drain pasta and add to the pan.",
                  "Toss everything together and season to taste.",
                  "Serve with grated cheese if desired."),
            5, 15, "Easy"
        )
    )

    fun generateRecipe(ingredients: List<Ingredient>, callback: Callback) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Simulate network delay
                delay(1000)
                
                val recipe = createLocalRecipe(ingredients)
                withContext(Dispatchers.Main) {
                    callback.onSuccess(recipe)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onError(e)
                }
            }
        }
    }

    private fun createLocalRecipe(ingredients: List<Ingredient>): Recipe {
        val template = recipeTemplates.random()
        val ingredientList = mutableListOf<Ingredient>()
        
        // Add template ingredients
        template.ingredients.forEach { ing ->
            val parts = ing.split(" ", limit = 2)
            ingredientList.add(
                Ingredient(
                    id = "",
                    name = parts.getOrElse(1) { ing },
                    amount = parts.firstOrNull() ?: "",
                    unit = ""
                )
            )
        }
        
        // Add user's ingredients
        ingredients.forEach { ingredient ->
            if (ingredientList.none { it.name.equals(ingredient.name, ignoreCase = true) }) {
                ingredientList.add(ingredient)
            }
        }
        
        return Recipe(
            id = "",
            title = "${ingredients.firstOrNull()?.name ?: "Delicious"} ${template.name}",
            description = template.description,
            ingredients = ingredientList,
            instructions = template.instructions.joinToString("\n"),
            prepTime = template.prepTime,
            cookTime = template.cookTime,
            difficulty = template.difficulty,
            isFavorite = false,
            imageUrl = ""
        )
    }
    
    private data class RecipeTemplate(
        val name: String,
        val description: String,
        val ingredients: List<String>,
        val instructions: List<String>,
        val prepTime: Int,
        val cookTime: Int,
        val difficulty: String
    )
}
