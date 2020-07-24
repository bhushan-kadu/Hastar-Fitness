package com.hastarfitness.hastarfitnessapp.appFragmentsUI.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.room.Room
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.ViewModel
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import com.hastarfitness.hastarfitnessapp.meditationNew.ShowMeditationTypesActivity
import com.hastarfitness.hastarfitnessapp.selectPlanForDailyWorkout.SelectPlanForDailyWorkoutActivity
import com.hastarfitness.hastarfitnessapp.viewPlansFavAndCustom.ViewPlansActivity
import com.hastarfitness.hastarfitnessapp.yoga.ShowYogaTypesActivity
import java.lang.String
import java.math.RoundingMode
import java.text.DecimalFormat

class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel? = null
    private var viewModel: ViewModel? = null
    private var db: AppDatabase? = null
    private var session: Session? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        initialize()
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        view.findViewById<View>(R.id.start_workout).setOnClickListener { v: View? ->
            val i = Intent(context, SelectPlanForDailyWorkoutActivity::class.java)
            startActivity(i)
        }
        view.findViewById<View>(R.id.yoga_play_Btn).setOnClickListener { v: View? ->
            val i = Intent(context, ShowMeditationTypesActivity::class.java)
            startActivity(i)
        }
        view.findViewById<View>(R.id.meditation_play_Btn).setOnClickListener { v: View? ->
            val i = Intent(context, ShowYogaTypesActivity::class.java)
            startActivity(i)
        }
        view.findViewById<View>(R.id.custom_plansBtn).setOnClickListener { v: View? ->
            val i = Intent(context, ViewPlansActivity::class.java)
            i.putExtra(AppConstants.FILTER_BY, AppConstants.MY_PLANS)
            startActivity(i)
        }
        view.findViewById<View>(R.id.fav_plansBtn).setOnClickListener { v: View? ->
            val i = Intent(context, ViewPlansActivity::class.java)
            i.putExtra(AppConstants.FILTER_BY, AppConstants.FAVOURITE_PLANS)
            startActivity(i)
        }
        val mTimeTv = view.findViewById<TextView>(R.id.time_min_textView)
        val mCaloriesTv = view.findViewById<TextView>(R.id.kcal_textView)
        val mWorkoutNoTv = view.findViewById<TextView>(R.id.workout_no_textView)
        viewModel!!.getTodaysUserData(db!!)
        viewModel!!.todayData.observe(requireActivity(), Observer { userDailyDataDbModel ->
            if (userDailyDataDbModel != null) {
                mTimeTv.text = roundingFormat!!.format(userDailyDataDbModel.timeWorkoutPerformed)
                mCaloriesTv.text = roundingFormat!!.format(userDailyDataDbModel.calories)
                mWorkoutNoTv.text = roundingFormat!!.format(userDailyDataDbModel.noOfWorkout.toLong())
            } else {
                mTimeTv.text = "0"
                mCaloriesTv.text = "0"
                mWorkoutNoTv.text = "0"
            }
        })
        val streakTV = view.findViewById<TextView>(R.id.streak_no_textView)
        val streakString = session!!.streakNo.toString() + " Day"
        streakTV.text = streakString
        val userGoalTv = view.findViewById<TextView>(R.id.userGoal_textView)
        val todaysBodyPartTv = view.findViewById<TextView>(R.id.todaysBodyPart_button)
        val difference = session!!.goalWeight!! - session!!.weightInKg!!
        if (difference < 0) {
            session!!.dietPreference = AppConstants.GAIN_WEIGHT
        } else if (difference > 0) {
            session!!.dietPreference = AppConstants.WEIGHT_LOSS
        } else {
            session!!.dietPreference = AppConstants.MAINTAIN_WEIGHT
        }
        userGoalTv.text = session!!.dietPreference!!.split("_").joinToString(" ").capitalize()
        todaysBodyPartTv.text = session!!.todaysWorkoutType!!.capitalize()
        return view

    }

    var roundingFormat: DecimalFormat? = null
    fun initialize() {

        //setup session
        session = Session(requireActivity())

        //Instantiate db
        instantiateDb()

        //setup ViewModel
        viewModel = ViewModelProviders.of(this).get(ViewModel::class.java)

        //init format
        roundingFormat = DecimalFormat("#.##")
        roundingFormat!!.roundingMode = RoundingMode.CEILING

        //set streak number
    }

    private fun setStreakNo() {}

    /**
     * instantiates the db variable
     */
    private fun instantiateDb() {
        db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "HasterDb.db")
                .build()
    }
}