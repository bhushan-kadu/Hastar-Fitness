package com.hastarfitness.hastarfitnessapp.models.foodSearchModels

data class FoodSearchCriteria(
    val generalSearchInput: String,
    val pageNumber: Int,
    val query: String,
    val requireAllWords: Boolean
)