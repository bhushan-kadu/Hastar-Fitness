package com.hastarfitness.hastarfitnessapp.customDialogueToGetPlanNameInfo

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.*
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.Length
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import com.mobsandgeeks.saripaar.annotation.Select
import kotlinx.android.synthetic.main.dlg_get_custom_plan_info_dlg.*


/**
 * custom dialogue class for getting custom plan info to save the plan from user
 *
 * @author Bhushan Kadu
 */
class DlgGetCustPlanInfoFrmUser(ctx: Context) : Dialog(ctx), Validator.ValidationListener {

    lateinit var diaText: TextView
    lateinit var cancelBtn: Button
    lateinit var okBtn: Button
    var isSave = false

    var name = ""
    var desc = ""

    @NotEmpty
    @Length(max = 100, message = "Please Enter maximum 100 characters")
    lateinit var planName: EditText

    @Length(max= 280, message = "Please Enter maximum 280 characters")
    lateinit var planDesc: EditText
    lateinit var validator: Validator

    @Select(message = "Please Select Type")
    lateinit var workoutTypeSpinner: Spinner



    /**
     * function to create the dialogue
     */
    override fun create() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_get_custom_plan_info_dlg);
        planName = planNameEditText
        planDesc = planDescEditText

        validator = Validator(this)
        validator.setValidationListener(this)

        cancelBtn = cancel_action
        okBtn = ok_action
        workoutTypeSpinner = planType_spinner


        val types: MutableList<String> = ArrayList()
        types.add("Select Type")
        types.add(AppConstants.FULL_BODY.capitalize())
        types.add(AppConstants.UPPER_BODY.capitalize())
        types.add(AppConstants.CORE_STRENGTH.capitalize())
        types.add(AppConstants.LOWER_BODY.capitalize())


        // Creating adapter for spinner
        val dataAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, types)

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // attaching data adapter to spinner
        planType_spinner.adapter = dataAdapter

        okBtn.setOnClickListener {
            validator.validate()
        }
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {

        for (error in errors!!) {
            val view = error.view
            if (view is EditText) {
                view.error = error.getCollatedErrorMessage(context)
            } else if (view is Spinner) {
                (view.selectedView as TextView).error = error.getCollatedErrorMessage(context)
            }

        }
    }


    override fun onValidationSucceeded() {
        savePlan()
    }

    private fun savePlan() {
        isSave = true
        dismiss()
    }


}

