package com.hastarfitness.hastarfitnessapp.database

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM ExerciseDbModel")
    suspend fun getAll(): List<ExerciseDbModel>


    @Query("SELECT * FROM ExerciseDbModel WHERE intensity like '%'||:type||'%'")
    fun getAllExercisesByType(type: String): List<ExerciseDbModel>

    @Query("SELECT * FROM ExerciseDbModel WHERE id = :uid")
    fun findById(uid:Int): ExerciseDbModel

    @Query("SELECT * FROM ExerciseDbModel WHERE type like :type and intensity like :intensity")
    fun getByType(type:String, intensity:String): List<ExerciseDbModel>

    @Query("SELECT * FROM ExerciseDbModel WHERE type like :type")
    fun getByWorkoutType(type:String): List<ExerciseDbModel>

    @Query("SELECT * FROM RestTimeModel WHERE lower(type) like :exerciseType and lower(intensity) like :exerciseIntensity")
    fun getRestByType(exerciseType:String, exerciseIntensity:String): RestTimeModel

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun updateRest(restTimeModel: RestTimeModel):Long

    @Query("update RestTimeModel set restTime = :restTime where intensity like :intensity and type like :workoutType")
    fun updateRest(restTime:Int, intensity: String, workoutType:String):Int

    @Query("SELECT * FROM YogaExerciseDbModel WHERE type like :type and intensity like :intensity")
    fun getYogaExerByType(type:String, intensity:String): List<YogaExerciseDbModel>

    @Query("SELECT * FROM MeditationDbModelNew WHERE lower(intensity) like :meditationType")
    fun getMeditationByType(meditationType:String): List<MeditationDbModelNew>

    @Query("SELECT DISTINCT intensity FROM YogaExerciseDbModel")
    fun getYogaTypes(): List<String>

//    @Query("create TEMP  VIEW Exerciseces  AS  select * from PlanExercisesDbModel join ExerciseDbModel on PlanExercisesDbModel.exerciseId = ExerciseDbModel.id  where PlanExercisesDbModel.planId = :planId ")
//    fun createPlanExerciseView(planId:Int)

    @Query("SELECT * FROM ExerciseView WHERE planId = :planId")
    fun getPlan(planId:Int): List<ExerciseView>

    @Query("SELECT * FROM WorkoutPlansDbModel WHERE lower(name) like lower(:planName)")
    suspend fun getSinglePlanByName(planName:String): WorkoutPlansDbModel

    @Query("SELECT * FROM WorkoutPlansDbModel WHERE id = :planId")
    suspend fun getSinglePlanById(planId:Int): WorkoutPlansDbModel

    @Query("SELECT * FROM WorkoutPlansDbModel WHERE type like lower(:type)")
    suspend fun getAllPlansByType(type:String): List<WorkoutPlansDbModel>

    @Query("SELECT * FROM WorkoutPlansDbModel WHERE isUserPlan = 0")
    suspend fun getAllPlans(): List<WorkoutPlansDbModel>

    @Query("SELECT * FROM WorkoutPlansDbModel WHERE type like lower(:type) and isUserPlan = 1")
    suspend fun getAllUserPlansByType(type:String): List<WorkoutPlansDbModel>

    @Query("SELECT * FROM WorkoutPlansDbModel WHERE (type like lower(:type) and isUserPlan = 1) or (type like lower(:type) and isUserPlan = 0)")
    suspend fun getAllAppSavedPlansByType(type:String): List<WorkoutPlansDbModel>


    @Query("SELECT * FROM WorkoutPlansDbModel where isUserPlan = 1")
    suspend fun getAllUserPlans(): List<WorkoutPlansDbModel>

    @Query("SELECT * FROM WorkoutPlansDbModel WHERE isFav = 1")
    suspend fun getAllFavPlans(): List<WorkoutPlansDbModel>

    @Insert
    suspend fun insertCustomPlanMappings(workoutPlansDbModel: WorkoutPlansDbModel):Long

    @Insert
    suspend fun insertCustomPlanExercises(planExercisesDbModel: List<PlanExercisesDbModel>):List<Long>

    @Query("update WorkoutPlansDbModel set isFav = :isFav WHERE id = :planId  ")
    suspend fun makePlanFavById(planId:Int, isFav:Int):Int

    @Query("delete from WorkoutPlansDbModel WHERE id = :planId  ")
    fun deletePlanById(planId: Int):Int
    @Delete
    fun deletePlan(workoutPlansDbModel: WorkoutPlansDbModel):Int

    @Delete
    fun deletePlanMappings(planExercisesDbModel: List<PlanExercisesDbModel>):Int

    @Insert
     fun insertFinalExercisesBodyWeight(finalBodyWeightExercisesDbModel: List<FinalBodyWeightExercisesDbModel>):List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertCustomPlanMappings(planExerciseDbModelList: List<PlanExercisesDbModel>):List<Long>


    @Insert
    fun insertFinalExercisesCardio(finalWorkoutExercisesDbModel: List<FinalCardioExercisesDbModel>):List<Long>

    @Query("select * from PlanExercisesDbModel where planId = :planId ")
    suspend fun getPlanMappingsById(planId: Int):List<PlanExercisesDbModel>

    @Query("delete from FinalBodyWeightExercisesDbModel ")
    suspend fun deleteExercisesFromFinalBodyWeightExTable():Int

    @Query("delete from FinalCardioExercisesDbModel ")
    suspend fun deleteExercisesFromFinalCardioExTable():Int

    @Query("select * from FinalBodyWeightExercisesDbModel ")
    fun selectAllFromFinalBodyWeightExerciseDbModel():List<FinalBodyWeightExercisesDbModel>


    @Query("select * from FinalCardioExercisesDbModel ")
    fun selectAllFromFinalCardioExerciseDbModel():List<FinalCardioExercisesDbModel>

    @Query("select * from FinalCardioExercisesDbModel ")
    fun selectExercisesFromFinalCardioExerciseDbModelByType():List<FinalCardioExercisesDbModel>

    @Query("select * from FinalBodyWeightExercisesDbModel ")
    fun selectExercisesFromFinalBodyWeightExerciseDbModelByType():List<FinalBodyWeightExercisesDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSteps(data: PedometerDataModel) :Long
    @Query("Select * from PedometerDataModel where id=1")
    fun getSteps():PedometerDataModel


    @RawQuery()
    fun createViewExerciseView(query: SupportSQLiteQuery):Any

    @RawQuery()
    fun dropExerciseView(query: SupportSQLiteQuery):Any














}
