package com.hastarfitness.hastarfitnessapp.models

internal class User private constructor() {


    var full_name: String? = null
        private set

    var email: String? = null
        private set

    var gender: String? = null
        private set

    var dob: String? = null
        private set

    var weight_kg: String? = null
        private set

    var goalWeight_kg: String? = null
        private set

    var height_cm: String? = null
        private set

    var weeklyActivity: String? = null
        private set

    var DietPreference: String? = null//los, gain, maintain
    private set

    var weeklyDietGoal: String? = null
        private set

    var created_at: String? = null
        private set

    var updated_at: String? = null
        private set



    constructor(full_name: String?, email: String?,
                gender: String?, dob: String?, weight: String?,
                goalWeight: String?, height: String?, weeklyActivity: String?,
                DietPreference: String?,  weeklyDietGoal: String?,   createdAt: String?,   updatedAt: String?) : this() {
        this.full_name = full_name
        this.height_cm = height
        this.email = email
        this.gender = gender
        this.dob = dob
        this.weight_kg = weight
        this.goalWeight_kg = goalWeight
        this.weeklyActivity = weeklyActivity
        this.DietPreference = DietPreference
        this.weeklyDietGoal = weeklyDietGoal
        this.created_at = createdAt
        this.updated_at = updatedAt
    }

}//loss/gain per day eg.0.5,0.75,1kg..etc

