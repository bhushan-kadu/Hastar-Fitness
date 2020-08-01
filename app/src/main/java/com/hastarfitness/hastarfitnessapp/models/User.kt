package com.hastarfitness.hastarfitnessapp.models

data class User(val userId: String = "",
                val full_name: String = "",
                val email: String? = "",
                val gender: String= "",
                val dob: String= "",
                val weight: Double = 0.0,
                val goalWeight: Double = 0.0,
                val height: Double = 0.0,
                val weeklyActivity: String= "",
                val DietPreference: String= "",//los, gain, maintain
                val weeklyDietGoal: String= "")//loss/gain per day eg.0.5,0.75,1kg..etc
