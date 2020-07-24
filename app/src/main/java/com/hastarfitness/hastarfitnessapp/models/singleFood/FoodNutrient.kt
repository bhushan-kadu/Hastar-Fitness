package com.hastarfitness.hastarfitnessapp.models.singleFood

data class FoodNutrient(
    val amount: Double,
    val foodNutrientDerivation: FoodNutrientDerivation,
    val id: Int,
    val nutrient: Nutrient,
    val type: String
)