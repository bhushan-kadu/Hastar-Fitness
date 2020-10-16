package com.hastarfitness.hastarfitnessapp.appFragmentsUI.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.hastarfitness.hastarfitnessapp.ActivityDashboard
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.ViewModel
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.database.StepCountModel
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import com.hastarfitness.hastarfitnessapp.meditationNew.ShowMeditationTypesActivity
import com.hastarfitness.hastarfitnessapp.pedometer.MyPedometerService
import com.hastarfitness.hastarfitnessapp.pedometer.PedometerViewModel
import com.hastarfitness.hastarfitnessapp.pedometer.StepTakenMessage
import com.hastarfitness.hastarfitnessapp.selectPlanForDailyWorkout.SelectPlanForDailyWorkoutActivity
import com.hastarfitness.hastarfitnessapp.viewPlansFavAndCustom.ViewPlansActivity
import com.hastarfitness.hastarfitnessapp.yoga.ShowYogaTypesActivity
import kotlinx.android.synthetic.main.fragment_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.String
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var viewModel: ViewModel
    private lateinit var db: AppDatabase
    private lateinit var session: Session
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


        val userGreetLl = view.findViewById<LinearLayout>(R.id.userGreeting_layout)
        val userNameTv = view.findViewById<TextView>(R.id.userName_textView)
        userNameTv.text = if(session!!.userName != "" || session!!.userName!!.toLowerCase() != "guest user"){
            session!!.userName!!.split(" ")[0]
        }else{
            userGreetLl.visibility = View.GONE
            ""
        }

        when {
            difference < 0 -> {
                session!!.dietPreference = AppConstants.GAIN_WEIGHT
            }
            difference > 0 -> {
                session!!.dietPreference = AppConstants.WEIGHT_LOSS
            }
            else -> {
                session!!.dietPreference = AppConstants.MAINTAIN_WEIGHT
            }
        }
        userGoalTv.text = session!!.dietPreference!!.split("_").joinToString(" ").capitalize()
        todaysBodyPartTv.text = session!!.todaysWorkoutType!!.capitalize()
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val calInstance = Calendar.getInstance()
        val dayNo = calInstance[Calendar.DAY_OF_MONTH]
        viewModel.getQuoteByDayNo(db, dayNo)

        viewModel.quote.observe(requireActivity(), Observer {
            quote_textView.text = "\"${it.quote}\""
        })

        //set pedometer
        spfPedometer = requireActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE)
        val stepsGoal =  spfPedometer.getInt("steps_goal_everyday", 6000)
        steps_seekArc.max = stepsGoal
        stepsGoal_textView.text = stepsGoal.toString()

    }

    var roundingFormat: DecimalFormat? = null
    lateinit var viewModelPedometer: PedometerViewModel
    lateinit var spfPedometer:SharedPreferences
    fun initialize() {

        //start pedometer service calling this again has same effect
        requireActivity().startService(Intent(requireContext(), MyPedometerService::class.java))



        //setup session
        session = Session(requireActivity())

        //Instantiate db
        instantiateDb()

        //setup ViewModel

//        viewModel = ViewModelProvider(this).get(ViewModel::class.java)
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)
        viewModelPedometer = ViewModelProvider(this).get(PedometerViewModel::class.java)

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

    private fun getTodaysDate(): Date {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        return today.time
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: StepTakenMessage?) {
        val steps = event!!.todaysSteps
        Log.v("steps", steps.toString())
        step_count.text = steps.toString()

        steps_seekArc.progress = steps
        val stepsGoal = spfPedometer.getInt("steps_goal_everyday", 6000)
        val stepsRemain = stepsGoal - steps
        stepRemain_textView.text = if(stepsRemain >= 0){
            stepsRemain.toString()
        }else{
            0.toString()
        }

        viewModelPedometer.insertOrUpdateStepForTheDate(db, StepCountModel(getTodaysDate(), steps))

    }

    override fun onResume() {
        super.onResume()
        val spf = requireActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE)
        step_count.text = spf.getInt("my_last_saved_step_value", 0).toString()
    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}