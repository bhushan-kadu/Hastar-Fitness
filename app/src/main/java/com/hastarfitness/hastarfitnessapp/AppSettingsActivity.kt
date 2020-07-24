package com.hastarfitness.hastarfitnessapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.diet.dietStartPages.DietStartPagesActivity
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import kotlinx.android.synthetic.main.activity_settings.*
import java.lang.reflect.Field
import java.util.*

class AppSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar!!.title = "Settings"

        val dailyPlan = LinkedHashMap<String, String>()
        val session = Session(this)

        val calenderDaysRadioGrp: RadioGroup = findViewById(R.id.calendarDayText)
        val intensityRadioGrp: RadioGroup = findViewById(R.id.exerciseIntensityRadio)
        val isCardioEnabledCbx = findViewById<CheckBox>(R.id.isCardioEnabled)

        val dayRadioId = getResId(session.day, R.id::class.java)
        calenderDaysRadioGrp.check(dayRadioId)

        val intensityRadioId = getResId(session.intensity!!.toLowerCase(), R.id::class.java)
        intensityRadioGrp.check(intensityRadioId)

        isCardioEnabledCbx.isChecked =  session.isCardioEnabled!!


        dailyPlan["monday"] = AppConstants.FULL_BODY
        dailyPlan["tuesday"] = AppConstants.CORE_STRENGTH
        dailyPlan["wednesday"] = AppConstants.UPPER_BODY
        dailyPlan["thursday"] = AppConstants.LOWER_BODY
        dailyPlan["friday"] = AppConstants.FULL_BODY
        dailyPlan["saturday"] = AppConstants.UPPER_BODY
        dailyPlan["sunday"] = AppConstants.CORE_STRENGTH




        save_prefrances.setOnClickListener {
            val checkedDayRadioBanId: Int = calenderDaysRadioGrp.checkedRadioButtonId
            val checkedDayRadioBtn = findViewById<RadioButton>(checkedDayRadioBanId)
            val day = checkedDayRadioBtn.text.toString()

            val intensityRadioBtnId: Int = intensityRadioGrp.checkedRadioButtonId
            val intensityRadioBtn = findViewById<RadioButton>(intensityRadioBtnId)
            val intensityString = intensityRadioBtn.text.toString()



            //set day change flag to true if day is changed
            if(session.day != day){
                session.isDayChanged = true
            }
            session.isCardioEnabled = isCardioEnabledCbx.isChecked
            session.intensity = intensityString
            session.day = day
            session.todaysWorkoutType = dailyPlan[day]



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

    private fun getResId(resName: String?, c: Class<*>): Int {
        return try {
            val idField: Field = c.getDeclaredField(resName!!)
            idField.getInt(idField)
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }
}