package com.hastarfitness.hastarfitnessapp


import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import com.hastarfitness.hastarfitnessapp.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ViewModel : ViewModel() {
  val exercises = MutableLiveData<List<ExerciseDbModel>>()
  val yogaExercises = MutableLiveData<List<YogaExerciseDbModel>>()
  val yogaTypes = MutableLiveData<List<String>>()
  val meditations = MutableLiveData<List<MeditationDbModelNew>>()
  val exercise = MutableLiveData<ExerciseDbModel>()
  val restTime = MutableLiveData<RestTimeModel>()
  val singleBodyWeightPlan = MutableLiveData<WorkoutPlansDbModel>()
  val singleCardioPlan = MutableLiveData<WorkoutPlansDbModel>()
  val fullPlanBodyWeight = MutableLiveData<List<ExerciseView>>()
  val fullPlanCardio = MutableLiveData<List<ExerciseView>>()
  val plansList = MutableLiveData<List<WorkoutPlansDbModel>>()
  val insertedRowInt = MutableLiveData<Int>()
  val deletedRowInt = MutableLiveData<Int>()
  val insertedRowLong = MutableLiveData<Long>()
  val insertedRowsList = MutableLiveData<List<Long>>()
  val insertedRowsListInt = MutableLiveData<List<Int>>()
  val deletedRowsListInt = MutableLiveData<List<Int>>()
  val insertedRowsListBodyWeight = MutableLiveData<List<Long>>()
  val insertedRowsListCardio = MutableLiveData<List<Long>>()
  val deletedRows = MutableLiveData<Int>()
  val deletedRowsBodyWeight = MutableLiveData<Int>()
  val deletedRowsCardio = MutableLiveData<Int>()
  val finalBodyWeightExercisesDbModelList = MutableLiveData<List<FinalBodyWeightExercisesDbModel>>()
  val finalCardioExercisesDbModelList = MutableLiveData<List<FinalCardioExercisesDbModel>>()
  val db = MutableLiveData<AppDatabase>()


  fun createDatabase(context: Context){
    viewModelScope.launch(Dispatchers.Default) {
      val dbTemp = Room.databaseBuilder(context, AppDatabase::class.java, "HasterDb.db")
              .createFromAsset("databases/HasterDb.db")
              .build()
      db.postValue(dbTemp)
    }
  }
  fun setup(db: AppDatabase, setupParam: String, intensity: String?) {
    viewModelScope.launch(Dispatchers.Default) {
      if (intensity != null) {
        exercises.postValue(db.exerciseDao().getByType(setupParam, intensity))
      } else {
        exercises.postValue(db.exerciseDao().getByWorkoutType(setupParam))
      }
    }
  }

  fun getAllExercisesByType(db: AppDatabase, type: String) {
    viewModelScope.launch(Dispatchers.Default) {
      exercises.postValue(db.exerciseDao().getAllExercisesByType(type))
    }
  }

  fun getYogaExercises(db: AppDatabase, setupParam: String, intensity: String?) {
    viewModelScope.launch(Dispatchers.Default) {
      if (intensity != null) {
        yogaExercises.postValue(db.exerciseDao().getYogaExerByType(setupParam, intensity))
      }
    }
  }

  fun getYogaTypes(db: AppDatabase) {
    viewModelScope.launch(Dispatchers.Default) {
      yogaTypes.postValue(db.exerciseDao().getYogaTypes())

    }
  }

  fun getMeditations(db: AppDatabase, setupParam: String) {
    viewModelScope.launch(Dispatchers.Default) {

      meditations.postValue(db.exerciseDao().getMeditationByType(setupParam))
    }
  }


  fun getAll(db: AppDatabase) {
    viewModelScope.launch(Dispatchers.Default) {

      exercises.postValue(db.exerciseDao().getAll())
    }
  }

  fun getRest(db: AppDatabase, type: String, intensity: String) {
    viewModelScope.launch(Dispatchers.Default) {

      restTime.postValue(db.exerciseDao().getRestByType(type, intensity))
      //type, intensity

    }
  }

  fun updateRest(db: AppDatabase, restTimeModel: RestTimeModel){
    viewModelScope.launch(Dispatchers.Default) {
      insertedRowInt.postValue(db.exerciseDao().updateRest(restTimeModel.restTime, restTimeModel.intensity, restTimeModel.type))
    }
  }

  fun getSinglePlanByName(db: AppDatabase, planName: String, isBodyWeight:Boolean) {
    viewModelScope.launch(Dispatchers.Default) {
      if(isBodyWeight) {
        singleBodyWeightPlan.postValue(db.exerciseDao().getSinglePlanByName(planName))
      }else{
        singleCardioPlan.postValue(db.exerciseDao().getSinglePlanByName(planName))
      }
    }
  }

  fun getSinglePlanById(db: AppDatabase, planId: Int) {
    viewModelScope.launch(Dispatchers.Default) {
      singleBodyWeightPlan.postValue(db.exerciseDao().getSinglePlanById(planId))
    }
  }

  fun getPlan(db: AppDatabase, planId: Int, isBodyWeight: Boolean) {
    viewModelScope.launch(Dispatchers.Default) {
      if(isBodyWeight) {
        fullPlanBodyWeight.postValue(db.exerciseDao().getPlan(planId))
      }else{
        fullPlanCardio.postValue(db.exerciseDao().getPlan(planId))

      }
    }
  }

  fun getAllPlansByType(db: AppDatabase, type: String) {
    viewModelScope.launch(Dispatchers.Default) {
      plansList.postValue(db.exerciseDao().getAllPlansByType(type))
    }
  }

  fun getAllPlans(db: AppDatabase) {
    viewModelScope.launch(Dispatchers.Default) {
      plansList.postValue(db.exerciseDao().getAllPlans())
    }
  }

  fun getAllAppSavedPlansByType(db: AppDatabase, type: String) {
    viewModelScope.launch(Dispatchers.Default) {
      plansList.postValue(db.exerciseDao().getAllAppSavedPlansByType(type))
    }
  }

  fun getAllUserPlansByType(db: AppDatabase, type: String) {
    viewModelScope.launch(Dispatchers.Default) {
      plansList.postValue(db.exerciseDao().getAllUserPlansByType(type))
    }
  }

  fun getAllUserPlans(db: AppDatabase) {
    viewModelScope.launch(Dispatchers.Default) {
      plansList.postValue(db.exerciseDao().getAllUserPlans())
    }
  }

  fun getAllFavPlans(db: AppDatabase) {
    viewModelScope.launch(Dispatchers.Default) {
      plansList.postValue(db.exerciseDao().getAllFavPlans())
    }
  }

  val insertedRecId = MutableLiveData<Long>()
  fun insertCustomUserPlan(db: AppDatabase, workoutPlansDbModel: WorkoutPlansDbModel) {
    viewModelScope.launch(Dispatchers.Default) {
      val id = db.exerciseDao().insertCustomPlanMappings(workoutPlansDbModel)
      insertedRecId.postValue(id)
    }
  }

  fun insertCustomUserPlanExercises(db: AppDatabase, planExercisesDbModelList: List<PlanExercisesDbModel>) {
    viewModelScope.launch(Dispatchers.Default) {
      insertedRowsList.postValue(db.exerciseDao().insertCustomPlanExercises(planExercisesDbModelList))
    }
  }

  fun togglePlanFavById(db: AppDatabase, planId: Int, isFav: Int) {
    viewModelScope.launch(Dispatchers.Default) {
      insertedRowInt.postValue(db.exerciseDao().makePlanFavById(planId, isFav))
    }
  }

  fun insertFinalExercisesBodyWeight(db: AppDatabase, exerciseDbModel: List<ExerciseDbModel>) {
    viewModelScope.launch(Dispatchers.Default) {
      val finalExercisesDbModelList = exerciseDbModel.map {
        FinalBodyWeightExercisesDbModel(0,
                it.name,
                it.type,
                it.desc,
                it.img,
                it.intensity,
                it.time,
                it.fmet,
                it.mmet)
      }
      insertedRowsListBodyWeight.postValue(db.exerciseDao().insertFinalExercisesBodyWeight(finalExercisesDbModelList))
    }
  }

  fun insertCustomPlanMappings(db: AppDatabase, planExerciseDbModel: List<PlanExercisesDbModel>) {
    viewModelScope.launch(Dispatchers.Default) {
      insertedRowsListBodyWeight.postValue(db.exerciseDao().insertCustomPlanMappings(planExerciseDbModel))
    }
  }
  fun deletePlanById(db: AppDatabase, planId: Int) {
    viewModelScope.launch(Dispatchers.Default) {
      deletedRowInt.postValue(db.exerciseDao().deletePlanById(planId))
    }
  }
  fun deletePlan(db: AppDatabase, workoutPlansDbModel: WorkoutPlansDbModel) {
    viewModelScope.launch(Dispatchers.Default) {
      deletedRowInt.postValue(db.exerciseDao().deletePlan(workoutPlansDbModel))
    }
  }

  var planExercisesMappings = MutableLiveData<List<PlanExercisesDbModel>>()
  fun getPlanMappingsById(db: AppDatabase, planId: Int) {
    viewModelScope.launch(Dispatchers.Default) {
      planExercisesMappings.postValue(db.exerciseDao().getPlanMappingsById(planId))
    }
  }
  fun deletePlanMappings(db: AppDatabase, planExerciseDbModel: List<PlanExercisesDbModel>) {
    viewModelScope.launch(Dispatchers.Default) {
      deletedRowInt.postValue(db.exerciseDao().deletePlanMappings(planExerciseDbModel))
    }
  }

  fun insertFinalExercisesCardio(db: AppDatabase, exerciseDbModel: List<ExerciseDbModel>) {
    viewModelScope.launch(Dispatchers.Default) {
      val finalExercisesDbModelList = exerciseDbModel.map {
        FinalCardioExercisesDbModel(0,
                it.name,
                it.type,
                it.desc,
                it.img,
                it.intensity,
                it.time,
                it.fmet,
                it.mmet)
      }
      insertedRowsListCardio.postValue(db.exerciseDao().insertFinalExercisesCardio(finalExercisesDbModelList))
    }
  }

  fun deleteExercisesFromFinalBodyWeightExTable(db: AppDatabase) {
    viewModelScope.launch(Dispatchers.Default) {
      deletedRowsBodyWeight.postValue(db.exerciseDao().deleteExercisesFromFinalBodyWeightExTable())
    }
  }

  fun deleteExercisesFromFinalCardioExTable(db: AppDatabase) {
    viewModelScope.launch(Dispatchers.Default) {
      deletedRowsCardio.postValue(db.exerciseDao().deleteExercisesFromFinalCardioExTable())
    }
  }


  fun selectAllFromFinalBodyWeightExerciseDbModel(db: AppDatabase) {
    viewModelScope.launch(Dispatchers.Default) {
      finalBodyWeightExercisesDbModelList.postValue(db.exerciseDao().selectAllFromFinalBodyWeightExerciseDbModel())
    }
  }
  fun selectAllFromFinalCardioExerciseDbModel(db: AppDatabase) {
    viewModelScope.launch(Dispatchers.Default) {
      finalCardioExercisesDbModelList.postValue(db.exerciseDao().selectAllFromFinalCardioExerciseDbModel())
    }
  }

  fun selectExercisesFromFinalCardioExerciseDbModelByType(db: AppDatabase) {
    viewModelScope.launch(Dispatchers.Default) {
      finalCardioExercisesDbModelList.postValue(db.exerciseDao().selectExercisesFromFinalCardioExerciseDbModelByType())
    }
  }

  fun selectExercisesFromFinalBodyWeightExerciseDbModelByType(db: AppDatabase) {
    viewModelScope.launch(Dispatchers.Default) {
      finalBodyWeightExercisesDbModelList.postValue(db.exerciseDao().selectExercisesFromFinalBodyWeightExerciseDbModelByType())
    }
  }

  fun saveCustomizationIn(db: AppDatabase, workoutType: String) {
    viewModelScope.launch(Dispatchers.Default) {
      finalCardioExercisesDbModelList.postValue(db.exerciseDao().selectExercisesFromFinalCardioExerciseDbModelByType())
    }
  }

  fun insertWeightWithDate(db: AppDatabase, weight: Double, date: Date) {
    viewModelScope.launch(Dispatchers.Default) {
      val calInstance = Calendar.getInstance()
      calInstance.time = date
      calInstance.set(Calendar.HOUR_OF_DAY, 0)
      calInstance.set(Calendar.MINUTE, 0)
      calInstance.set(Calendar.SECOND, 0)
      calInstance.set(Calendar.MILLISECOND, 0)
      val dateTobeInserted: Date = calInstance.time
      insertedRowLong.postValue(db.userInfoDao().insertTodaysWeight(UserDailyWeightDataDbModel(dateTobeInserted, weight)))
    }
  }

  val weightInfoDbModel = MutableLiveData<List<UserDailyWeightDataDbModel>>()

  fun getAllWeights(db: AppDatabase) {
    viewModelScope.launch(Dispatchers.Default) {
      weightInfoDbModel.postValue(db.userInfoDao().getAllWeights())
    }
  }

  fun insertUserTodayData(db: AppDatabase, userTodayData: UserDailyDataDbModel) {
    viewModelScope.launch(Dispatchers.Default) {
      insertedRowLong.postValue(db.userInfoDao().insertUserTodayData(userTodayData))
    }
  }

  fun updateUserTodayData(db: AppDatabase, userTodayData: UserDailyDataDbModel) {
    viewModelScope.launch(Dispatchers.Default) {
      insertedRowInt.postValue(db.userInfoDao().updateUserTodayData(userTodayData))
    }
  }

  val todayData = MutableLiveData<UserDailyDataDbModel>()
  fun getTodaysUserData(db: AppDatabase) {
    viewModelScope.launch(Dispatchers.Default) {
      val today = Calendar.getInstance()
      today.set(Calendar.HOUR_OF_DAY, 0)
      today.set(Calendar.MINUTE, 0)
      today.set(Calendar.SECOND, 0)
      today.set(Calendar.MILLISECOND, 0)
      try {

        todayData.postValue(db.userInfoDao().getTodaysUserData(today.time))
      }catch (e:Exception){

      }
    }
  }

  val totalUserData = MutableLiveData<List<UserDailyDataDbModel>>()
  fun getTotalsUserData(db: AppDatabase) {
    viewModelScope.launch(Dispatchers.Default) {

      totalUserData.postValue(db.userInfoDao().getTotalsUserData())
    }
  }

  fun insertCustomFood(db: AppDatabase, food:CustomFoodsDbModel) {
    viewModelScope.launch(Dispatchers.Default) {

      insertedRowLong.postValue(db.userInfoDao().insertCustomFood(food))
    }
  }

  val allCustomAddedFoods = MutableLiveData<List<CustomFoodsDbModel>>()
  fun getAllCustomAddedFoods(db: AppDatabase) {
    viewModelScope.launch(Dispatchers.Default) {

      allCustomAddedFoods.postValue(db.userInfoDao().getAllCustomFood())
    }
  }

  fun createViewExerciseView(db: AppDatabase) {
    viewModelScope.launch(Dispatchers.Default) {

//      db.exerciseDao().dropExerciseView(SimpleSQLiteQuery("drop VIEW ExerciseView"))
      db.exerciseDao().createViewExerciseView(SimpleSQLiteQuery("CREATE VIEW IF NOT EXISTS ExerciseView AS select * from PlanExercisesDbModel join ExerciseDbModel on PlanExercisesDbModel.exerciseId = ExerciseDbModel.id"))

    }
  }

  val steps = MutableLiveData<PedometerDataModel>();
  fun getSteps(db: AppDatabase) {
    viewModelScope.launch(Dispatchers.Default) {

      steps.postValue(db.exerciseDao().getSteps())

    }
  }

  val quote = MutableLiveData<QuotesDbModel>()
  fun getQuoteByDayNo(db: AppDatabase, dayNo:Int) {
    viewModelScope.launch(Dispatchers.Default) {
      quote.postValue(db.userInfoDao().getQuoteByDayNo(dayNo))
    }
  }



}