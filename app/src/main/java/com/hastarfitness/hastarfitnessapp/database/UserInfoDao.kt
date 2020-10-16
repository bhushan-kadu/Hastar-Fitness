package com.hastarfitness.hastarfitnessapp.database

import androidx.room.*
import java.util.*

@Dao
interface UserInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTodaysWeight(weight:UserDailyWeightDataDbModel):Long

    @Query("select * from UserDailyWeightDataDbModel")
    fun getAllWeights():List<UserDailyWeightDataDbModel>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertUserTodayData(todayData:UserDailyDataDbModel):Long

    @Update
    fun updateUserTodayData(todayData:UserDailyDataDbModel):Int

    @Query("select * from UserDailyDataDbModel where date = :dateToday")
    fun getTodaysUserData(dateToday: Date):UserDailyDataDbModel


    @Query("select * from UserDailyDataDbModel")
    fun getTotalsUserData():List<UserDailyDataDbModel>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertCustomFood(todayData:CustomFoodsDbModel):Long

    @Query("select * from CustomFoodsDbModel")
    fun getAllCustomFood():List<CustomFoodsDbModel>

    @Query("select * from QuotesDbModel where id = :dayNo")
    fun getQuoteByDayNo(dayNo:Int):QuotesDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllFoodNutrient(foodNutrientList:List<FoodNutrientDbModel>):List<Long>

    @Query("select * from FoodNutrientDbModel where fdcId = :foodId")
    fun getSingleFoodNutrientsById(foodId:Int):List<FoodNutrientDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecentlySearchedFood(foodNutrientList:LastSearchedFoods):Long

    @Query("select * from LastSearchedFoods")
    fun getAllLastSearchedFoods():List<LastSearchedFoods>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserFoodConsumedData(userFoodConsumedDataDbModel: UserFoodConsumedDataDbModel):Long

    @Query("select * from UserFoodConsumedDataDbModel where date = :date")
    fun getAllFoodConsumedByDate(date:Date):List<UserFoodConsumedDataDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateStepForTheDate(stepCount: StepCountModel):Long

}
