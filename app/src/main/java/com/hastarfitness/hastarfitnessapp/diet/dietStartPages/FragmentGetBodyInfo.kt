package com.hastarfitness.hastarfitnessapp.diet.dietStartPages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.*
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.fitnessCalculators.FitnessCalculators
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import kotlinx.android.synthetic.main.fragment_diet_get_body_info.*
import kotlinx.android.synthetic.main.fragment_diet_get_body_info.cm_input
import kotlinx.android.synthetic.main.fragment_diet_get_body_info.feet_inches_view
import kotlinx.android.synthetic.main.fragment_diet_get_body_info.ft_input
import kotlinx.android.synthetic.main.fragment_diet_get_body_info.in_input
import java.math.RoundingMode
import java.text.DecimalFormat

class FragmentGetBodyInfo : Fragment(), Validator.ValidationListener {
    private val roundingFormat = DecimalFormat("#.##")
    @NotEmpty
    @DecimalMax(450.0, message = "Please Select maximum 450 Kg")
    @DecimalMin(1.0, message = "Please Select minimum 1 Kg")
    lateinit var wtInput: EditText

    @NotEmpty
    @Max(8, message = "Please Select maximum 8 ft")
    @Min(1, message = "Please Select minimum 1 ft")
    lateinit var ftInput: EditText

    @NotEmpty
    @DecimalMax(12.0, message = "Please Select maximum 12 in")
    @DecimalMin(1.0, message = "Please Select minimum 1 in")
    lateinit var inInput: EditText

    @NotEmpty
    @DecimalMax(272.0, message = "Please Select maximum 272 cm")
    @DecimalMin(30.0, message = "Please Select minimum 30 cm")
    lateinit var cmInput: EditText

    @NotEmpty
    @Max(100, message = "Please Select maximum 100")
    @Min(1, message = "Please Select minimum 1")
    lateinit var ageInput: EditText

    lateinit var validator: Validator

