package com.hastarfitness.hastarfitnessapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * model as well as view in Db for Storing users daily date
 *
 * @author Bhushan Kadu
 */
@Entity
data class UserDailyWeightDataDbModel(

        @SerializedName("day_no")
        @PrimaryKey(autoGenerate = true) val date: Date,

        @SerializedName("weight")
        @ColumnInfo val weight: Double
)
