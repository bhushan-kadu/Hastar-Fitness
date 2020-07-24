package com.hastarfitness.hastarfitnessapp.fitnessCalculators

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.*
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import kotlinx.android.synthetic.main.activity_bmr_calculator.*
import kotlinx.android.synthetic.main.activity_bmr_calculator.age_input
import kotlinx.android.synthetic.main.activity_bmr_calculator.feet_inches_view
import kotlinx.android.synthetic.main.activity_bmr_calculator.genderInputLayout
import kotlinx.android.synthetic.main.activity_bmr_calculator.kg_lb_input
import kotlinx.android.synthetic.main.activity_bmr_calculator.toggle_ft_cm
import kotlinx.android.synthetic.main.activity_bmr_calculator.toggle_kg_lb
import kotlinx.android.synthetic.main.activity_bmr_calculator.weight_input

/**
 *  Activity for BMI calculator
 */
class BMRCalculator : AppCompatActivity(), Validator.ValidationListener {
    private var isCm = true
    private var iskg = true
    val fitnessCalculators = FitnessCalculators()

    @NotEmpty
    lateinit var activityInput: AutoCompleteTextView

    @NotEmpty
    lateinit var genderInput: AutoCompleteTextView

    @NotEmpty
    @DecimalMax(272.0, message = "Please Select maximum 272 cm")
    @DecimalMin(30.0, message = "Please Select minimum 30 cm")
    lateinit var cmInput: TextInputEditText

    @NotEmpty
    @Max(8, message = "Please Select maximum 8 ft")
    @Min(1, message = "Please Select minimum 1 ft")
    lateinit var ftInput: TextInputEditText

    @NotEmpty
    @Max(12, message = "Please Select maximum 12 in")
    @Min(1, message = "Please Select minimum 1 in")
    lateinit var inInput: TextInputEditText

    @NotEmpty
    @Max(100, message = "Please Select maximum 100")
    @Min(1, message = "Please Select minimum 1")
    lateinit var ageInput: TextInputEditText

    @NotEmpty
    @DecimalMax(450.0, message = "Please Select maximum 450 Kg")
    @DecimalMin(1.0, message = "Please Select minimum 1 Kg")
    lateinit var kgInput: TextInputEditText

    @NotEmpty
    @DecimalMax(70.0, message = "Please Select maximum 70%")
    @DecimalMin(1.0, message = "Please Select minimum 1%")
    lateinit var bodyFat: TextInputEditText

    lateinit var validator: Validator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmr_calculator)

        //set activity title
        supportActionBar!!.title = "BMR Calculator"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        //init class
        initialize()

        toggle_kg_lb.setOnClickListener { toggleKgLb() }
        toggle_ft_cm.setOnClickListener { toggleFtCm() }
        get_bmr_btn.setOnClickListener { validator.validate() }

        //clear the errors as user selects any items from dropdown
        genderInput.setOnClickListener {
            genderInputLayout.error = null
        }

        activity_Input.setOnClickListener {
            activityInputLayout.error = null
        }

    }

    private fun initialize(){
        activityInput = activity_Input
        activity_Input.inputType = InputType.TYPE_NULL
        genderInput = genderInput_textView
        genderInput.inputType = InputType.TYPE_NULL
        cmInput = cm_input
        ftInput = ft_input
        inInput = in_input
        ageInput = age_input
        kgInput = kg_lb_input
        bodyFat = bodyFatInput

        validator = Validator(this)
        validator.setValidationListener(this)

        //initialize male/female and intensity of workout dropdown
        var items = listOf(AppConstants.FEMALE, AppConstants.MALE)
        var adapter = ArrayAdapter(this, R.layout.single_list_item, items)
        (genderInputLayout.editText as? AppCompatAutoCompleteTextView)?.setAdapter(adapter)

        items = listOf(AppConstants.LITTLE_OR_NO_EXERCISE,
                AppConstants.LIGHT_EXERCISE,
                AppConstants.MODERATE_EXERCISE,
                AppConstants.HARD_EXERCISE,
                AppConstants.VERY_HARD_EXERCISE)
        adapter = ArrayAdapter(this, R.layout.single_list_item, items)
        (activityInputLayout.editText as? AppCompatAutoCompleteTextView)?.setAdapter(adapter)

        //set initial dropdown for simplicity
        genderInput.setText(AppConstants.FEMALE, false)
        activity_Input.setText(AppConstants.MODERATE_EXERCISE, false)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> onBackPressed();
        }
        return true
    }

    private fun toggleFtCm() {
        toggleFtCmEditTextValues()
        isCm = !isCm
        if (isCm) {
            cm_view.visibility = View.VISIBLE
            feet_inches_view.visibility = View.GONE
            toggle_ft_cm.text = "change to ft"
        } else {
            feet_inches_view.visibility = View.VISIBLE
            cm_view.visibility = View.GONE
            toggle_ft_cm.text = "change to cm"
        }

    }

    private fun toggleKgLb() {
        togglekgLbEditTextValues()
        iskg = !iskg

        if (iskg) {
            weight_input.hint = "weight(kg)"
            toggle_kg_lb.text = "change to lb"
        } else {
            weight_input.hint = "weight(lb)"
            toggle_kg_lb.text = "change to kg"
        }
    }

    private fun toggleFtCmEditTextValues() {

        if (!isCm) {
            val ftValueString = ft_input.text.toString()
            val inValueString = in_input.text.toString()
            val ftValue = if (ftValueString == "") 0 else ftValueString.toInt()
            val inValue = if (inValueString == "") 0 else inValueString.toInt()

            cm_input.setText(fitnessCalculators.ftInToCm("$ftValue $inValue").toString())
        } else {
            var ft = 0
            var inch = 0
            val cmValueString = cm_input.text.toString()
            val cmValue = if (cmValueString == "") 0.toDouble() else cmValueString.toDouble()
            if (cmValue != 0.toDouble()) {
                val split = fitnessCalculators.cmToftIn(cmValue).split(" ")
                ft = split[0].toDouble().toInt()
                inch = split[1].toDouble().toInt()
            }
            ft_input.setText(ft.toString())
            in_input.setText(inch.toString())
        }
    }

    private fun togglekgLbEditTextValues() {
        val valueString = kg_lb_input.text.toString()
        val value = if (valueString == "") 0.toDouble() else valueString.toDouble()

        if (!iskg) {
            kg_lb_input.setText(fitnessCalculators.lbToKg(value).toString())
        } else {
            kg_lb_input.setText(fitnessCalculators.kgToLb(value).toString())
        }
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
        for (error in errors!!) {
            val view = error.view
            if (view is TextInputEditText) {
                (error.view as TextInputEditText).error = error.getCollatedErrorMessage(this)
            } else if (view is MaterialAutoCompleteTextView) {
                view.error = "please enter this field"
            }
        }
    }

    override fun onValidationSucceeded() {

        val height = if(isCm){
            cmInput.text.toString().toDouble()
        }else{
            fitnessCalculators.ftInToCm("$inInput $ftInput")
        }
        val weight = if(iskg){
            kgInput.text.toString().toDouble()
        }else{
            fitnessCalculators.lbToKg(kgInput.text.toString().toDouble())
        }
        val age = ageInput.text.toString().toInt()
        val isMale = genderInput.text.toString() == AppConstants.MALE
        val bmr = fitnessCalculators.calculateBMRMetric(height, weight, age, isMale)
        val tdee = fitnessCalculators.calculateTDEE(bmr, activityInput.text.toString())

        bmr_result_textView.text = "Your BMR is $bmr"
        tdee_result_textView.text = "Your TDEE is $tdee"
    }

}