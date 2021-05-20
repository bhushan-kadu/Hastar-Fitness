package com.hastarfitness.hastarfitnessapp.pedometer

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log.v
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.room.Room
import com.hastarfitness.hastarfitnessapp.ActivityDashboard
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.Util
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import org.greenrobot.eventbus.EventBus
import kotlin.math.min


class MyPedometerService : Service(), SensorEventListener2, LifecycleObserver {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    lateinit var db:AppDatabase
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerSensorListener()
        db = Room.databaseBuilder(this, AppDatabase::class.java, "HasterDb.db")
                .build()

        val nextUpdate = min(Util.tomorrow,
                System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR)

        val am = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        val pi = PendingIntent
                .getService(applicationContext, 2, Intent(this, MyPedometerService::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT >= 23) {
            API23Wrapper.setAlarmWhileIdle(am, AlarmManager.RTC, nextUpdate, pi)
        } else {
            am.set(AlarmManager.RTC, nextUpdate, pi)
        }


        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        return START_STICKY
    }
    @RequiresApi(Build.VERSION_CODES.M)
    object API23Wrapper {
        fun requestPermission(a: Activity, permissions: Array<String>) {
            a.requestPermissions(permissions, 42)
        }


        fun setAlarmWhileIdle(am: AlarmManager, type: Int, time: Long,
                              intent: PendingIntent?) {
            am.setAndAllowWhileIdle(type, time, intent)
        }
    }

    var isAppInBackground = false;
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        //App in background
        isAppInBackground = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        // App in foreground
        isAppInBackground = false
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

    lateinit var spf:SharedPreferences

    override fun onSensorChanged(p0: SensorEvent?) {

        val sensorStepsValue = p0!!.values[0]

        val spf = getSharedPreferences("pedometer", Context.MODE_PRIVATE)

        val dateToday = Util.today
        val lastSaveDate = spf.getLong("last_saved_date", dateToday)
        if(dateToday > lastSaveDate){
            spf.edit().putInt("my_last_saved_step_value", 0).apply()
        }

        var offset = spf.getInt("offset", sensorStepsValue.toInt())

        if(sensorStepsValue < offset){
            spf.edit().putInt("offset", 0).apply()
            offset = 0
        }
        val stepsToBeAdded = sensorStepsValue - offset
        v("steps1", sensorStepsValue.toString())
        spf.edit().putInt("offset", sensorStepsValue.toInt()).apply()

        val myLastSavedSteps = spf.getInt("my_last_saved_step_value", 0)

        val stepsTobeSavedToPref = myLastSavedSteps + stepsToBeAdded.toInt()
        spf.edit().putInt("my_last_saved_step_value", stepsTobeSavedToPref).apply()
        spf.edit().putLong("last_saved_date", dateToday).apply()

        spf.edit().putInt("steps_to_be_added",
                spf.getInt("steps_to_be_added", 0) + stepsToBeAdded.toInt())
                .apply()
        val goal = spf.getInt("steps_goal_everyday", 6000)
        startForeground(1, getNotification(goal, stepsTobeSavedToPref))

        if(!isAppInBackground){
            EventBus.getDefault().post(StepTakenMessage(todaysSteps = stepsTobeSavedToPref, stepsToBeAdded = stepsToBeAdded.toInt()))
        }
    }
    private fun getNotification(goal: Int, steps: Int):Notification{
        val pendingIntent: PendingIntent =
                Intent(this, ActivityDashboard::class.java).let { notificationIntent ->
                    PendingIntent.getActivity(this, 0, notificationIntent, 0)
                }
        val builder = NotificationCompat.Builder(this, THIS)
                .setSmallIcon(R.drawable.notification_logo)
                .setContentTitle("Hastar Fitness")
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentIntent(pendingIntent)
                .setShowWhen(false)
                .setOngoing(true)
        createNotificationChannel()

        builder.setProgress(goal, steps, false)
                .setContentText(if((goal - steps) > 0) "${goal-steps} steps to go" else "Goal of $goal steps completed!")
                .setContentTitle("$steps steps")
        return builder.build()
    }
    private var THIS ="saknda";

    private fun createNotificationChannel() {


        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.next_up)
            val descriptionText = getString(R.string.next)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel1 = NotificationChannel(THIS, name, importance).apply {
                description = descriptionText
                setSound(null, null)
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
        }
    }



    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onFlushCompleted(p0: Sensor?) {

    }

    override fun onTaskRemoved(rootIntent: Intent?) {

        super.onTaskRemoved(rootIntent)
        // Restart service in 500 ms
        (getSystemService(ALARM_SERVICE) as AlarmManager)
                .set(AlarmManager.RTC, System.currentTimeMillis() + 500, PendingIntent
                        .getService(this, 3, Intent(this, MyPedometerService::class.java), 0))

    }



    override fun onDestroy() {
        super.onDestroy()
        //Your Runnable
        v("steps", "service destroyed")
        try {
            val sm = getSystemService(SENSOR_SERVICE) as SensorManager
//            sm.unregisterListener(this)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }






}
