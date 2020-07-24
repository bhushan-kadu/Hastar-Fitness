package com.hastarfitness.hastarfitnessapp.database

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * model as well as view in Db for extracting exercise data from PlanExerciseDbModel
 */
@DatabaseView("select * from PlanExercisesDbModel join ExerciseDbModel on" +
        " PlanExercisesDbModel.exerciseId = ExerciseDbModel.id")

data class ExerciseView(

        @SerializedName("id")
        @PrimaryKey(autoGenerate = true) val id: Int,

        @SerializedName("exercise_id")
        @ColumnInfo val exerciseId: Int,

        @SerializedName("plan_id")
        @ColumnInfo val planId: Int,

        @SerializedName("name_of_exercise")
        @ColumnInfo val name: String,

        @SerializedName("desc")
        @ColumnInfo val desc: String,

        @SerializedName("type_of_exercise")
        @ColumnInfo val type: String,

        @SerializedName("intensity")
        @ColumnInfo val intensity: String,

        @SerializedName("intensity")
        @ColumnInfo val time: Int,

        @SerializedName("intensity")
        @ColumnInfo val mmet: Double,

        @SerializedName("intensity")
        @ColumnInfo val fmet: Double




)