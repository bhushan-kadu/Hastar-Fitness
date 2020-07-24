package com.hastarfitness.hastarfitnessapp.repository

import com.hastarfitness.hastarfitnessapp.models.foodSearchModels.FoodSearchResults
import com.hastarfitness.hastarfitnessapp.models.singleFood.SingleFood
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodApi {

    @GET("/fdc/v1/foods/search")
    suspend fun fetchFoodSearch(@Query("api_key") apiKey: String,
                                @Query("query") query: String,
                                @Query("pageSize") pageSize: Int,
                                @Query("dataType") dataType: String
    ): FoodSearchResults

    @GET("/fdc/v1/food/{food_id}")
    suspend fun fetchFoodDetails(@Path("food_id") foodId:Int,
                                 @Query("api_key") apiKey: String,
                                 @Query("format") format: String
    ): SingleFood
}