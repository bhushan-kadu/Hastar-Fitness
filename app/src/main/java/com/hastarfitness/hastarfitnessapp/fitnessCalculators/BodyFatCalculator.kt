package com.hastarfitness.hastarfitnessapp.fitnessCalculators

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.mobsandgeeks.saripaar.ValidationError
import com.hastarfitness.hastarfitnessapp.R
import kotlinx.android.synthetic.main.activity_body_fat_calculator.*
import kotlinx.android.synthetic.main.activity_body_fat_calculator.feet_inches_view
import kotlinx.android.synthetic.main.activity_body_fat_calculator.toggle_ft_cm
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.*
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import kotlinx.android.synthetic.main.activity_b_m_i_calculator.*
import kotlinx.android.synthetic.main.activity_bmr_calculator.*
import kotlinx.android.synthetic.main.activity_bmr_calculator.cm_input
import kotlinx.android.synthetic.main.activity_body_fat_calculator.cmInputEditText
import kotlinx.android.synthetic.main.activity_body_fat_calculator.cmInputLayout
import kotlinx.android.synthetic.main.activity_body_fat_calculator.feetInputEditText
import kotlinx.android.synthetic.main.activity_body_fat_calculator.genderInputLayout
import kotlinx.android.synthetic.main.activity_body_fat_calculator.genderInput_textView
import kotlinx.android.synthetic.main.activity_body_fat_calculator.hipInputEditText
import kotlinx.android.synthetic.main.activity_body_fat_calculator.hipInputLayout
import kotlinx.android.synthetic.main.activity_body_fat_calculator.inInputEditText
import kotlinx.android.synthetic.main.activity_body_fat_calculator.neakInputEditText
import kotlinx.android.synthetic.main.activity_body_fat_calculator.waistInputEditText
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.round

/**
 *  Activity for BMI calculator
 */

class BodyFatCalculator : AppCompatActivity(), Validator.ValidationListener {
    private val roundingFormat = DecimalFormat("#.##")
    private var isCm = true
    var fitnessCalculators = FitnessCalculators()

    private lateinit var validator: Validator

    @NotEmpty
    @DecimalMax(272.0, message = "Please Select maximum 272 cm")
    @DecimalMin(30.0, message = "Please Select minimum 30 cm")
    lateinit var cmInput: TextInputEditText

    @NotEmpty
    @Max(8, message = "Please Select maximum 8 ft")
    @Min(1, message = "Please Select minimum 1 ft")
    lateinit var ftInput: TextInputEditText

    @NotEmpty
    @DecimalMax(12.0, message = "Please Select maximum 12 in")
    @DecimalMin(1.0, message = "Please Select minimum 1 in")
    lateinit var inInput: TextInputEditText

    @NotEmpty
    lateinit var genderInput: AutoCompleteTextView

    @NotEmpty
    @DecimalMax(100.0, message = "Please Select maximum 100 in")
    @DecimalMin(30.0, message = "Please Select minimum 30 in")
    lateinit var waistInput: TextInputEditText

    @NotEmpty
    @DecimalMax(150.0, message = "Please Select maximum 150 in")
    @DecimalMin(30.0, message = "Please Select minimum 30 in")
    lateinit var hipInput: TextInputEditText

    @NotEmpty
    @DecimalMax(20.0, message = "Please Select maximum 20 in")
    @DecimalMin(5.0, message = "Please Select minimum 5 in")
    lateinit var neckInput: TextInputEditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_fat_calculator)

        //set activity title
        supportActionBar!!.title = "Body Fat Calculator"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //init class
        initialize()

        //initialize female in dropdown for simplicity
        genderInput.setText(AppConstants.FEMALE, false)
        setFemaleVisibility()

        toggle_ft_cm.setOnClickListener { toggleFtCm() }

        genderInput.setOnItemClickListener { parent, view, position, id ->

            val dropDown = (view as AppCompatTextView)
            when (dropDown.text) {
                AppConstants.FEMALE -> {
                    setMaleVisibility()
                }
                AppConstants.MALE -> {
                    setFemaleVisibility()
                }
            }
            genderInput.error = null

        }

        get_bf_btn.setOnClickListener {
            validator.validate()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> onBackPressed();
        }
        return true
    }
    private fun initialize() {
        roundingFormat.roundingMode = RoundingMode.CEILING
        cmInput = cmInputEditText
        ftInput = feetInputEditText
        inInput = inInputEditText
        genderInput = genderInput_textView
        genderInput.inputType = InputType.TYPE_NULL
        waistInput = waistInputEditText
        hipInput = hipInputEditText
        neckInput = neakInputEditText

        val items = listOf(AppConstants.FEMALE, AppConstants.MALE)
        val adapter = ArrayAdapter(this, R.layout.single_list_item, items)
        (genderInputLayout.editText as? AppCompatAutoCompleteTextView)?.setAdapter(adapter)

        validator = Validator(this)
        validator.setValidationListener(this)

    }

    private fun calculateFatPercentage(): Double {
        var height = 0.0
        var waist = 0.0
        var neak = 0.0
        var hip = 0.0
        var result = 0.0
        if (isCm) {
            height = cmInput.text.toString().toDouble() * 0.393701
        } else {
            height = ftInput.text.toString().toDouble() * 12 + inInput.text.toString().toDouble()
        }
        waist = waistInput.text.toString().toDouble()
        neak = neckInput.text.toString().toDouble()

        if (genderInput.text.toString().equals("Male")) {
            result = fitnessCalculators.bodyFatMen(height, waist, neak)
        } else {
            hip = hipInput.text.toString().toDouble()
            result = fitnessCalculators.bodyFatWomen(height, waist, hip, neak)
        }

        return round(result)
    }


    private fun setMaleVisibility() {

        hipInputLayout.visibility = View.GONE
    }

    private fun setFemaleVisibility() {

        hipInputLayout.visibility = View.VISIBLE
    }

    private fun toggleFtCm() {
        toggleFtCmEditTextValues()
        isCm = !isCm
        if (isCm) {
            cmInputLayout.visibility = View.VISIBLE
            feet_inches_view.visibility = View.GONE
            toggle_ft_cm.text = "change to ft"
        } else {
            feet_inches_view.visibility = View.VISIBLE
            cmInputLayout.visibility = View.GONE
            toggle_ft_cm.text = "change to cm"
        }

    }

    private fun toggleFtCmEditTextValues() {

        if (!isCm) {
            val ftValueString = ftInput.text.toString()
            val inValueString = inInput.text.toString()
            val ftValue = if (ftValueString == "") 0 else ftValueString.toInt()
            val inValue = if (inValueString == "") 0 else inValueString

            cmInput.setText(fitnessCalculators.ftInToCm("$ftValue $inValue").toString())
            cmInput.setSelection(cmInput.text!!.length)
        } else {
            var ft = 0
            var inch = 0.0
            val cmValueString = cmInput.text.toString()
            val cmValue = if (cmValueString == "") 0.toDouble() else cmValueString.toDouble()
            if (cmValue != 0.toDouble()) {
                val split = fitnessCalculators.cmToftIn(cmValue).split(" ")
                ft = split[0].toDouble().toInt()
                inch = split[1].toDouble()
            }
            ftInput.setText(ft.toString())
            inInput.setText(roundingFormat.format(inch))

            ftInput.setSelection(ftInput.text!!.length)
            inInput.setSelection(inInput.text!!.length)
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
        fp_result_textView.text = calculateFatPercentage().toString() + "%"
    }

}
