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
data class UserTodayDataDbModel(

        @SerializedName("day_no")
        @PrimaryKey(autoGenerate = true) val date: Date,

        @SerializedName("calories")
        @ColumnInfo val calories: Double,

        @SerializedName("no_of_workout")
        @ColumnInfo val noOfWorkout: Int

)