package com.hastarfitness.hastarfitnessapp.models.singleFood

data class FoodNutrientDerivation(
    val code: String,
    val description: String,
    val foodNutrientSource: FoodNutrientSource,
    val id: Int
)