package com.hastarfitness.hastarfitnessapp.repository

import com.google.gson.GsonBuilder
import com.hastarfitness.hastarfitnessapp.models.foodSearchModels.FoodSearchResults
import com.hastarfitness.hastarfitnessapp.models.singleFood.SingleFood
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FoodRepository(){

    private val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

    val client : OkHttpClient = OkHttpClient.Builder().apply {
        this.addInterceptor(interceptor)
    }.build()

    private fun retrofit(): FoodApi{
        return Retrofit.Builder()
                .baseUrl("https://api.nal.usda.gov")
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .client(client)
                .build()
                .create(FoodApi::class.java)
    }

    suspend fun fetchAllSearchList(query:String, pageSize:Int): FoodSearchResults {
        return retrofit().fetchFoodSearch(FoodApiKey().API_KEY, query, pageSize, "Branded")
    }

    suspend fun fetchFoodDetails(foodId:Int): SingleFood {
        return retrofit().fetchFoodDetails(foodId, FoodApiKey().API_KEY, "full")
    }
}