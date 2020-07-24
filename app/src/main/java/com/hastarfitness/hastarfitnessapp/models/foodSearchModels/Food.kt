package com.hastarfitness.hastarfitnessapp.models.foodSearchModels

data class Food(
    val allHighlightFields: String,
    val brandOwner: String,
    val dataType: String,
    val description: String,
    val fdcId: Int,
    val foodNutrients: List<FoodNutrient>,
    val gtinUpc: String,
    val ingredients: String,
    val publishedDate: String,
    val score: Double
)