package com.hastarfitness.hastarfitnessapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * model as well as view in Db for Storing users daily data
 *
 * @author Bhushan Kadu
 */
@Entity
data class UserFoodConsumedDataDbModel(

        @SerializedName("id")
        @PrimaryKey(autoGenerate = true) val id: Int,

        @SerializedName("calories")
        @ColumnInfo val calories: Double,

        @SerializedName("protein")
        @ColumnInfo val protein: Double,

        @SerializedName("fats")
        @ColumnInfo val fats: Double,

        @SerializedName("carbs")
        @ColumnInfo val carbs: Double,

        @SerializedName("food_name")
        @ColumnInfo val foodName: String,

        @SerializedName("food_id")
        @ColumnInfo val fdcId: Int,

        @SerializedName("date")
        @ColumnInfo val date: Date,

        @SerializedName("meal_type")
        @ColumnInfo val mealType: String,

        @SerializedName("grams_comsumed")
        @ColumnInfo val grams: Double
)