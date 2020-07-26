package com.hastarfitness.hastarfitnessapp.fitnessCalculators

import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.*
import com.hastarfitness.hastarfitnessapp.R
import com.xw.repo.BubbleSeekBar.CustomSectionTextArray
import kotlinx.android.synthetic.main.activity_b_m_i_calculator.*
import kotlinx.android.synthetic.main.activity_food_selected.*
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.floor
import kotlin.math.round

/**
 *  Activity for BMI calculator
 *
 *  @author Bhushan Kadu
 */
class BMICalculator : AppCompatActivity(), Validator.ValidationListener {
    private val roundingFormat = DecimalFormat("#.##")
    private var isCm = true
    private var iskg = true

    private lateinit var globalValidator: Validator

    @NotEmpty
    @DecimalMax(272.0, message = "Please Select maximum 272 cm")
    @DecimalMin(30.0, message = "Please Select minimum 30 cm")
    lateinit var cmInput: TextInputEditText

    @NotEmpty
    @Max(8, message = "Please Select maximum 8 ft")
    @Min(1, message = "Please Select minimum 1 ft")
    lateinit var ftInput: TextInputEditText

    @NotEmpty
    @DecimalMax(11.0, message = "Please Select maximum 12 in")
    @DecimalMin(0.0, message = "Please Select minimum 1 in")
    lateinit var inInput: TextInputEditText

    @NotEmpty
    @Max(100, message = "Please Select maximum 100")
    @Min(1, message = "Please Select minimum 1")
    lateinit var ageInput: TextInputEditText

    @NotEmpty
    @DecimalMax(450.0, message = "Please Select maximum 450 Kg")
    @DecimalMin(1.0, message = "Please Select minimum 1 Kg")
    lateinit var kgInput: TextInputEditText
    val fitnessCalculators = FitnessCalculators()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b_m_i_calculator)

        //set activity title
        supportActionBar!!.title = "BMI Calculator"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //init class
        init()

        toggle_kg_lb.setOnClickListener {  toggleKgLb() }
        toggle_ft_cm.setOnClickListener {toggleFtCm() }


        get_bmi_btn.setOnClickListener {
            //set validation
            globalValidator.validate();
        }

    }
    fun init() {
        roundingFormat.roundingMode = RoundingMode.CEILING

        cmInput = cm_input
        ftInput = ft_input
        inInput = in_input
        ageInput = age_input
        kgInput = kg_lb_input

        globalValidator = Validator(this);

        globalValidator.setValidationListener(this);

        bubbleSeekbar.setOnTouchListener{ _: View, _: MotionEvent -> true}
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> onBackPressed();
        }
        return true
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {

        for (error in errors!!) {
            (error.view as TextInputEditText).error = error.getCollatedErrorMessage(this)
        }

    }

    override fun onValidationSucceeded() {
        calculate()
    }

    private fun calculate() {
        var height: Double = 0.0
        var weight: Double = 0.0

        height = if (isCm) {
            cm_input.text.toString().toDouble()
        } else {
            val ft = ft_input.text.toString()
            val inch = in_input.text.toString()

            ftInToCm("$ft $inch")
        }

        weight = if (iskg) {
            kg_lb_input.text.toString().toDouble()
        } else {
            lbToKg(kg_lb_input.text.toString().toDouble())
        }

        val bmi = fitnessCalculators.calculateBMIMetric(height, weight)

        bmi_result_textView.text = "Your BMI is ${round(bmi)}"

        showSeekbar(bmi)

        setResultTextAndSeekbarVisiblity()
    }

    /**
     * function to show seekbar
     *
     * @param bmi bmi value to be represent on seekbar
     */
    private fun showSeekbar(bmi:Double){
        var color = ContextCompat.getColor(this, R.color.green)

        if(bmi < 18.5 || bmi > 30){
            color = ContextCompat.getColor(this, R.color.pestalRed)
        }else if(bmi >= 18.5 &&  bmi < 25){
            color = ContextCompat.getColor(this, R.color.green)
        }else if(bmi >= 25 &&  bmi < 30){
            color = ContextCompat.getColor(this, R.color.yellow)
        }

        bubbleSeekbar.setBubbleColor(color)
        bubbleSeekbar.setTrackColor(color)
        bubbleSeekbar.setThumbColor(color)
        bubbleSeekbar.setSecondTrackColor(color)
        bubbleSeekbar.setProgress(bmi.toFloat())

        val v = bubbleSeekbar
        v.configBuilder.alwaysShowBubble().build()
        initBubbleSeeker()
    }

    private fun toggleFtCmEditTextValues() {

        if (!isCm) {
            val ftValueString = ftInput.text.toString()
            val inValueString = inInput.text.toString()
            val ftValue =  if(ftValueString == "") 0 else ftValueString.toInt()
            val inValue =  if(inValueString == "") 0 else inValueString

            cm_input.setText(fitnessCalculators.ftInToCm("$ftValue $inValue").toString())
            cm_input.setSelection(cm_input.text!!.length)
        } else {
            val cmValueString = cmInput.text.toString()
            val cmValue =  if(cmValueString == "") 0.toDouble() else cmValueString.toDouble()

            val split = fitnessCalculators.cmToftIn(cmValue).split(" ")
            val ft = split[0].toDouble().toInt()
            val inch = split[1].toDouble()
            ft_input.setText(ft.toString())
            in_input.setText(roundingFormat.format(inch))

            ft_input.setSelection(ft_input.text!!.length)
            in_input.setSelection(in_input.text!!.length)
        }
    }

    private fun toggleKgLbEditTextValues() {
        val valueString = kg_lb_input.text.toString()
        val value =  if(valueString == "") 0.toDouble() else valueString.toDouble()

        if (!iskg) {
            kg_lb_input.setText(fitnessCalculators.lbToKg(value).toString())
            kg_lb_input.setSelection(kg_lb_input.text!!.length)
        } else {
            kg_lb_input.setText(fitnessCalculators.kgToLb(value).toString())
            kg_lb_input.setSelection(kg_lb_input.text!!.length)
        }
    }


    private fun initBubbleSeeker(){
        bubbleSeekbar.setCustomSectionTextArray(CustomSectionTextArray { sectionCount, array ->
            array.clear()
            array.put(1, "Underweight")
            array.put(4, "Normal")
            array.put(6, "Overweight")
            array.put(9, "Obesity")
            array
        })
    }

    private fun setResultTextAndSeekbarVisiblity(){
        bmi_result_textView.visibility = View.VISIBLE
        bubbleSeekbar.visibility = View.VISIBLE
        info_textView.visibility = View.VISIBLE
    }

    private fun kgToLb(kg: Double): Double {
        return round(kg * 2.20462)
    }

    private fun lbToKg(lb: Double): Double {
        return round(lb / 2.20462)
    }

    private fun ftInToCm(ftIn: String): Double {
        val split = ftIn.split(" ")
        val ft = split[0].toDouble()
        val inch = split[1].toDouble()
        return round((ft * 30.48) + (inch * 2.54))
    }

    private fun cmToftIn(cm: Double): String {

        var ft: Double = 0.0328 * cm
        var inch = ((ft % floor(ft)) * 12)
        if (inch.isNaN()) {
            inch = round(cm * 0.393701)
        }

        return "${round(ft)} ${round(inch)}"

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
        toggleKgLbEditTextValues()
        iskg = !iskg

        if (iskg) {
            weight_input.hint = "weight(kg)"
            toggle_kg_lb.text = "change to lb"
        } else {
            weight_input.hint = "weight(lb)"
            toggle_kg_lb.text = "change to kg"
        }
    }

}