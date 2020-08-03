package com.hastarfitness.hastarfitnessapp.settings

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hastarfitness.hastarfitnessapp.ActivityDashboard
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.diet.dietStartPages.DietStartPagesActivity
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import kotlinx.android.synthetic.main.activity_settings.*
import java.lang.reflect.Field
import java.util.*

class AppSettingsActivity : AppCompatActivity(), StartDragListener {
    var day = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar!!.title = "Settings"

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val session = Session(this)
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


            session.isCardioEnabled = isCardioEnabledCbx.isChecked
            session.intensity = intensityString

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
    }
    private fun setDayColor(day:String){
        val dayTextViewId = getResId(day,  R.id::class.java)
        val dayTextView = findViewById<TextView>(dayTextViewId)
        dayTextView.setTextColor(ContextCompat.getColor(this, R.color.green))
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
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