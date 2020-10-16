package com.hastarfitness.hastarfitnessapp.pedometer

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import android.util.Log.v
import org.greenrobot.eventbus.EventBus
import java.util.*
import java.util.concurrent.TimeUnit


class MyPedometerService : Service(), SensorEventListener2 {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerSensorListener()

        return START_STICKY
    }

    private fun registerSensorListener(){
        val sm = getSystemService(SENSOR_SERVICE) as SensorManager
        try {
            sm.unregisterListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_UI, 0);
        v("steps", "sensor registered")
    }

    override fun onSensorChanged(p0: SensorEvent?) {

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


//        Log.v("steps", stepsTobeSavedToPref.toString())

        EventBus.getDefault().post(StepTakenMessage(todaysSteps = stepsTobeSavedToPref))
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onFlushCompleted(p0: Sensor?) {

    }
}