    var weeklyActivity = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_diet_get_body_info, container, false)

        initialize()
        val ftButton = rootView.findViewById<Button>(R.id.ft_button)
        val cmButton = rootView.findViewById<Button>(R.id.cm_button)

        val kgButton = rootView.findViewById<Button>(R.id.kg_button)
        val lbButton = rootView.findViewById<Button>(R.id.lb_button)

        val maleButton = rootView.findViewById<Button>(R.id.maleBtn)
        val femaleButton = rootView.findViewById<Button>(R.id.femaleBtn)


        wtInput = rootView.findViewById(R.id.wt_input)
        ftInput = rootView.findViewById(R.id.ft_input)
        inInput = rootView.findViewById(R.id.in_input)
        cmInput = rootView.findViewById(R.id.cm_input)
        ageInput = rootView.findViewById(R.id.age_input)

        val session = Session(activity as DietStartPagesActivity)
        wtInput.setText(session.weightInKg.toString())
        cmInput.setText(session.heightCm.toString())
        ageInput.setText(session.age.toString())


        val veryLittleButton = rootView.findViewById<Button>(R.id.veryLittle)
        val oneToThreeButton = rootView.findViewById<Button>(R.id.onetoThree)
        val threeToFiveButton = rootView.findViewById<Button>(R.id.threetofive)
        val sixToSevenButton = rootView.findViewById<Button>(R.id.sixToSeven)
        val veryHardButton = rootView.findViewById<Button>(R.id.veryHard)

        val blackColor = ContextCompat.getColor(requireContext(), R.color.gray)
        val whiteColor = ContextCompat.getColor(requireContext(), android.R.color.white)
        val selectedBackGround = ContextCompat.getDrawable(requireContext(), R.drawable.diet_buttons_background_selected)
        val unSelectedBackGround = ContextCompat.getDrawable(requireContext(), R.drawable.diet_unselected_faint_border)

        weeklyActivity = AppConstants.LIGHT_EXERCISE


        maleButton.setOnClickListener {
            maleButton.background = selectedBackGround
            femaleButton.background = unSelectedBackGround

            maleButton.setTextColor(whiteColor)
            femaleButton.setTextColor(blackColor)
            isMale = true
        }

        femaleButton.setOnClickListener {
            maleButton.background = unSelectedBackGround
            femaleButton.background = selectedBackGround

            maleButton.setTextColor(blackColor)
            femaleButton.setTextColor(whiteColor)
            isMale = false
        }

        veryLittleButton.setOnClickListener {
            veryLittleButton.background = selectedBackGround
            veryLittleButton.setTextColor(whiteColor)
            oneToThreeButton.background = unSelectedBackGround
            oneToThreeButton.setTextColor(blackColor)
            threeToFiveButton.background = unSelectedBackGround
            threeToFiveButton.setTextColor(blackColor)
            sixToSevenButton.background = unSelectedBackGround
            sixToSevenButton.setTextColor(blackColor)
            veryHardButton.background = unSelectedBackGround
            veryHardButton.setTextColor(blackColor)

            weeklyActivity = AppConstants.LITTLE_OR_NO_EXERCISE
        }
        oneToThreeButton.setOnClickListener {
            oneToThreeButton.background = selectedBackGround
            oneToThreeButton.setTextColor(whiteColor)
            veryLittleButton.background = unSelectedBackGround
            veryLittleButton.setTextColor(blackColor)
            threeToFiveButton.background = unSelectedBackGround
            threeToFiveButton.setTextColor(blackColor)
            sixToSevenButton.background = unSelectedBackGround
            sixToSevenButton.setTextColor(blackColor)
            veryHardButton.background = unSelectedBackGround
            veryHardButton.setTextColor(blackColor)

            weeklyActivity = AppConstants.LIGHT_EXERCISE
        }
        threeToFiveButton.setOnClickListener {
            threeToFiveButton.background = selectedBackGround
            threeToFiveButton.setTextColor(whiteColor)
            veryLittleButton.background = unSelectedBackGround
            veryLittleButton.setTextColor(blackColor)
            oneToThreeButton.background = unSelectedBackGround
            oneToThreeButton.setTextColor(blackColor)
            sixToSevenButton.background = unSelectedBackGround
            sixToSevenButton.setTextColor(blackColor)
            veryHardButton.background = unSelectedBackGround
            veryHardButton.setTextColor(blackColor)

            weeklyActivity = AppConstants.MODERATE_EXERCISE
        }
        sixToSevenButton.setOnClickListener {
            sixToSevenButton.background = selectedBackGround
            sixToSevenButton.setTextColor(whiteColor)
            veryLittleButton.background = unSelectedBackGround
            veryLittleButton.setTextColor(blackColor)
            oneToThreeButton.background = unSelectedBackGround
            oneToThreeButton.setTextColor(blackColor)
            threeToFiveButton.background = unSelectedBackGround
            threeToFiveButton.setTextColor(blackColor)
            veryHardButton.background = unSelectedBackGround
            veryHardButton.setTextColor(blackColor)

            weeklyActivity = AppConstants.HARD_EXERCISE
        }
        veryHardButton.setOnClickListener {
            veryHardButton.background = selectedBackGround
            veryHardButton.setTextColor(whiteColor)
            veryLittleButton.background = unSelectedBackGround
            veryLittleButton.setTextColor(blackColor)
            oneToThreeButton.background = unSelectedBackGround
            oneToThreeButton.setTextColor(blackColor)
            threeToFiveButton.background = unSelectedBackGround
            threeToFiveButton.setTextColor(blackColor)
            sixToSevenButton.background = unSelectedBackGround
            sixToSevenButton.setTextColor(blackColor)

            weeklyActivity = AppConstants.VERY_HARD_EXERCISE
        }

        val nextBtn = rootView.findViewById<ImageView>(R.id.bodyInfoToWeeklyGoalFragment_button)
        nextBtn.setOnClickListener {
            validator.validate()
        }

        return rootView
    }

    val fitnessCalculators = FitnessCalculators()
    fun initialize() {
        roundingFormat.roundingMode = RoundingMode.CEILING
        validator = Validator(this)
        validator.setValidationListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val blackColor = ContextCompat.getColor(requireContext(), R.color.gray)
        val whiteColor = ContextCompat.getColor(requireContext(), android.R.color.white)
        val selectedBackGround = ContextCompat.getDrawable(requireContext(), R.drawable.diet_buttons_background_selected)
        val unSelectedBackGround = ContextCompat.getDrawable(requireContext(), R.drawable.diet_unselected_faint_border)

        ft_button.setOnClickListener {
            if(isCm){
                toggleFtCm()
                ft_button.background = selectedBackGround
                ft_button.setTextColor(whiteColor)
                cm_button.background = unSelectedBackGround
                cm_button.setTextColor(blackColor)
            }

        }

        cm_button.setOnClickListener {
            if(!isCm){
                toggleFtCm()
                cm_button.background = selectedBackGround
                cm_button.setTextColor(whiteColor)
                ft_button.background = unSelectedBackGround
                ft_button.setTextColor(blackColor)
            }
        }

        kg_button.setOnClickListener {
            if(!isKg){
                toggleKgLb()
                kg_button.background = selectedBackGround
                kg_button.setTextColor(whiteColor)
                lb_button.background = unSelectedBackGround
                lb_button.setTextColor(blackColor)
            }
        }

        lb_button.setOnClickListener {
            if(isKg){
                toggleKgLb()
                lb_button.background = selectedBackGround
                lb_button.setTextColor(whiteColor)
                kg_button.background = unSelectedBackGround
                kg_button.setTextColor(blackColor)
            }
        }

    }

    private fun toggleFtCm() {
        toggleFtCmEditTextValues()
        isCm = !isCm
        if (isCm) {
            cm_input.visibility = View.VISIBLE
            feet_inches_view.visibility = View.GONE
        } else {
            feet_inches_view.visibility = View.VISIBLE
            cm_input.visibility = View.GONE
        }

    }

    private fun toggleFtCmEditTextValues() {

        if (!isCm) {
            val ftValueString = ftInput.text.toString()
            val inValueString = inInput.text.toString()
            val ftValue = if (ftValueString == "") 0 else ftValueString.toInt()
            val inValue = if (inValueString == "") 0 else inValueString

            cm_input.setText(fitnessCalculators.ftInToCm("$ftValue $inValue").toString())
        } else {
            val cmValueString = cmInput.text.toString()
            val cmValue = if (cmValueString == "") 0.toDouble() else cmValueString.toDouble()

            val split = fitnessCalculators.cmToftIn(cmValue).split(" ")
            val ft = split[0].toDouble().toInt()
            val inch = split[1].toDouble()
            ft_input.setText(ft.toString())
            in_input.setText(roundingFormat.format(inch))
        }
    }

    private fun toggleKgLb() {
        togglekgLbEditTextValues()
        isKg = !isKg

        if (isKg) {
            wt_input.hint = "kg"
        } else {
            wt_input.hint = "lb"
        }
    }
    private fun togglekgLbEditTextValues() {
        val valueString = wt_input.text.toString()
        val value =  if(valueString == "") 0.toDouble() else valueString.toDouble()

        if (!isKg) {
            wt_input.setText(fitnessCalculators.lbToKg(value).toString())
        } else {
            wt_input.setText(fitnessCalculators.kgToLb(value).toString())
        }
    }
    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
        for (error in errors!!) {
            (error.view as EditText).error = error.getCollatedErrorMessage(requireContext())
        }
    }

    private var isCm = false
    private var isKg = true
    private var isMale = true
    override fun onValidationSucceeded() {
        val height = if (isCm) {
            cmInput.text.toString().toDouble()
        } else {
            fitnessCalculators.ftInToCm("${ftInput.text.toString().toDouble()} ${inInput.text.toString().toDouble()}")
        }
        val weight = if (isKg) {
            wtInput.text.toString().toDouble()
        } else {
            fitnessCalculators.lbToKg(wtInput.text.toString().toDouble())
        }

        val activity = activity as DietStartPagesActivity
        activity.weeklyActivity = weeklyActivity
        activity.age = ageInput.text.toString().toInt()
        activity.height = height
        activity.weight = weight
        activity.isMale = isMale

        activity.ab!!.show()
        activity.viewPager.currentItem += 1
    }
}