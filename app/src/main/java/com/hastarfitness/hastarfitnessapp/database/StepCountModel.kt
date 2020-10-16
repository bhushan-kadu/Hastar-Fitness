package com.hastarfitness.hastarfitnessapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
@Entity
data class StepCountModel(
        @PrimaryKey
        @ColumnInfo val date: Date,
        @ColumnInfo val steps: Int)