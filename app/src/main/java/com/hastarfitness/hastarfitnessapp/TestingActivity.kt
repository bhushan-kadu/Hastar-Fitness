package com.hastarfitness.hastarfitnessapp

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log.v
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.database.StepCountModel
import com.hastarfitness.hastarfitnessapp.pedometer.MyPedometerService
import com.hastarfitness.hastarfitnessapp.pedometer.PedometerViewModel
import com.hastarfitness.hastarfitnessapp.pedometer.StepTakenMessage
import kotlinx.android.synthetic.main.activity_testingactivity.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


class TestingActivity : AppCompatActivity(), SensorEventListener2 {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testingactivity)


        startService(Intent(this, MyPedometerService::class.java))

        val sm: SensorManager = this.getSystemService(SENSOR_SERVICE) as SensorManager
        val sensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
//        sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI, 0)
//        v("steps", "sensor registered")
    }

    lateinit var db:AppDatabase
    lateinit var viewModel: PedometerViewModel

    fun initialize(){

       instantiateDb()
        //setup ViewModel
        viewModel = ViewModelProvider(this).get(PedometerViewModel::class.java)

    }

    private fun instantiateDb() {
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "HasterDb.db")
                .build()
    }

    override fun onSensorChanged(p0: SensorEvent?) {
//        v("steps", p0!!.values[0].toString())
        val sensorStepsValue = p0!!.values[0]

        val spf = getSharedPreferences("pedometer", Context.MODE_PRIVATE)

        val dateToday = Calendar.getInstance()[Calendar.DATE]
        val lastSaveDate = spf.getInt("last_saved_date", dateToday)

        if(dateToday > lastSaveDate){
            spf.edit().putInt("my_last_saved_step_value", 0).apply()
        }

        var offset = spf.getInt("offset", sensorStepsValue.toInt())

        if(sensorStepsValue < offset){
            spf.edit().putInt("offset", 0).apply()
            offset = 0
        }
        val stepsToBeAdded = sensorStepsValue - offset
        spf.edit().putInt("offset", sensorStepsValue.toInt()).apply()

        val myLastSavedSteps = spf.getInt("my_last_saved_step_value", 0)

        val stepsTobeSavedToPref = myLastSavedSteps + stepsToBeAdded.toInt()
        spf.edit().putInt("my_last_saved_step_value", stepsTobeSavedToPref).apply()
        spf.edit().putInt("last_saved_date", dateToday).apply()


//        v("steps", stepsTobeSavedToPref.toString())


    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onFlushCompleted(p0: Sensor?) {
    }

    private fun getTodaysDate(): Date {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        return today.time
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: StepTakenMessage?) {
        val steps = event!!.todaysSteps
        v("steps", steps.toString())
        step_count.text = steps.toString()

        viewModel.insertOrUpdateStepForTheDate(db, StepCountModel(getTodaysDate(), steps))

    }

    override fun onResume() {
        super.onResume()
        val spf = getSharedPreferences("pedometer", Context.MODE_PRIVATE)
        step_count.text = spf.getInt("my_last_saved_step_value", 0).toString()
    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}
