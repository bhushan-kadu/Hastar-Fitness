package com.hastarfitness.hastarfitnessapp.diet.dietStartPages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants

class FragmentGetGoal : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_get_diet_goal, container, false)
        val buttonLose = rootView.findViewById<Button>(R.id.loseWeight_btn)
        val buttonGain = rootView.findViewById<Button>(R.id.gainWeight_btn)
        val buttonMaintain = rootView.findViewById<Button>(R.id.maintainWeight_btn)
        val nextBtn = rootView.findViewById<ImageView>(R.id.goalToBodyInfo_button)
        val closeBtn = rootView.findViewById<LinearLayout>(R.id.close_Btn)

        val blackColor = ContextCompat.getColor(requireContext(), android.R.color.black)
        val whiteColor = ContextCompat.getColor(requireContext(), android.R.color.white)
        val selectedBackGround = ContextCompat.getDrawable(requireContext(), R.drawable.diet_buttons_background_selected)
        val unSelectedBackGround = ContextCompat.getDrawable(requireContext(), R.drawable.diet_buttons_background_unselected)

        var dietPreference = AppConstants.WEIGHT_LOSS
        buttonLose.setOnClickListener {
            buttonGain.background = unSelectedBackGround
            buttonMaintain.background = unSelectedBackGround
            buttonGain.setTextColor(blackColor)
            buttonMaintain.setTextColor(blackColor)

            buttonLose.background = selectedBackGround
            buttonLose.setTextColor(whiteColor)
            dietPreference = AppConstants.WEIGHT_LOSS
        }
        buttonGain.setOnClickListener {
            buttonLose.background = unSelectedBackGround
            buttonMaintain.background = unSelectedBackGround
            buttonMaintain.setTextColor(blackColor)
            buttonLose.setTextColor(blackColor)

            buttonGain.background = selectedBackGround
            buttonGain.setTextColor(whiteColor)
            dietPreference = AppConstants.GAIN_WEIGHT
        }
        buttonMaintain.setOnClickListener {
            buttonGain.background = unSelectedBackGround
            buttonLose.background = unSelectedBackGround
            buttonLose.setTextColor(blackColor)
            buttonGain.setTextColor(blackColor)

            buttonMaintain.background = selectedBackGround
            buttonMaintain.setTextColor(whiteColor)

            dietPreference = AppConstants.MAINTAIN_WEIGHT
        }
        closeBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        nextBtn.setOnClickListener {
           val activity = activity as DietStartPagesActivity
            activity.dietPreference = dietPreference

            activity.ab!!.show()
            activity.viewPager.currentItem += 1
        }
        return rootView
    }


}