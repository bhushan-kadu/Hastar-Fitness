package com.hastarfitness.hastarfitnessapp.settings

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hastarfitness.hastarfitnessapp.ActivityDashboard
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.ViewModel
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.database.RestTimeModel
import com.hastarfitness.hastarfitnessapp.diet.dietStartPages.DietStartPagesActivity
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import kotlinx.android.synthetic.main.activity_settings.*
import java.lang.reflect.Field
import java.util.*
import kotlin.collections.ArrayList

class AppSettingsActivity : AppCompatActivity(), StartDragListener {
    var day = ""
    private lateinit var viewModel: ViewModel
    lateinit var db: AppDatabase
    lateinit var restTimeModel: RestTimeModel
    private lateinit var dlgSetRestTime: DlgSetRestTime
    private lateinit var dlgSetWalkingGoal: DlgSetWalkingGoal
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initialize()

        supportActionBar!!.title = "Settings"

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val session = Session(this)
        val spf = getSharedPreferences("pedometer", Context.MODE_PRIVATE)
        val stepsGoal = spf.getInt("steps_goal_everyday", 6000)
        pedometerGoal_textView.text = stepsGoal.toString()




//        get the rest time from session if present else get from db


        dlgSetRestTime = DlgSetRestTime(this@AppSettingsActivity, 10)//temp
        dlgSetRestTime.create()

        dlgSetWalkingGoal = DlgSetWalkingGoal(this@AppSettingsActivity, stepsGoal)//temp
        dlgSetWalkingGoal.create()

        dlgSetRestTime.setOnShowListener {
            dlgSetRestTime.isSaved = false
        }

        dlgSetRestTime.setOnDismissListener {
            if (dlgSetRestTime.isSaved) {
                val changedRestTime = dlgSetRestTime.restTime
                viewModel.updateRest(db, RestTimeModel(restTimeModel.id, restTimeModel.type, restTimeModel.intensity, restTimeModel.numberOfExerciseAfter, changedRestTime))
                restTime_textView.text = "$changedRestTime Sec"
            }

        }

        dlgSetWalkingGoal.setOnDismissListener {
            if (dlgSetWalkingGoal.isSaved) {
                val stepGoal = dlgSetWalkingGoal.stepsGoal
                spf.edit().putInt("steps_goal_everyday", stepGoal).apply()
                pedometerGoal_textView.text = stepGoal.toString()
            }

        }
        val workoutType = session.todaysWorkoutType!!
        val intensity = session.intensity!!
        viewModel.getRest(db, AppConstants.BODY_WEIGHT, intensity.toLowerCase())

        viewModel.restTime.observe(this, Observer { it ->
            restTimeModel = it

//            restTime_Seekbar.setProgress(it.restTime.toFloat())

            dlgSetRestTime.restTime = it.restTime

            restTime_textView.text = "${restTimeModel.restTime} Sec"
        })



        openRestChangeDialogue_Btn.setOnClickListener {
            dlgSetRestTime.show()
        }
        pedometerGoal_button.setOnClickListener {
            dlgSetWalkingGoal.show()
        }


