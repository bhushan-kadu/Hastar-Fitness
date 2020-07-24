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
import com.hastarfitness.hastarfitnessapp.ActivityDashboard
import com.hastarfitness.hastarfitnessapp.AppStartLoadingScreen
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
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
            parentActivity.session.age = parentActivity.age
            parentActivity.session.gender = parentActivity.gender
            parentActivity.session.heightCm = parentActivity.height
            parentActivity.session.weightInKg = parentActivity.weight
            parentActivity.session.goalWeight = parentActivity.goalWeight
            parentActivity.session.weeklyActivity = parentActivity.weeklyActivity
            parentActivity.session.areStartPagesShown = true
            saveUserInformation()

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
    private fun saveUserInformation(){
        val user = auth.currentUser!!
        session.userName = user.displayName
        session.photoUrl = user.photoUrl.toString()
        session.userEmail = user.email.toString()
    }
}