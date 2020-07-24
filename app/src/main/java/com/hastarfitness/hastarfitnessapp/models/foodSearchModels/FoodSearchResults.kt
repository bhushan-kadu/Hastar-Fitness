package com.hastarfitness.hastarfitnessapp.models.foodSearchModels

data class FoodSearchResults(
    val currentPage: Int,
    val foodSearchCriteria: FoodSearchCriteria,
    val foods: List<Food>,
    val totalHits: Int,
    val totalPages: Int
)