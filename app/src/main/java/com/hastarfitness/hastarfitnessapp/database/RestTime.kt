package com.hastarfitness.hastarfitnessapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class RestTimeModel(

        @SerializedName("id")
        @PrimaryKey(autoGenerate = true) val id: Int,

        @SerializedName("type_of_exercise")
        @ColumnInfo val type: String,

        @SerializedName("intensity_type")
        @ColumnInfo val intensity: String,

        @SerializedName("number_of_ExerciseAfter")
        @ColumnInfo val numberOfExerciseAfter: Int,

        @SerializedName("rest_time")
        @ColumnInfo val restTime: Int
)

