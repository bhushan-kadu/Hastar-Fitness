package com.hastarfitness.hastarfitnessapp.startingPages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.hastarfitness.hastarfitnessapp.ActivityDashboard
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.fitnessCalculators.FitnessCalculators
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session

class FragmentGetWeeklyGoal : Fragment() {
    private lateinit var m250GmBtn: Button
    private lateinit var m500GmBtn: Button
    private lateinit var m750GmBtn: Button
    private lateinit var m1000GmBtn: Button
    var weeklyGoal = ""
    lateinit var session:Session
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_diet_get_weekly_goal_in_app_start_pages, container, false)

        m250GmBtn = rootView.findViewById<Button>(R.id._250GmAWeek)
        m500GmBtn = rootView.findViewById<Button>(R.id._500GmAWeek)
        m750GmBtn = rootView.findViewById<Button>(R.id._750GmAWeek)
        m1000GmBtn = rootView.findViewById<Button>(R.id._1000GmAWeek)

        val blackColor = ContextCompat.getColor(requireContext(), R.color.gray)
        val whiteColor = ContextCompat.getColor(requireContext(), android.R.color.white)
        val selectedBackGround = ContextCompat.getDrawable(requireContext(), R.drawable.diet_buttons_background_selected)
        val unSelectedBackGround = ContextCompat.getDrawable(requireContext(), R.drawable.diet_unselected_faint_border)

        session = Session(activity as ActivityStartPages)

        weeklyGoal = AppConstants.GAIN_WEIGHT_500GM_PER_WEEK
        parentActivity = activity as ActivityStartPages
        val dietPreference = session.dietPreference

        if (dietPreference == AppConstants.MAINTAIN_WEIGHT) {
            if(this.isVisible) goToNextPage()
        } else {
            when (dietPreference) {
                AppConstants.GAIN_WEIGHT -> {
                    m250GmBtn.text = "Gain 0.25Kg per week"
                    m500GmBtn.text = "Gain 0.5Kg per week"
                    m750GmBtn.text = "Gain 0.75Kg per week"
                    m1000GmBtn.text = "Gain 1Kg per week"
                }
                AppConstants.WEIGHT_LOSS -> {
                    m250GmBtn.text = "Lose 0.25Kg per week"
                    m500GmBtn.text = "Lose 0.5Kg per week"
                    m750GmBtn.text = "Lose 0.75Kg per week"
                    m1000GmBtn.text = "Lose 1Kg per week"
                }
                else -> {
                    m250GmBtn.text = "Maintain Weight"
                    m500GmBtn.text = "Maintain Weight"
                    m750GmBtn.text = "Maintain Weight"
                    m1000GmBtn.text = "Maintain Weight"
                }
            }

            m250GmBtn.setOnClickListener {
//                m250GmBtn.background = selectedBackGround
//                m250GmBtn.setTextColor(whiteColor)
//                m500GmBtn.background = unSelectedBackGround
//                m500GmBtn.setTextColor(blackColor)
//                m750GmBtn.background = unSelectedBackGround
//                m750GmBtn.setTextColor(blackColor)
//                m1000GmBtn.background = unSelectedBackGround
//                m1000GmBtn.setTextColor(blackColor)

                weeklyGoal = if (dietPreference == AppConstants.GAIN_WEIGHT) {
                    AppConstants.GAIN_WEIGHT_250GM_PER_WEEK
                } else {
                    AppConstants.LOSE_WEIGHT_250GM_PER_WEEK
                }
                calculateGoalsAndSaveToPreferences()
                goToNextPage()
            }
            m500GmBtn.setOnClickListener {
//                m250GmBtn.background = unSelectedBackGround
//                m250GmBtn.setTextColor(blackColor)
//                m500GmBtn.background = selectedBackGround
//                m500GmBtn.setTextColor(whiteColor)
//                m750GmBtn.background = unSelectedBackGround
//                m750GmBtn.setTextColor(blackColor)
//                m1000GmBtn.background = unSelectedBackGround
//                m1000GmBtn.setTextColor(blackColor)

                weeklyGoal = if (dietPreference == AppConstants.GAIN_WEIGHT) {
                    AppConstants.GAIN_WEIGHT_500GM_PER_WEEK
                } else {
                    AppConstants.LOSE_WEIGHT_500GM_PER_WEEK
                }
                calculateGoalsAndSaveToPreferences()
                goToNextPage()
            }
            m750GmBtn.setOnClickListener {
//                m250GmBtn.background = unSelectedBackGround
//                m250GmBtn.setTextColor(blackColor)
//                m500GmBtn.background = unSelectedBackGround
//                m500GmBtn.setTextColor(blackColor)
//                m750GmBtn.background = selectedBackGround
//                m750GmBtn.setTextColor(whiteColor)
//                m1000GmBtn.background = unSelectedBackGround
//                m1000GmBtn.setTextColor(blackColor)

                weeklyGoal = if (dietPreference == AppConstants.GAIN_WEIGHT) {
                    AppConstants.GAIN_WEIGHT_750GM_PER_WEEK
                } else {
                    AppConstants.LOSE_WEIGHT_750GM_PER_WEEK
                }
                calculateGoalsAndSaveToPreferences()
                goToNextPage()
            }
            m1000GmBtn.setOnClickListener {
//                m250GmBtn.background = unSelectedBackGround
//                m250GmBtn.setTextColor(blackColor)
//                m500GmBtn.background = unSelectedBackGround
//                m500GmBtn.setTextColor(blackColor)
//                m750GmBtn.background = unSelectedBackGround
//                m750GmBtn.setTextColor(blackColor)
//                m1000GmBtn.background = selectedBackGround
//                m1000GmBtn.setTextColor(whiteColor)

                weeklyGoal = if (dietPreference == AppConstants.GAIN_WEIGHT) {
                    AppConstants.GAIN_WEIGHT_1000GM_PER_WEEK
                } else {
                    AppConstants.LOSE_WEIGHT_1000GM_PER_WEEK
                }
                goToNextPage()
                calculateGoalsAndSaveToPreferences()

            }
        }

        return rootView
    }
    lateinit var parentActivity:ActivityStartPages
    private fun goToNextPage(){

        parentActivity.viewPager.currentItem = parentActivity.viewPager.currentItem + 1
    }

    private fun calculateGoalsAndSaveToPreferences(){

            session.dietWeeklyGoal = weeklyGoal

            val bmr = fitnessCalculators.calculateBMRMetric(parentActivity.height, parentActivity.weight, parentActivity.age, session.gender == AppConstants.MALE)
            val tdee = fitnessCalculators.calculateTDEE(bmr, parentActivity.weeklyActivity)
            val caloriesToConsume = if (session.dietPreference == AppConstants.WEIGHT_LOSS
                    || session.dietPreference == AppConstants.GAIN_WEIGHT) {
                calculateCaloriesToConsume(tdee, session.dietPreference!!, session.dietWeeklyGoal!!)
            } else {
                tdee
            }

            val macros = fitnessCalculators.macroCalc(caloriesToConsume.toInt(), 45, 25, 30)
            session.goalProtein = macros[AppConstants.PROTEIN]
            session.goalCarbs = macros[AppConstants.CARBS]
            session.goalFat = macros[AppConstants.FAT]
            session.goalCalories = tdee

            goToNextPage()

    }

    private fun setWeeklyGoalButtons() {
        val activity = activity as ActivityStartPages
        if(this.isVisible){
            when (session.dietPreference) {
                AppConstants.GAIN_WEIGHT -> {
                    m250GmBtn.text = "Gain 0.25Kg per week"
                    m500GmBtn.text = "Gain 0.5Kg per week"
                    m750GmBtn.text = "Gain 0.75Kg per week"
                    m1000GmBtn.text = "Gain 1Kg per week"
                }
                AppConstants.WEIGHT_LOSS -> {
                    m250GmBtn.text = "Lose 0.25Kg per week"
                    m500GmBtn.text = "Lose 0.5Kg per week"
                    m750GmBtn.text = "Lose 0.75Kg per week"
                    m1000GmBtn.text = "Lose 1Kg per week"
                }

                AppConstants.MAINTAIN_WEIGHT -> {
                    m250GmBtn.text = "Maintain Weight"
                    m500GmBtn.text = "Maintain Weight"
                    m750GmBtn.text = "Maintain Weight"
                    m1000GmBtn.text = "Maintain Weight"
                    goToNextPage()
                }
            }
        }

    }



    val fitnessCalculators = FitnessCalculators()
    private fun calculateCaloriesToConsume(tdee: Double, dietPref: String, weekGoal: String): Double {

        return when (weekGoal) {
            AppConstants.LOSE_WEIGHT_1000GM_PER_WEEK -> {
                tdee - 1286//7716
            }
            AppConstants.LOSE_WEIGHT_750GM_PER_WEEK -> {
                tdee - 827//5787
            }
            AppConstants.LOSE_WEIGHT_500GM_PER_WEEK -> {
                tdee - 551//3858
            }
            AppConstants.LOSE_WEIGHT_250GM_PER_WEEK -> {
                tdee - 267//1929
            }
            AppConstants.GAIN_WEIGHT_1000GM_PER_WEEK -> {
                tdee + 1286//7716
            }
            AppConstants.GAIN_WEIGHT_750GM_PER_WEEK -> {
                tdee + 827//5787
            }
            AppConstants.GAIN_WEIGHT_500GM_PER_WEEK -> {
                tdee + 551//3858
            }
            AppConstants.GAIN_WEIGHT_250GM_PER_WEEK -> {
                tdee + 267//1929
            }
            else -> {
                -1.0
            }
        }
    }

    override fun onResume() {
        setWeeklyGoalButtons()
        super.onResume()
    }
}