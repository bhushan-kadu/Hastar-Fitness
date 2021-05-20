package com.hastarfitness.hastarfitnessapp

import java.util.*


object Util {
    /**
     * @return milliseconds since 1.1.1970 for today 0:00:00 local timezone
     */
    val today: Long
        get() {
            val c = Calendar.getInstance()
            c.timeInMillis = System.currentTimeMillis()
            c[Calendar.HOUR_OF_DAY] = 0
            c[Calendar.MINUTE] = 0
            c[Calendar.SECOND] = 0
            c[Calendar.MILLISECOND] = 0
            return c.timeInMillis
        }

    /**
     * @return milliseconds since 1.1.1970 for tomorrow 0:00:01 local timezone
     */
    val tomorrow: Long
        get() {
            val c = Calendar.getInstance()
            c.timeInMillis = System.currentTimeMillis()
            c[Calendar.HOUR_OF_DAY] = 0
            c[Calendar.MINUTE] = 0
            c[Calendar.SECOND] = 1
            c[Calendar.MILLISECOND] = 0
            c.add(Calendar.DATE, 1)
            return c.timeInMillis
        }
}