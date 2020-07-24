package com.hastarfitness.hastarfitnessapp.models.singleFood

data class SingleFood(
    val availableDate: String,
    val brandOwner: String,
    val brandedFoodCategory: String,
    val dataSource: String,
    val dataType: String,
    val description: String,
    val fdcId: Int,
    val foodAttributes: List<Any>,
    val foodClass: String,
    val foodComponents: List<Any>,
    val foodNutrients: List<FoodNutrient>,
    val foodPortions: List<Any>,
    val gtinUpc: String,
    val householdServingFullText: String,
    val ingredients: String,
    val labelNutrients: LabelNutrients,
    val marketCountry: String,
    val modifiedDate: String,
    val publicationDate: String,
    val servingSize: Double,
    val servingSizeUnit: String
)