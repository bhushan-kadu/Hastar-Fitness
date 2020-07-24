package com.hastarfitness.hastarfitnessapp.profile

import android.app.Activity
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.hastarfitness.hastarfitnessapp.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class CurrentDayDecorator(context: Activity?, currentDay: CalendarDay) : DayViewDecorator {
    private val drawable: Drawable? = ContextCompat.getDrawable(context!!, R.drawable.circle_background)
    private var myDay = currentDay
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == myDay
    }

    override fun decorate(view: DayViewFacade) {
        view.setBackgroundDrawable(drawable!!)
    }
}