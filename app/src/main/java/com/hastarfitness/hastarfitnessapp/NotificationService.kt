package com.hastarfitness.hastarfitnessapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import java.util.*

class NotificationService : Service() {
    var timer: Timer? = null
    var timerTask: TimerTask? = null
    var TAG = "Timers"
    var Your_X_SECS = 5
    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)
        startTimer()
        return START_STICKY
    }

    override fun onCreate() {
        Log.e(TAG, "onCreate")
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        stoptimertask()
        super.onDestroy()
    }

    //we are going to use a handler to be able to run in our TimerTask
    val handler = Handler()
    fun startTimer() {
        //set a new Timer
        timer = Timer()


        //initialize the TimerTask's job
        initializeTimerTask()

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer!!.schedule(timerTask, 5000, Your_X_SECS * 1000.toLong()) //
        //timer.schedule(timerTask, 5000,1000); //
    }

    fun stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    fun initializeTimerTask() {
        timerTask = object : TimerTask() {
            override fun run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post {
                                            YOURNOTIFICATIONFUNCTION();
                }
            }
        }
    }

    fun YOURNOTIFICATIONFUNCTION(){


        //Creates a new launcher intent, equivalent to the intent generated by
        //clicking the icon on the home screen.
        val intent = Intent(this, ActivityStartExercise::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, AppConstants.CHANNEL_ID)
                .setSmallIcon(R.drawable.dot_one)
                .setContentTitle("Hastar Fitness")
                .setContentText("go to your ongoing workout")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)//to make notification un-swipable


        //this method is for oreo and above
        // - doesn't affect lower apis
        createNotificationChannel()

        //show notification by this code
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(AppConstants.APP_WORKOUT_RESTART_NOTIFICATION, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.next_up)
            val descriptionText = getString(R.string.next)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel1 = NotificationChannel(AppConstants.CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setSound(null, null)
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
        }
    }
}