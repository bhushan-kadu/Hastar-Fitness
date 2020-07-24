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
import kotlinx.android.synthetic.main.fragment_diet_get_body_info.*

class FragmentGetBodyInfo : Fragment(), Validator.ValidationListener  {

    @NotEmpty
    @DecimalMax(450.0, message = "Please Select maximum 450 Kg")
    @DecimalMin(1.0, message = "Please Select minimum 1 Kg")
    lateinit var wtInput:EditText

    @NotEmpty
    @Max(8, message = "Please Select maximum 8 ft")
    @Min(1, message = "Please Select minimum 1 ft")
    lateinit var ftInput:EditText

    @NotEmpty
    @Max(12, message = "Please Select maximum 12 in")
    @Min(1, message = "Please Select minimum 1 in")
    lateinit var inInput:EditText

    @NotEmpty
    @DecimalMax(272.0, message = "Please Select maximum 272 cm")
    @DecimalMin(30.0, message = "Please Select minimum 30 cm")
    lateinit var cmInput:EditText

    @NotEmpty
    @Max(100, message = "Please Select maximum 100")
    @Min(1, message = "Please Select minimum 1")
    lateinit var ageInput:EditText

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
        ftButton.setOnClickListener {
            feet_inches_view.visibility = View.VISIBLE
            cm_input.visibility = View.GONE
            ftButton.background = selectedBackGround
            ftButton.setTextColor(whiteColor)
            cmButton.background = unSelectedBackGround
            cmButton.setTextColor(blackColor)

            isCm = false
        }

        cmButton.setOnClickListener {
            feet_inches_view.visibility = View.GONE
            cm_input.visibility = View.VISIBLE
            cmButton.background = selectedBackGround
            cmButton.setTextColor(whiteColor)
            ftButton.background = unSelectedBackGround
            ftButton.setTextColor(blackColor)

            isCm = true
        }

        kgButton.setOnClickListener {
            wtInput.hint = getString(R.string.kg)
            kgButton.background = selectedBackGround
            kgButton.setTextColor(whiteColor)
            lbButton.background = unSelectedBackGround
            lbButton.setTextColor(blackColor)

            isKg = true
        }

        lbButton.setOnClickListener {
            wtInput.hint = getString(R.string.lb)
            lbButton.background = selectedBackGround
            lbButton.setTextColor(whiteColor)
            kgButton.background = unSelectedBackGround
            kgButton.setTextColor(blackColor)

            isKg = false
        }

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
    fun initialize(){
        validator = Validator(this)
        validator.setValidationListener(this)
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
        val height = if(isCm){
            cmInput.text.toString().toDouble()
        }else{
            fitnessCalculators.ftInToCm("${inInput.text.toString().toDouble()} ${ftInput.text.toString().toDouble()}")
        }
        val weight = if(isKg){
            wtInput.text.toString().toDouble()
        }else{
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