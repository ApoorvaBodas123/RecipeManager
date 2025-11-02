package com.example.recipemanager.ai

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.recipemanager.data.model.Ingredient
import com.example.recipemanager.data.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeGenerator(private val openAIKey: String) {

    @OptIn(BetaOpenAI::class)
    suspend fun generateRecipe(ingredients: List<Ingredient>): Result<Recipe> = withContext(Dispatchers.IO) {
        return@withContext try {
            val openAI = OpenAI(openAIKey)
            
            // Format ingredients list
            val ingredientsList = ingredients.joinToString(", ") { it.name }
            
            // Create the prompt for the AI
            val prompt = """
                Create a detailed recipe using these ingredients: $ingredientsList
                
                Please provide the response in the following JSON format:
                {
                    "title": "Recipe Title",
                    "description": "A brief description of the recipe",
                    "ingredients": [
                        {"name": "ingredient 1", "amount": "1 cup"},
                        ...
                    ],
                    "instructions": [
                        "Step 1: ...",
                        "Step 2: ...",
                        ...
                    ],
                    "prepTime": 10,
                    "cookTime": 20,
                    "difficulty": "Easy/Medium/Hard"
                }
                
                Make sure to include all the original ingredients in the recipe and add any common pantry staples as needed.
                The recipe should be delicious and well-balanced.
            """.trimIndent()

            val chatCompletionRequest = ChatCompletionRequest(
                model = ModelId("gpt-4"),
                messages = listOf(
                    ChatMessage(
                        role = ChatRole.System,
                        content = "You are a professional chef creating detailed and delicious recipes."
                    ),
                    ChatMessage(
                        role = ChatRole.User,
                        content = prompt
                    )
                ),
                temperature = 0.7
            )

            val completion = openAI.chatCompletion(chatCompletionRequest)
            val response = completion.choices.first().message?.content
            
            if (response != null) {
                // Parse the JSON response and create a Recipe object
                // Note: You'll need to implement proper JSON parsing based on the response format
                val recipe = parseRecipeFromJson(response, ingredients)
                Result.success(recipe)
            } else {
                Result.failure(Exception("No response from AI"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun parseRecipeFromJson(json: String, originalIngredients: List<Ingredient>): Recipe {
        // This is a simplified version - you should use a proper JSON parser like Gson
        // or Kotlin's built-in JSON parsing
        
        // For now, we'll create a simple recipe with the original ingredients
        return Recipe(
            title = "Generated Recipe with ${originalIngredients.joinToString(", ") { it.name }}",
            description = "A delicious recipe created just for you!",
            instructions = "1. Prepare all ingredients.\n2. Combine and cook as desired.",
            prepTime = 10,
            ingredients = ArrayList(originalIngredients),
            difficulty = "Medium"
        )
    }
}
