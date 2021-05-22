package com.hastarfitness.hastarfitnessapp.broadcastReceivers
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hastarfitness.hastarfitnessapp.pedometer.MyPedometerService


class ShutdownReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.startService(Intent(context, MyPedometerService::class.java))
    }
}