package com.hastarfitness.hastarfitnessapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PedometerDataModel (

    @PrimaryKey val id: Int,

    @ColumnInfo
    val steps: Int
)