package com.hastarfitness.hastarfitnessapp.startingPages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.fitnessCalculators.FitnessCalculators
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import java.lang.Exception
import java.util.*

class FragmentAppInfoPage : Fragment() {
    private lateinit var auth: FirebaseAuth
    lateinit var session: Session
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_app_info_screen, container, false)
        val startJourneyBtn = rootView.findViewById<Button>(R.id.start_journey)
        val parentActivity = activity as ActivityStartPages

        //init session
        session = Session(parentActivity)

        // Initialize Firebase Auth
        auth = Firebase.auth

        startJourneyBtn.setOnClickListener {
//            parentActivity.session.age = parentActivity.age
//            parentActivity.session.dateOfBirth = parentActivity.dob
            parentActivity.session.gender = parentActivity.gender
            parentActivity.session.heightCm = parentActivity.height
            parentActivity.session.weightInKg = parentActivity.weight
            parentActivity.session.goalWeight = parentActivity.goalWeight
            parentActivity.session.weeklyActivity = parentActivity.weeklyActivity
            parentActivity.session.areStartPagesShown = true
            saveUserInformation()
            setDietGoal()

            val session = Session(activity as ActivityStartPages)
            val calInstance = Calendar.getInstance()
            session.day = calInstance.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
            session.day  = session.day!!.toLowerCase()
            val day = session.day!!

            session.todaysWorkoutType = AppConstants.dailyPlanBodyWeight[day]

            startActivity(Intent(parentActivity, AppStartLoadingScreen::class.java))
        }
        return rootView
    }
    val fitnessCalculators = FitnessCalculators()
    fun setDietGoal(){
        val bmr = fitnessCalculators.calculateBMRMetric(session.heightCm!!, session.weightInKg!!, session.age!!, session.gender == AppConstants.MALE)
        val tdee = fitnessCalculators.calculateTDEE(bmr, session.weeklyActivity!!)
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
    }
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

    private fun saveUserInformation(){

        try {
            val user = auth.currentUser!!
            session.userName = user.displayName
            session.photoUrl = user.photoUrl.toString()
            session.userEmail = user.email.toString()
        }catch (e:Exception){
            session.userName = "Child User"
            session.userEmail = ""
            val imageFilePath = "file:///android_asset/images/child.webp"
            session.photoUrl = imageFilePath
        }

    }
}