        val calInstance = Calendar.getInstance()
        day = calInstance.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())!!.toLowerCase()


        val intensityRadioGrp: RadioGroup = findViewById(R.id.exerciseIntensityRadio)
        val isCardioEnabledCbx = findViewById<CheckBox>(R.id.isCardioEnabled)

        val intensityRadioId = getResId(session.intensity!!.toLowerCase(), R.id::class.java)
        intensityRadioGrp.check(intensityRadioId)

        isCardioEnabledCbx.isChecked = session.isCardioEnabled!!

        //show drag-dropable list
        populateRecyclerView()

        //set current day color
        setDayColor(day)


        save_prefrances.setOnClickListener {

            val intensityRadioBtnId: Int = intensityRadioGrp.checkedRadioButtonId
            val intensityRadioBtn = findViewById<RadioButton>(intensityRadioBtnId)
            val intensityString = intensityRadioBtn.tag.toString()

            for (child in 0 until settings_recyclerView.childCount) {
                val viewGroup = ((settings_recyclerView.getChildAt(child) as ViewGroup).getChildAt(0) as ViewGroup)
                val spinner = (viewGroup.getChildAt(0) as Spinner)
                val workoutSubType = when (spinner.selectedItemPosition) {
                    0 -> AppConstants.FULL_BODY
                    1 -> AppConstants.UPPER_BODY
                    2 -> AppConstants.CORE_STRENGTH
                    3 -> AppConstants.LOWER_BODY
                    else -> ""
                }
                val dayString = when (child) {
                    0 -> AppConstants.MONDAY
                    1 -> AppConstants.TUESDAY
                    2 -> AppConstants.WEDNESDAY
                    3 -> AppConstants.THURSDAY
                    4 -> AppConstants.FRIDAY
                    5 -> AppConstants.SATURDAY
                    6 -> AppConstants.SUNDAY
                    else -> ""
                }
                when (dayString) {
                    AppConstants.MONDAY -> session.mondayBodyWeight = workoutSubType
                    AppConstants.TUESDAY -> session.tuesdayBodyWeight = workoutSubType
                    AppConstants.WEDNESDAY -> session.wednesdayBodyWeight = workoutSubType
                    AppConstants.THURSDAY -> session.thursdayBodyWeight = workoutSubType
                    AppConstants.FRIDAY -> session.fridayBodyWeight = workoutSubType
                    AppConstants.SATURDAY -> session.saturdayBodyWeight = workoutSubType
                    AppConstants.SUNDAY -> session.sundayBodyWeight = workoutSubType
                }
            }

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

            //exit activity
            val intent = Intent(applicationContext, ActivityDashboard::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)

            Toast.makeText(this, "Preferences Saved", Toast.LENGTH_LONG).show()
        }

        set_diet_btn.setOnClickListener {
            startActivity(Intent(this, DietStartPagesActivity::class.java))
        }

        isCardioEnabled.setOnCheckedChangeListener { compoundButton, b ->
            session.isCardioEnabled = b
        }


        exerciseIntensityRadio.setOnCheckedChangeListener { radioGroup, i ->
            val radioBtn = findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
            when (radioBtn.text.toString().toLowerCase()) {
                AppConstants.BEGINNER -> {
                    viewModel.getRest(db, AppConstants.BODY_WEIGHT, AppConstants.BEGINNER)
                    session.intensity = AppConstants.BEGINNER
                }
                AppConstants.INTERMEDIATE -> {
                    viewModel.getRest(db, AppConstants.BODY_WEIGHT, AppConstants.INTERMEDIATE)
                    session.intensity = AppConstants.INTERMEDIATE

                }
                AppConstants.ADVANCED -> {
                    viewModel.getRest(db, AppConstants.BODY_WEIGHT, AppConstants.ADVANCED)
                    session.intensity = AppConstants.ADVANCED

                }
            }
        }

    }

    private fun initialize() {
        instantiateDb()

        //setup ViewModel
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)
    }

    private fun instantiateDb() {
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "HasterDb.db")
                .build()
    }

    override fun onBackPressed() {
        startActivity(Intent(this, ActivityDashboard::class.java))
    }

    private fun openBugReportPageInBrowser() {
        val database: DatabaseReference = Firebase.database.reference
        val ref = database.child("urls").child("feedback")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val url = dataSnapshot.value.toString()
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(browserIntent)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                databaseError
                // ...
            }
        }
        ref.addValueEventListener(valueEventListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.setting_activity_menu, menu)
        return true
    }

    private fun setDayColor(day: String) {
        val dayTextViewId = getResId(day, R.id::class.java)
        val dayTextView = findViewById<TextView>(dayTextViewId)
        dayTextView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        dayTextView.typeface = Typeface.DEFAULT_BOLD
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.submit_bug -> openBugReportPageInBrowser()
        }
        return true
    }

    var stringArrayList: ArrayList<String> = ArrayList()
    private lateinit var touchHelper: ItemTouchHelper

    private fun populateRecyclerView() {
        stringArrayList.add("Item 1")
        stringArrayList.add("Item 2")
        stringArrayList.add("Item 3")
        stringArrayList.add("Item 4")
        stringArrayList.add("Item 5")
        stringArrayList.add("Item 6")
        stringArrayList.add("Item 7")
        val mAdapter = RecyclerViewAdapter(stringArrayList, this, this)
        val callback: ItemTouchHelper.Callback = ItemMoveCallback(mAdapter)
        touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(settings_recyclerView)
        settings_recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
    }


    private fun getResId(resName: String?, c: Class<*>): Int {
        return try {
            val idField: Field = c.getDeclaredField(resName!!)
            idField.getInt(idField)
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun requestDrag(viewHolder: RecyclerView.ViewHolder?) {
        if (viewHolder != null) {
            touchHelper.startDrag(viewHolder)
        };
    }


}