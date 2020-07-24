package com.hastarfitness.hastarfitnessapp.manageSharedPrefs

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.database.Converters
import java.util.*

/**
 * class for Managing sharedPreferences within app
 *  -you can set or get any shared pref listed here
 */

class Session(val activity: Activity) {

    private var prefs: SharedPreferences? = null

    init {
        prefs = activity.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    var areStartPagesShown: Boolean?
        get() = prefs!!.getBoolean("areStartPagesShown", false)
        set(areStartPagesShown) {
            prefs!!.edit().putBoolean("areStartPagesShown", areStartPagesShown!!).apply()
        }

    var userName: String?
        get() = prefs!!.getString("userName", "Guest User")
        set(userName) {
            prefs!!.edit().putString("userName", userName).apply()
        }

    var userEmail: String?
        get() = prefs!!.getString("userEmail", "")
        set(userEmail) {
            prefs!!.edit().putString("userEmail", userEmail).apply()
        }

    var photoUrl: String?
        get() = prefs!!.getString("photoUrl", "")
        set(photoUrl) {
            prefs!!.edit().putString("photoUrl", photoUrl).apply()
        }

    var type: String?
        get() = prefs!!.getString("type", "")
        set(type) {
            prefs!!.edit().putString("type", type).apply()
        }


    var intensity: String?
        get() = prefs!!.getString("intensity", AppConstants.BEGINNER)
        set(intensity) {
            prefs!!.edit().putString("intensity", intensity).apply()
        }

    var cardioSubType: String?
        get() = prefs!!.getString("cardioSubType", AppConstants.HIIT)
        set(cardioSubType) {
            prefs!!.edit().putString("cardioSubType", cardioSubType).apply()
        }

    val days = listOf<String>("sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday")
    var day: String?
        get() = prefs!!.getString("day", days[Calendar.getInstance()[Calendar.DAY_OF_WEEK] - 1])
        set(mobile) {
            prefs!!.edit().putString("day", mobile).apply()
        }

    var name: String?
        get() = prefs!!.getString("name", "")
        set(name) {
            prefs!!.edit().putString("name", name).apply()
        }

    var workoutSubType: String?
        get() = prefs!!.getString("workoutSubType", "")
        set(workoutSubType) {
            prefs!!.edit().putString("workoutSubType", workoutSubType).apply()
        }

    var isSuggestedPlan: Boolean?
        get() = prefs!!.getBoolean("isSuggestedPlan", true)
        set(isSuggestedPlan) {
            prefs!!.edit().putBoolean("isSuggestedPlan", isSuggestedPlan!!).apply()
        }

    var planId: Int?
        get() = prefs!!.getInt("planId", 0)
        set(planId) {
            prefs!!.edit().putInt("planId", planId!!).apply()
        }

    var totalWorkoutTime: Int?
        get() = prefs!!.getInt("totalWorkoutTime", 0)
        set(totalWorkoutTime) {
            prefs!!.edit().putInt("totalWorkoutTime", totalWorkoutTime!!).apply()
        }

    var finalIntensity: String?
        get() = prefs!!.getString("finalIntensity", "")
        set(finalIntensity) {
            prefs!!.edit().putString("finalIntensity", finalIntensity).apply()
        }

    var finalWorkoutType: String?
        get() = prefs!!.getString("finalWorkoutType", "")
        set(finalWorkoutType) {
            prefs!!.edit().putString("finalWorkoutType", finalWorkoutType).apply()
        }

    var isCardioEnabled: Boolean?
        get() = prefs!!.getBoolean("isCardioEnabled", true)
        set(isCardioEnabled) {
            prefs!!.edit().putBoolean("isCardioEnabled", isCardioEnabled!!).apply()
        }

    var todaysWorkoutType: String?
        get() = prefs!!.getString("todaysWorkoutType", "notSet")
        set(todaysWorkoutType) {
            prefs!!.edit().putString("todaysWorkoutType", todaysWorkoutType!!).apply()
        }

    //body data preferences

    var age: Int?
        get() = prefs!!.getInt("age", -1)
        set(age) {
            prefs!!.edit().putInt("age", age!!).apply()
        }

    var gender: String?
        get() = prefs!!.getString("gender", AppConstants.MALE)
        set(gender) {
            prefs!!.edit().putString("gender", gender).apply()
        }

    var weeklyActivity: String?
        get() = prefs!!.getString("weeklyActivity", "")
        set(weeklyActivity) {
            prefs!!.edit().putString("weeklyActivity", weeklyActivity).apply()
        }

    var weistInches: Double?
        get() = prefs!!.getString("weistInches", "-1")!!.toDouble()
        set(weistInches) {
            prefs!!.edit().putString("weistInches", weistInches.toString()).apply()
        }

    var goalWeight: Double?
        get() = prefs!!.getString("goalWeight", "-1")!!.toDouble()
        set(goalWeight) {
            prefs!!.edit().putString("goalWeight", goalWeight.toString()).apply()
        }

    var hipInches: Double?
        get() = prefs!!.getString("hipInches", "-1")!!.toDouble()
        set(hipInches) {
            prefs!!.edit().putString("hipInches", hipInches.toString()).apply()
        }

    var neakInches: Double?
        get() = prefs!!.getString("neakInches", "-1")!!.toDouble()
        set(neakInches) {
            prefs!!.edit().putString("neakInches", neakInches.toString()).apply()
        }

    var heightCm: Double?
        get() = prefs!!.getString("heightCm", "-1")!!.toDouble()
        set(heightCm) {
            prefs!!.edit().putString("heightCm", heightCm.toString()).apply()
        }

    var weightInKg: Double?
        get() = prefs!!.getString("weightInKg", "60")!!.toDouble()
        set(weightInKg) {
            prefs!!.edit().putString("weightInKg", weightInKg.toString()).apply()
        }

    var streakDate: Date?
        get() = Converters().fromTimestamp(prefs!!.getString("streakDate", null)?.toLong())
        set(streakDate) {
            prefs!!.edit().putString("streakDate", Converters().dateToTimestamp(streakDate).toString()).apply()
        }

    var streakNo: Int
        get() = prefs!!.getInt("streakNo", 0)
        set(streakNo) {
            prefs!!.edit().putInt("streakNo", streakNo).apply()
        }



    //day change
    var isDayChanged: Boolean?
        get() = prefs!!.getBoolean("isDayChanged", true)
        set(isDayChanged) {
            prefs!!.edit().putBoolean("isDayChanged", isDayChanged!!).apply()
        }



    //diet goal
    var goalCalories: Double?
        get() = prefs!!.getString("goalCalories", "-1")!!.toDouble()
        set(goalCalories) {
            prefs!!.edit().putString("goalCalories", goalCalories.toString()).apply()
        }

    var goalFat: Double?
        get() = prefs!!.getString("goalFat", "-1")!!.toDouble()
        set(goalFat) {
            prefs!!.edit().putString("goalFat", goalFat.toString()).apply()
        }

    var goalProtein: Double?
        get() = prefs!!.getString("goalProtein", "-1")!!.toDouble()
        set(goalProtein) {
            prefs!!.edit().putString("goalProtein", goalProtein.toString()).apply()
        }

    var goalCarbs: Double?
        get() = prefs!!.getString("goalCarbs", "-1")!!.toDouble()
        set(goalCarbs) {
            prefs!!.edit().putString("goalCarbs", goalProtein.toString()).apply()
        }

    var dietPreference: String?
        get() = prefs!!.getString("dietPreference", "")
        set(dietPreference) {
            prefs!!.edit().putString("dietPreference", dietPreference).apply()
        }
}
