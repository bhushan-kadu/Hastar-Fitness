package com.hastarfitness.hastarfitnessapp.exerciseListForCardioAndBodyWeight

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.database.ExerciseDbModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivityEcerciseListViewModel: ViewModel() {
    val exercises = MutableLiveData<List<ExerciseDbModel>>()
    val exercise = MutableLiveData<ExerciseDbModel>()


    fun setup( db: AppDatabase, setupParam:String, intensity:String?){
        viewModelScope.launch(Dispatchers.Default) {

            when(setupParam){
                "all" -> exercises.postValue(db.exerciseDao().getAll())
                else -> exercises.postValue(db.exerciseDao().getByType(setupParam, intensity!!))
            }
        }
    }

}