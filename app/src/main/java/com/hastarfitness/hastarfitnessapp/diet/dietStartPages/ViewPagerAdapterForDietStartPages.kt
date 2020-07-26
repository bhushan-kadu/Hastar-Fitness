package com.hastarfitness.hastarfitnessapp.diet.dietStartPages

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hastarfitness.hastarfitnessapp.R

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 *
 * @author Bhushan Kadu
 */
class ViewPagerAdapterForDietStartPages(fa: FragmentActivity?) : FragmentStateAdapter(fa!!) {
    override fun createFragment(position: Int): Fragment {
        var fragment:Fragment? = null
        when (position) {
            0 -> fragment = FragmentGetGoal()
            1 -> fragment = FragmentGetBodyInfo()
            2 -> fragment = FragmentGetWeeklyGoal()
        }
        return fragment!!
    }

    override fun getItemCount(): Int {
        // Show 6 total pages.
        return 3
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(R.string.title_activity_start_diet,
                R.string.title_activity_get_body_information,
                R.string.title_activity_get_week_goal)
    }
}