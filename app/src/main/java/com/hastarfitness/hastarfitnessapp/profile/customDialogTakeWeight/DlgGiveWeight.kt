package com.hastarfitness.hastarfitnessapp.profile.customDialogTakeWeight

import android.app.Dialog
import android.view.View
import android.view.Window
import com.github.mikephil.charting.data.Entry
import com.google.android.material.textfield.TextInputEditText
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.DecimalMax
import com.mobsandgeeks.saripaar.annotation.DecimalMin
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.profile.FitnessDataFragment
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import kotlinx.android.synthetic.main.dlg_give_weight.*
import java.util.*


/**
 * Custom Dialogue for showing exercise desc to user
 *
 * @author Bhushan Kadu
 */
class DlgGiveWeight(private val ctx: FitnessDataFragment) : Dialog(ctx.requireContext()), View.OnClickListener,
        Validator.ValidationListener {

    private lateinit var globalValidator: Validator
    @NotEmpty
    @DecimalMin(16.0)
    @DecimalMax(450.0)
    lateinit var weightInput: TextInputEditText
    lateinit var horizontalCalendar: HorizontalCalendar
    var entry:Entry? = null
    var date:Date? = null
    var isValueChanged = false
    override fun create() {
        super.create()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dlg_give_weight)

        //init
        initialize()

        /* starts before 1 month from now */
        val startDate = Calendar.getInstance()
        startDate.add(Calendar.MONTH, -3)

        /* ends after 1 month from now */
        val endDate = Calendar.getInstance()
        endDate.add(Calendar.DAY_OF_MONTH, 0)
        endDate.add(Calendar.MONTH, 0)

        horizontalCalendar= HorizontalCalendar.Builder(rootView, R.id.calender_view)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .build()
        horizontalCalendar.goToday( true)

        calenderLayout.visibility = View.VISIBLE

        horizontalCalendar.calendarListener = object : HorizontalCalendarListener() {
            override fun onDateSelected(date: Calendar?, position: Int) {
                //do something
            }
        }
    }

    private fun initialize() {

        //initialize
        globalValidator = Validator(this);

        //set validation listener
        globalValidator.setValidationListener(this);

        weightInput = weightEditText

        cancel_btn.setOnClickListener(this)
        save_btn.setOnClickListener(this)
        today_btn.setOnClickListener(this)
    }

    private fun saveBtnClick(){
        isValueChanged = true
        val dayOfYear = horizontalCalendar.selectedDate[Calendar.DAY_OF_YEAR]
        date = horizontalCalendar.selectedDate.time
        val weight = weightEditText.text.toString().toFloat()
        entry = Entry(dayOfYear.toFloat(), weight)
        dismiss()
    }
    //implement clicks
    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.cancel_btn -> {
                dismiss()
            }
            R.id.save_btn -> {
                globalValidator.validate()
            }
            R.id.today_btn ->{
                calenderLayout.visibility = View.GONE
                /* ends after 1 month from now */
                val endDate = Calendar.getInstance()
//                endDate.add(Calendar.DAY_OF_MONTH, 0)
//                endDate.add(Calendar.MONTH, 0)
//                horizontalCalendar.goToday(true)
                val pos = horizontalCalendar.positionOfDate(endDate)
                horizontalCalendar.centerCalendarToPosition(pos)
//                horizontalCalendar.selectDate(endDate, false)
                calenderLayout.visibility = View.VISIBLE
            }
        }
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
        for (error in errors!!) {
            val view = error.view
            if (view is TextInputEditText) {
                view.error = "please fill correct value"
            }
        }
    }

    override fun onValidationSucceeded() {
        saveBtnClick()
    }
}

