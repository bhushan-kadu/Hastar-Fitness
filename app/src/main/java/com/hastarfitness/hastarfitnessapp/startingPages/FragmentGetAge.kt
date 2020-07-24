package com.hastarfitness.hastarfitnessapp.startingPages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hastarfitness.hastarfitnessapp.R
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.util.*

class FragmentGetAge : Fragment(), DatePickerDialog.OnDateSetListener {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_get_age, container, false)
        val bDayButton = rootView.findViewById<Button>(R.id.setBirthday_Btn)
        val parentActivity = activity as ActivityStartPages

        bDayButton.setOnClickListener {
            val now = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog.newInstance(
                    this,
                    now[Calendar.YEAR],  // Initial year selection
                    now[Calendar.MONTH],  // Initial month selection
                    now[Calendar.DAY_OF_MONTH] // Initial day selection
            )
            datePickerDialog.show(parentActivity.supportFragmentManager, "Datepickerdialog")
        }

        return rootView
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calInstance = Calendar.getInstance()
        val fiveYearBackYear = (calInstance[Calendar.YEAR] - 5)
        if(year < fiveYearBackYear){
            val age = Calendar.getInstance()[Calendar.YEAR] - year
            val parentActivity = activity as ActivityStartPages
            parentActivity.age = age
            parentActivity.viewPager.currentItem = parentActivity.viewPager.currentItem + 1
        }else{
            Toast.makeText(context, "Minimum age limit is five years", Toast.LENGTH_LONG).show()
        }

    }
}