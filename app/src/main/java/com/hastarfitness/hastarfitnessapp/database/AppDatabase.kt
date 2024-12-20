package com.hastarfitness.hastarfitnessapp.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * this class defines schema to be used for app's Database
 *
 * @author Bhushan Kadu
 */
@Database(entities = [ExerciseDbModel::class,
    RestTimeModel::class,
    YogaExerciseDbModel::class,
    MeditationDbModelNew::class,
    PlanExercisesDbModel::class,
    WorkoutPlansDbModel::class,
    FinalCardioExercisesDbModel::class,
    FinalBodyWeightExercisesDbModel::class,
    UserDailyDataDbModel::class,
    UserDailyWeightDataDbModel::class,
    UserTodayDataDbModel::class,
    CustomFoodsDbModel::class,
    FoodNutrientDbModel::class,
    LastSearchedFoods::class,
    UserFoodConsumedDataDbModel::class,
    QuotesDbModel::class,
    StepCountModel::class,
    PedometerDataModel::class],
        views = [ExerciseView::class],
        version = 2,
    autoMigrations = [
        AutoMigration (from = 1, to = 2 )
    ])
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun userInfoDao(): UserInfoDao
}