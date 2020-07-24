package com.hastarfitness.hastarfitnessapp.fitnessCalculators

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatTextView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import com.hastarfitness.hastarfitnessapp.R
import kotlinx.android.synthetic.main.activity_macro_calculator.*
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.DecimalMax
import com.mobsandgeeks.saripaar.annotation.DecimalMin
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.pieChart.MyValueFormatter
import kotlin.math.round

/**
 * class for macro calculator activity
 */
class MacroCalculator : AppCompatActivity(), Validator.ValidationListener {

    private lateinit var colors:ArrayList<Int>

    val fitnessCalculators = FitnessCalculators()

    private var proteinRatio = 0
    private var carbsRatio = 0
    private var fatRatio = 0
    private var caloriesSelected = 0.0

    @NotEmpty
    lateinit var dietType:AutoCompleteTextView

    @NotEmpty
    @DecimalMax(3000.0, message = "Please Select maximum 3000")
    @DecimalMin(1000.0, message = "Please Select minimum 1000")
    lateinit var calories: TextInputEditText

    private lateinit var validator: Validator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_macro_calculator)

        //set activity title
        supportActionBar!!.title = "Macro Calculator"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        dietType = diet
        dietType.inputType = InputType.TYPE_NULL
        calories = calorieEditText

        initialize()

        //do this for initial pie chart view
        proteinRatio = 60
        carbsRatio = 25
        fatRatio = 15
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            dietType.setText("High Carbs", false)
        }
        val values = calcMacros()
        setPieChart(values, true)


        diet.setOnItemClickListener { _, view, _, _ ->
            val dropDown = (view as AppCompatTextView)
            when (dropDown.text.toString().toLowerCase()) {
                "high carbs" -> {
                  proteinRatio = 60
                    carbsRatio = 25
                    fatRatio = 15
                }
                "moderate carbs" -> {
                    proteinRatio = 50
                    carbsRatio = 30
                    fatRatio = 20
                }
                "zone diet" -> {
                    proteinRatio = 40
                    carbsRatio = 30
                    fatRatio = 30
                }
                "low carbs" -> {
                    proteinRatio = 25
                    carbsRatio = 35
                    fatRatio = 40
                }
                "keto diet" -> {
                    proteinRatio = 5
                    carbsRatio = 35
                    fatRatio = 60
                }
            }

            dietTypeTextView.error = null

        }

        get_macros_btn.setOnClickListener { validator.validate() }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> onBackPressed();
        }
        return true
    }

    private fun initialize() {
        val items = listOf("High Carbs", "Moderate Carbs", "Zone Diet", "Low Carbs", "Keto Diet")
        val adapter = ArrayAdapter(this, R.layout.single_list_item, items)
        (dietTypeTextView.editText as? AppCompatAutoCompleteTextView)?.setAdapter(adapter)

        validator = Validator(this)
        validator.setValidationListener(this)

        val MY_COLORS = intArrayOf( Color.rgb(255, 0, 0), Color.rgb(255,210,0),
                Color.rgb (255, 165, 0))
        colors = ArrayList<Int>()

        for (c in MY_COLORS) colors.add(c)

        pieChart.isRotationEnabled = false
        pieChart.transparentCircleRadius = 55f
        pieChart.animateXY(700, 700, Easing.EaseInOutQuad );
        pieChart.legend.isEnabled = false
        pieChart.description.isEnabled = false
        pieChart.invalidate() // refresh
    }
    private fun calcMacros():HashMap<String, Double>{
        val cals = calories.text.toString().toInt()

        return fitnessCalculators.macroCalc(cals, proteinRatio, carbsRatio, fatRatio)
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
        for (error in errors!!) {
            val view = error.view
            if (view is TextInputEditText) {
                (error.view as TextInputEditText).error = error.getCollatedErrorMessage(this)
            } else if (view is MaterialAutoCompleteTextView) {
                view.error = "please enter this field correctly"
            }
        }
    }

    override fun onValidationSucceeded() {
        val values = calcMacros()
        setPieChart(values, false)
    }

    private fun setPieChart(macros: HashMap<String, Double>, isFirstType: Boolean){
        var pieEntries:MutableList<PieEntry>  = ArrayList()
        val protein = round(macros[AppConstants.PROTEIN]!!.toFloat())
        val carbs = round(macros[AppConstants.CARBS]!!.toFloat())
        val fats = round(macros[AppConstants.FAT]!!.toFloat())

        pieEntries.add(PieEntry(protein, "Protein"))
        pieEntries.add(PieEntry(carbs, "carb"))
        pieEntries.add(PieEntry(fats, "fat"))

        val set = PieDataSet(pieEntries, "Macros")
        set.colors = colors
        set.valueTextSize = 15f
        set.valueTextColor = Color.WHITE
        set.sliceSpace = 5f
        set.valueFormatter = MyValueFormatter()
        val data = PieData(set)
        pieChart.data = data

        //set  pie chart text
        caloriesSelected = calories.text.toString().toDouble()
        caloriesTextView.text =    "$caloriesSelected Kcal"


        if(!isFirstType)pieChart.animateXY(500, 500, Easing.EaseInOutQuad );
    }
}