package com.hastarfitness.hastarfitnessapp.startingPages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants

class FragmentGetActivityLevel : Fragment(), View.OnClickListener {
    private lateinit var parentActivity:ActivityStartPages
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_get_activity_level, container, false)

        val veryLittleButton = rootView.findViewById<Button>(R.id.veryLittle)
        val oneToThreeButton = rootView.findViewById<Button>(R.id.onetoThree)
        val threeToFiveButton = rootView.findViewById<Button>(R.id.threetofive)
        val sixToSevenButton = rootView.findViewById<Button>(R.id.sixToSeven)
        val veryHardButton = rootView.findViewById<Button>(R.id.veryHard)

        parentActivity = activity as ActivityStartPages

        veryLittleButton.setOnClickListener {
            parentActivity.weeklyActivity = AppConstants.LITTLE_OR_NO_EXERCISE
            parentActivity.viewPager.currentItem = parentActivity.viewPager.currentItem + 1
        }
        oneToThreeButton.setOnClickListener {
            parentActivity.weeklyActivity = AppConstants.LIGHT_EXERCISE
            parentActivity.viewPager.currentItem = parentActivity.viewPager.currentItem + 1
        }
        threeToFiveButton.setOnClickListener {
            parentActivity.weeklyActivity = AppConstants.MODERATE_EXERCISE
            parentActivity.viewPager.currentItem = parentActivity.viewPager.currentItem + 1
        }
        sixToSevenButton.setOnClickListener {
            parentActivity.weeklyActivity = AppConstants.HARD_EXERCISE
            parentActivity.viewPager.currentItem = parentActivity.viewPager.currentItem + 1
        }
        veryHardButton.setOnClickListener {
            parentActivity.weeklyActivity = AppConstants.VERY_HARD_EXERCISE
            parentActivity.viewPager.currentItem = parentActivity.viewPager.currentItem + 1
        }
        return rootView
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.sixToSeven -> {
                parentActivity.weeklyActivity = AppConstants.HARD_EXERCISE
            parentActivity.viewPager.currentItem = parentActivity.viewPager.currentItem + 1

            }
        }
    }
}