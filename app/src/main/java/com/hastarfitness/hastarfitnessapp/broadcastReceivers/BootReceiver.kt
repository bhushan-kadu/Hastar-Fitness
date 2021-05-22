package com.hastarfitness.hastarfitnessapp.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.hastarfitness.hastarfitnessapp.pedometer.MyPedometerService
import com.hastarfitness.hastarfitnessapp.util.API26Wrapper


class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if (Build.VERSION.SDK_INT >= 26) {
            API26Wrapper.startForegroundService(context, Intent(context, MyPedometerService::class.java))
        } else {
            context.startService(Intent(context, MyPedometerService::class.java))
        }
    }
}