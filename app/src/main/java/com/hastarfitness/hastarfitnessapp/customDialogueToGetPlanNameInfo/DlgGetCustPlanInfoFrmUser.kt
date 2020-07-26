package com.hastarfitness.hastarfitnessapp.customDialogueToGetPlanNameInfo

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.createYourOwnPlan.ExercisesListAdapter
import com.hastarfitness.hastarfitnessapp.database.PlanExercisesDbModel
import com.hastarfitness.hastarfitnessapp.database.WorkoutPlansDbModel
import com.hastarfitness.hastarfitnessapp.selectPlanForDailyWorkout.SelectPlanForDailyWorkoutActivity
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.Length
import com.mobsandgeeks.saripaar.annotation.Max
import com.mobsandgeeks.saripaar.annotation.Min
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import kotlinx.android.synthetic.main.activity_create_your_plan.*
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

        okBtn.setOnClickListener {
            validator.validate()
        }
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
        for (error in errors!!) {
                (error.view as EditText).error = error.getCollatedErrorMessage(context)
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

