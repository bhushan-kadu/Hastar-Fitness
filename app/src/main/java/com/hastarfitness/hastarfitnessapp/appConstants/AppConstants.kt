package com.hastarfitness.hastarfitnessapp.appConstants

import java.util.*

/**
 * This class saves all app constants needed for for running the app
 */
object AppConstants {
    const val BODY_WEIGHT = "body weight"
    const val CARDIO = "cardio"
    const val YOGA = "yoga"
    const val MEDITATION = "meditation"
    const val CORE_STRENGTH = "core Strength"
    const val UPPER_BODY = "upper body"
    const val LOWER_BODY = "lower body"
    const val FULL_BODY = "full body"
    const val BEGINNER = "beginner"
    const val INTERMEDIATE = "intermediate"
    const val ADVANCED = "advanced"
    const val HIIT = "hiit"
    const val LIGHT_CARDIO = "light cardio"
    const val PLYOMETRICS = "plyometrics"
    const val JOINT_FRIENDLY = "joint friendly"
    const val RESULT_OK = 5

    //intents extra name
    const val WORKOUT_TYPE = "workout_type"
    const val WORKOUT_SUB_TYPE = "workout sub_type"
    const val WORKOUT_PLAN_ID = "workout_plan_id"
    const val INTENSITY = "intensity"
    const val WORKOUT_TIME_IN_MINUTES = "workout_time_in_minutes"
    const val IS_CARDIO_ENABLED = "is_cardio_enabled"
    const val DAY = "day"
    const val IS_SUGGESTED_PLAN = "is_suggested_plan"
    const val REST_TIME_IN_SECONDS = "rest_time_in_seconds"
    const val REST_INTERVAL = "rest_interval"
    const val TOTAL_EXERCISE_SLOTS = "total_exercise_slots"
    const val EXERCISE_LIST = "exercise_list"
    const val IS_CUSTOMIZE = "isCustomize"
    const val MEDITATION_DATA = "meditation_data"
    const val MEDITATION_TYPE = "meditation_type"
    const val YOGA_EXERCISES = "yoga_exercises"
    const val YOGA_TYPE = "yoga_type"
    const val EXIT = "exit"
    const val FILTER_BY = "filter_by"
    const val MY_PLANS = "my_plans"
    const val FAVOURITE_PLANS = "favourite_plans"
    const val MALE = "Male"
    const val FEMALE = "Female"
    const val OTHER_GENDER = "other_gender"
    const val IS_USER_PLAN = "is_user_plan"
    const val IS_SHUFFLE_ENABLED = "is_shuffle_enabled"
    const val INCH = "in"
    const val CM = "cm"
    const val LB = "lb"
    const val KG = "kg"


    const val LITTLE_OR_NO_EXERCISE =  "little or no exercise"
    const val LIGHT_EXERCISE = "light exercise/sports 1-3 days/week"
    const val MODERATE_EXERCISE = "moderate exercise/sports 3-5 days/week"
    const val HARD_EXERCISE = "hard exercise/sports 6-7 days a week"
    const val VERY_HARD_EXERCISE = "very hard exercise/sports & physical job or 2x training"
    const val CHANNEL_ID = "my_channel_id"
    const val APP_WORKOUT_RESTART_NOTIFICATION = 69
    const val SWITCH_FRAGMENT = "switch_fragment"

    //meal types
    const val MEAL_TYPE = "meal_type"
    const val BREAKFAST = "breakfast"
    const val LUNCH = "lunch"
    const val DINNER = "dinner"
    const val SNACKS = "snacks"
    const val NUTRIENTS = "nutrients"
    const val FOOD_NAME = "food_name"
    const val FOOD_ID = "food_id"

    const val PROTEIN = "protein"
    const val CARBS = "carbohydrate"
    const val CALORIES = "calories"
    const val FAT = "fat"
    const val ENERGY = "energy"
    const val ENERGY_ID = 1008
    const val PROTEIN_ID = 1003
    const val CARBS_ID = 1005
    const val FAT_ID = 1004
    const val IS_CUSTOM_FOOD = "is_custom_food"
    const val MEASUREMENT = "measurement"
    const val SERVINGS_OR_GRAM_VALUE = "servings_or_gram_value"
    const val SERVINGS_MEASUREMENT = 0
    const val GRAM_MEASUREMENT = 1

    //diet

    const val WEIGHT_LOSS = "weight_loss"
    const val MAINTAIN_WEIGHT = "maintain_weight"
    const val GAIN_WEIGHT = "gain_weight"

    //diet preference
    const val GAIN_WEIGHT_250GM_PER_WEEK = "gain_weight_250gm_per_week"
    const val GAIN_WEIGHT_500GM_PER_WEEK = "gain_weight_500gm_per_week"
    const val GAIN_WEIGHT_750GM_PER_WEEK = "gain_weight_750gm_per_week"
    const val GAIN_WEIGHT_1000GM_PER_WEEK = "gain_weight_1000gm_per_week"

    const val LOSE_WEIGHT_250GM_PER_WEEK = "lose_weight_250gm_per_week"
    const val LOSE_WEIGHT_500GM_PER_WEEK = "lose_weight_500gm_per_week"
    const val LOSE_WEIGHT_750GM_PER_WEEK = "lose_weight_750gm_per_week"
    const val LOSE_WEIGHT_1000GM_PER_WEEK = "lose_weight_1000gm_per_week"

    const val BREAKFAST_CALORIES_PERCENTAGE = 30
    const val LUNCH_CALORIES_PERCENTAGE = 40
    const val SNACKS_CALORIES_PERCENTAGE = 6
    const val DINNER_CALORIES_PERCENTAGE = 24


    const val ALL_PLANS = "All Plans"








    val dailyPlanBodyWeight = LinkedHashMap<String, String>()
    val dailyPlanCardio = LinkedHashMap<String, String>()
    init {
        dailyPlanBodyWeight["monday"] = AppConstants.FULL_BODY
        dailyPlanBodyWeight["tuesday"] = AppConstants.CORE_STRENGTH
        dailyPlanBodyWeight["wednesday"] = AppConstants.UPPER_BODY
        dailyPlanBodyWeight["thursday"] = AppConstants.LOWER_BODY
        dailyPlanBodyWeight["friday"] = AppConstants.FULL_BODY
        dailyPlanBodyWeight["saturday"] = AppConstants.UPPER_BODY
        dailyPlanBodyWeight["sunday"] = AppConstants.CORE_STRENGTH

        dailyPlanCardio["monday"] = AppConstants.LIGHT_CARDIO
        dailyPlanCardio["tuesday"] = AppConstants.HIIT
        dailyPlanCardio["wednesday"] = AppConstants.PLYOMETRICS
        dailyPlanCardio["thursday"] = AppConstants.HIIT
        dailyPlanCardio["friday"] = AppConstants.JOINT_FRIENDLY
        dailyPlanCardio["saturday"] = AppConstants.HIIT
        dailyPlanCardio["sunday"] = AppConstants.HIIT
    }
}