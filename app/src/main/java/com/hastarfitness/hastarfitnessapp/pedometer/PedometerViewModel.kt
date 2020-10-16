package com.hastarfitness.hastarfitnessapp.pedometer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.database.StepCountModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PedometerViewModel:ViewModel() {
    fun insertOrUpdateStepForTheDate(db:AppDatabase, stepCount: StepCountModel){
        viewModelScope.launch(Dispatchers.Default) {

            db.userInfoDao().insertOrUpdateStepForTheDate(stepCount)

        }
    }
}