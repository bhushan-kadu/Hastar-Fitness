package com.hastarfitness.hastarfitnessapp.diet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hastarfitness.hastarfitnessapp.Internet
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.database.FoodNutrientDbModel
import com.hastarfitness.hastarfitnessapp.database.LastSearchedFoods
import com.hastarfitness.hastarfitnessapp.database.UserFoodConsumedDataDbModel
import com.hastarfitness.hastarfitnessapp.models.foodSearchModels.FoodSearchResults
import com.hastarfitness.hastarfitnessapp.models.singleFood.SingleFood
import com.hastarfitness.hastarfitnessapp.repository.FoodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class DietViewModel:ViewModel(){
    val foodSearches = MutableLiveData<FoodSearchResults>()
    fun setup(query:String){
        viewModelScope.launch(Dispatchers.Default) {
            foodSearches.postValue(FoodRepository().fetchAllSearchList(query, 5))
        }
    }
    val isInternetConnected = MutableLiveData<Boolean>()
    fun checkIfInternetConnected(){
        viewModelScope.launch(Dispatchers.Default) {
            isInternetConnected.postValue(Internet().isInternetAvailable())
        }
    }
    val NewSingleFood = MutableLiveData<SingleFood>()
    fun fetchFoodDetails(foodId:Int){
        viewModelScope.launch(Dispatchers.Default) {
            NewSingleFood.postValue(FoodRepository().fetchFoodDetails(foodId))
        }
    }

    val insertedRowLong = MutableLiveData<Long>()
    val insertedRowLongList = MutableLiveData<List<Long>>()
    fun insertAllFoodNutrient(db:AppDatabase, foodNutrientDbModel: List<FoodNutrientDbModel>){
        viewModelScope.launch(Dispatchers.Default) {
            insertedRowLongList.postValue(db.userInfoDao().insertAllFoodNutrient(foodNutrientDbModel))
        }
    }

    val foodNutrientDbModel = MutableLiveData<List<FoodNutrientDbModel>>()
    fun getFoodNutrientsById(db:AppDatabase, foodId: Int){
        viewModelScope.launch(Dispatchers.Default) {
            foodNutrientDbModel.postValue(db.userInfoDao().getSingleFoodNutrientsById(foodId))
        }
    }

    fun insertLastSearchedFood(db:AppDatabase, lastSearchedFoods: LastSearchedFoods){
        viewModelScope.launch(Dispatchers.Default) {
            insertedRowLong.postValue(db.userInfoDao().insertRecentlySearchedFood(lastSearchedFoods))
        }
    }

    val lastSearchedFoodsList = MutableLiveData<List<LastSearchedFoods>>()
    fun getAllLastSearchedFoods(db:AppDatabase){
        viewModelScope.launch(Dispatchers.Default) {
            lastSearchedFoodsList.postValue(db.userInfoDao().getAllLastSearchedFoods())
        }
    }

    fun insertUserFoodConsumedData(db:AppDatabase, userFoodConsumedDataDbModel: UserFoodConsumedDataDbModel){
        viewModelScope.launch(Dispatchers.Default) {
            insertedRowLong.postValue(db.userInfoDao().insertUserFoodConsumedData(userFoodConsumedDataDbModel))
        }
    }
    val consumedFoods = MutableLiveData<List<UserFoodConsumedDataDbModel>>()
    fun getAllFoodConsumedByDate(db:AppDatabase, date: Date){
        viewModelScope.launch(Dispatchers.Default) {
            consumedFoods.postValue(db.userInfoDao().getAllFoodConsumedByDate(date))
        }
    }


}