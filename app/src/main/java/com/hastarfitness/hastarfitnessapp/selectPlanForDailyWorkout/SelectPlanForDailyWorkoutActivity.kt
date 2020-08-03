package com.hastarfitness.hastarfitnessapp.selectPlanForDailyWorkout

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.hastarfitness.hastarfitnessapp.ActivityDashboard
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.ViewModel
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.createYourOwnPlan.CreateYourOwnPlanActivity
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.database.ExerciseDbModel
import com.hastarfitness.hastarfitnessapp.database.WorkoutPlansDbModel
import com.hastarfitness.hastarfitnessapp.finalExerciseListBeforeStartingWorkout.FinalExerciseListActivity
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import com.hastarfitness.hastarfitnessapp.selectBaseWorkoutPlan.WorkoutPlansListActivity
import kotlinx.android.synthetic.main.activity_select_plan_for_daily_workout.*

/**
 * Activity for user to select any plan (App suggested or Predefined plans)
 * for his daily workout sessions
 *
 * @author Bhushan Kadu
 */
class SelectPlanForDailyWorkoutActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var viewModel: ViewModel
    var workoutType = "yoga"
    var cardioSubType = AppConstants.HIIT
    var intensity = "15 days"
    private var day = "monday"
    private var isCardioEnabled = false
    private lateinit var session: Session
    lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_plan_for_daily_workout)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //init class
        init()


        var workoutPlansList = listOf<WorkoutPlansDbModel>()
        var workoutPlansFilterList = listOf<String>("All Plans", "My Plans", "Fav Plans", "Beginner", "Intermediate", "Advanced")

        //code runs in background thread
        viewModel.getAllAppSavedPlansByType(db, workoutType)

        //invokes when exercises value change happen
        viewModel.plansList.observe(this, Observer { it ->
            workoutPlansList = it

            workoutPlansRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = SelectPlanForDailyWorkoutListAdapter(workoutPlansList, this@SelectPlanForDailyWorkoutActivity)
            }

            filterWorkoutHorizontalRecyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = SelectPlanForDailyWorkoutFilterListAdapter(workoutPlansFilterList, (workoutPlansRecyclerView.adapter as SelectPlanForDailyWorkoutListAdapter))
            }
        })

        //populate a temporary table which would have today's exercises to be performed
        //only if the day is changed
        if (session.isDayChanged!!) {
            session.day = day
            generateFinalExerciseTable()
        }



        lightCardio_radio.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                selectSingleRadio(buttonView.id)
                cardioSubType = AppConstants.LIGHT_CARDIO
            }
        }
        hiit_radio.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                selectSingleRadio(buttonView.id)
                cardioSubType = AppConstants.HIIT
            }
        }
        plyometrics_radio.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                selectSingleRadio(buttonView.id)
                cardioSubType = AppConstants.PLYOMETRICS
            }
        }
        jointFriendly_radio.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                selectSingleRadio(buttonView.id)
                cardioSubType = AppConstants.JOINT_FRIENDLY
            }
        }

    }
    private fun selectSingleRadio(checkedId:Int){
        lightCardio_radio.isChecked = false
        hiit_radio.isChecked = false
        plyometrics_radio.isChecked = false
        jointFriendly_radio.isChecked = false

        findViewById<RadioButton>(checkedId).isChecked = true
    }

    fun init() {
        session = Session(this)
        try {
            //this applies if activity is called from homeFragment
            day = session.day!!
            session.todaysWorkoutType = when (day) {
                AppConstants.MONDAY -> session.mondayBodyWeight
                AppConstants.TUESDAY -> session.tuesdayBodyWeight
                AppConstants.WEDNESDAY -> session.wednesdayBodyWeight
                AppConstants.THURSDAY -> session.thursdayBodyWeight
                AppConstants.FRIDAY -> session.fridayBodyWeight
                AppConstants.SATURDAY -> session.saturdayBodyWeight
                AppConstants.SUNDAY -> session.sundayBodyWeight
                else -> ""
            }
            workoutType = session.todaysWorkoutType!!
            intensity = session.intensity!!
            isCardioEnabled = session.isCardioEnabled!!

            //cardio
            cardioSubType = AppConstants.dailyPlanCardio[day]!!
            session.cardioSubType = cardioSubType
            //dependency
            session.type = workoutType


        } catch (e: Exception) {
            //this applies if activity is called from CreateNewPlan/Show Base Plan
            workoutType = session.type!!
            intensity = session.intensity!!
            isCardioEnabled = session.isCardioEnabled!!
            day = session.day!!
        }

        //set title for app's suggested workout
        titleExercise.text = workoutType.capitalize()

        //if user selected cardio then make it visible
        if (isCardioEnabled) {
            cardio.visibility = View.VISIBLE
        } else {
            cardio.visibility = View.GONE
        }

        //set click listeners
        cardioEditBtn.setOnClickListener(this)
        workoutEditBtn.setOnClickListener(this)
        workout_start_btn.setOnClickListener(this)
        createYourNewPlanTextView.setOnClickListener(this)
        cardio_start_btn.setOnClickListener(this)

        instantiateDb()

        //setup ViewModel
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)



    }


    //populate a temporary table which would have today's exercises to be performed
    private fun generateFinalExerciseTable() {
        var exerciseList: ArrayList<ExerciseDbModel>
        val workoutSubType = when (day) {
            AppConstants.MONDAY -> session.mondayBodyWeight
            AppConstants.TUESDAY -> session.tuesdayBodyWeight
            AppConstants.WEDNESDAY -> session.wednesdayBodyWeight
            AppConstants.THURSDAY -> session.thursdayBodyWeight
            AppConstants.FRIDAY -> session.fridayBodyWeight
            AppConstants.SATURDAY -> session.saturdayBodyWeight
            AppConstants.SUNDAY -> session.sundayBodyWeight
            else -> ""
        }
//                AppConstants.dailyPlanBodyWeight[day]
        viewModel.getAll(db)//this is for if db is not populated early in Dashboard Activity
        viewModel.getSinglePlanByName(db, "${intensity}_${workoutSubType!!.toLowerCase().split(" ").joinToString("")}", true)
        viewModel.singleBodyWeightPlan.observe(this, Observer { it ->
            viewModel.getPlan(db, it.id, true)
            viewModel.fullPlanBodyWeight.observe(this, Observer { its ->

                exerciseList = its.map {
                    ExerciseDbModel(
                            it.id,
                            it.name,
                            it.type,
                            it.desc,
                            " ",
                            it.intensity,
                            it.time,
                            it.mmet,
                            it.fmet)
                } as ArrayList<ExerciseDbModel>

                //firstly delete old rows
                viewModel.deleteExercisesFromFinalBodyWeightExTable(db)
                viewModel.deletedRowsBodyWeight.observe(this, Observer {
                    it
                    //after delete now add new rows from exerciseList
                    viewModel.insertFinalExercisesBodyWeight(db, exerciseList)
                })
                viewModel.insertedRowsListBodyWeight.observe(this, Observer {
                    it
                    //for cardio
                    val cardioSubTypeByDailyPlan = AppConstants.dailyPlanCardio[day]
                    viewModel.getSinglePlanByName(db, cardioSubTypeByDailyPlan!!.toLowerCase().split(" ").joinToString(""), false)

                })
            })
        })
        viewModel.singleCardioPlan.observe(this, Observer { it ->
            viewModel.getPlan(db, it.id, false)
            viewModel.fullPlanCardio.observe(this, Observer { its ->

                exerciseList = its.map {
                    ExerciseDbModel(
                            it.id,
                            it.name,
                            it.type,
                            it.desc,
                            " ",
                            it.intensity,
                            it.time,
                            it.mmet,
                            it.fmet)
                } as ArrayList<ExerciseDbModel>

                //firstly delete old rows
                viewModel.deleteExercisesFromFinalCardioExTable(db)
                viewModel.deletedRowsCardio.observe(this, Observer {
                    it
                    //after delete now add new rows from exerciseList
                    viewModel.insertFinalExercisesCardio(db, exerciseList)
                })
                viewModel.insertedRowsListCardio.observe(this, Observer {
                    it
                })
            })
        })
    }


    private fun instantiateDb() {
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "HasterDb.db")
                .build()
    }

    //create new plan button click
    private fun creteNewPlan() {
        val i = Intent(applicationContext, WorkoutPlansListActivity::class.java)
        i.putExtra(AppConstants.WORKOUT_TYPE, workoutType)
        startActivity(i)
    }

    /**
     * get time from time radioGroup
     *
     * @param radioGrp Radio grp from time being extracted
     * @return time in Int
     */
    private fun getTime(radioGrp: RadioGroup): Int {
        //get the id of selected radio btn
        val timeRadioBtnId: Int = radioGrp.checkedRadioButtonId

        // find the radiobutton by from id
        val typeRadioBtn = findViewById<RadioButton>(timeRadioBtnId)
        return typeRadioBtn.tag.toString().toInt()
    }

    /**
     * get cardio intensity from time radioGroup
     *
     * @param radioGrp Radio grp from cardio intensity being extracted
     * @return cardio intensity in String
     */
    private fun getCardioIntensity(radioGrp: RadioGroup): String {

        return cardioSubType
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cardioEditBtn -> {
                val i = Intent(applicationContext, CreateYourOwnPlanActivity::class.java)
                i.putExtra(AppConstants.INTENSITY, intensity)
                i.putExtra(AppConstants.WORKOUT_SUB_TYPE, cardioSubType)
                i.putExtra(AppConstants.WORKOUT_TYPE, AppConstants.CARDIO)
                i.putExtra(AppConstants.IS_CUSTOMIZE, true)
                startActivity(i)
            }
            R.id.workoutEditBtn -> {
                val i = Intent(applicationContext, CreateYourOwnPlanActivity::class.java)
                i.putExtra(AppConstants.INTENSITY, intensity)
                i.putExtra(AppConstants.WORKOUT_SUB_TYPE, workoutType)
                i.putExtra(AppConstants.WORKOUT_TYPE, AppConstants.BODY_WEIGHT)
                i.putExtra(AppConstants.IS_CUSTOMIZE, true)
                startActivity(i)
            }
            R.id.cardio_start_btn -> {
                val i = Intent(applicationContext, FinalExerciseListActivity::class.java)
                val time = getTime(cardioTime)
                i.putExtra(AppConstants.WORKOUT_TIME_IN_MINUTES, time)
                i.putExtra(AppConstants.WORKOUT_TYPE, AppConstants.CARDIO)
                val cardioSubType = getCardioIntensity(cardioSubType_radioGroup)
                i.putExtra(AppConstants.WORKOUT_SUB_TYPE, cardioSubType)
                i.putExtra(AppConstants.IS_SUGGESTED_PLAN, true)
                startActivity(i)
            }
            R.id.workout_start_btn -> {
                val i = Intent(applicationContext, FinalExerciseListActivity::class.java)
                val time = getTime(workoutTimeRadio)
                i.putExtra(AppConstants.WORKOUT_TIME_IN_MINUTES, time)
                i.putExtra(AppConstants.WORKOUT_TYPE, AppConstants.BODY_WEIGHT)
                i.putExtra(AppConstants.WORKOUT_SUB_TYPE, workoutType)
                i.putExtra(AppConstants.INTENSITY, intensity)
                i.putExtra(AppConstants.IS_SUGGESTED_PLAN, true)
                startActivity(i)
            }
            R.id.createYourNewPlanTextView -> {
                creteNewPlan()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.select_plan_for_workout_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.add_plan -> {
                creteNewPlan()
            }
            android.R.id.home -> onBackPressed();

        }
        return true
    }

    override fun onBackPressed() {
        //exit activity
        val intent = Intent(applicationContext, ActivityDashboard::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}