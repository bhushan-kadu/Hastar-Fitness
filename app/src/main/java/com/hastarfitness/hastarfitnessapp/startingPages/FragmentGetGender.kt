package com.hastarfitness.hastarfitnessapp.startingPages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import kotlinx.android.synthetic.main.fragment_get_gender.*

class FragmentGetGender : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_get_gender, container, false)

        val maleButton = rootView.findViewById<Button>(R.id.maleBtn)
        val femaleButton = rootView.findViewById<Button>(R.id.femaleBtn)
        val otherButton = rootView.findViewById<Button>(R.id.maleBtn)

        val parentActivity = activity as ActivityStartPages

        maleButton.setOnClickListener {
            parentActivity.gender = AppConstants.MALE
            parentActivity.viewPager.setCurrentItem(parentActivity.viewPager.currentItem + 1, true)
        }
        femaleButton.setOnClickListener {
            parentActivity.gender = AppConstants.FEMALE
            parentActivity.viewPager.setCurrentItem(parentActivity.viewPager.currentItem + 1, true)
        }
        otherButton.setOnClickListener {
            parentActivity.gender = AppConstants.OTHER_GENDER
            parentActivity.viewPager.setCurrentItem(parentActivity.viewPager.currentItem + 1, true)
        }

        return rootView
    }
}