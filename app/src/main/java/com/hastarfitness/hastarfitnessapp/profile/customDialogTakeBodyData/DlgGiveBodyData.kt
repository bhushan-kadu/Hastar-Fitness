package com.hastarfitness.hastarfitnessapp.profile.customDialogTakeBodyData

import android.app.Dialog
import android.text.InputType
import android.view.View
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatTextView
import com.github.mikephil.charting.data.Entry
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.*
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.fitnessCalculators.FitnessCalculators
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import com.hastarfitness.hastarfitnessapp.profile.FitnessDataFragment
import devs.mulham.horizontalcalendar.HorizontalCalendar
import kotlinx.android.synthetic.main.dlg_give_body_data.*
import kotlinx.android.synthetic.main.dlg_give_body_data.cmInputEditText
import kotlinx.android.synthetic.main.dlg_give_body_data.cmInputLayout
import kotlinx.android.synthetic.main.dlg_give_body_data.feetInputEditText
import kotlinx.android.synthetic.main.dlg_give_body_data.feet_inches_view
import kotlinx.android.synthetic.main.dlg_give_body_data.genderInputLayout
import kotlinx.android.synthetic.main.dlg_give_body_data.hipInputEditText
import kotlinx.android.synthetic.main.dlg_give_body_data.hipInputLayout
import kotlinx.android.synthetic.main.dlg_give_body_data.inInputEditText
import kotlinx.android.synthetic.main.dlg_give_body_data.neakInputEditText
import kotlinx.android.synthetic.main.dlg_give_body_data.toggle_ft_cm
import kotlinx.android.synthetic.main.dlg_give_body_data.waistInputEditText
import kotlinx.android.synthetic.main.dlg_give_weight.cancel_btn
import kotlinx.android.synthetic.main.dlg_give_weight.save_btn
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*


/**
 * Custom Dialogue for showing exercise desc to user
 *
 * @author Bhushan Kadu
 */
class DlgGiveBodyData(private val ctx: FitnessDataFragment) : Dialog(ctx.requireContext()), View.OnClickListener, Validator.ValidationListener {

    lateinit var horizontalCalendar: HorizontalCalendar
    var entry:Entry? = null
    var date:Date? = null
    var isSaveClicked = false
    lateinit var session:Session

    private lateinit var globalValidator: Validator

    private var isCm = true
    private var iskg = true

    @NotEmpty
    @Max(100, message = "Please select maximum 100")
    @Min(1, message = "Please select minimum 1")
    lateinit var ageInput: TextInputEditText

    @NotEmpty
    @DecimalMax(450.0, message = "Please select maximum 450 kg")
    @DecimalMin(5.0, message = "Please select minimum 5 kg")
    lateinit var kgInput: TextInputEditText

    @NotEmpty
    @DecimalMax(272.0, message = "Please select maximum 272 cm")
    @DecimalMin(30.0, message = "Please select minimum 30 cm")
    lateinit var cmInput: TextInputEditText

    @NotEmpty
    @Max(8, message = "Please select maximum 8 feet")
    @Min(1, message = "Please select minimum 1 feet")
    lateinit var ftInput: TextInputEditText

    @NotEmpty
    @DecimalMax(11.0, message = "Please select maximum 12 inch")
    @DecimalMin(0.0, message = "Please select minimum 1 inch")
    lateinit var inInput: TextInputEditText

    @NotEmpty
    lateinit var genderInput: AutoCompleteTextView

    @NotEmpty
    @DecimalMax(100.0, message = "Please select maximum 100 inch")
    @DecimalMin(6.0, message = "Please select minimum 6 inch")
    lateinit var waistInput: TextInputEditText

    @NotEmpty
    @DecimalMax(150.0, message = "Please select maximum 150 inch")
    @DecimalMin(20.0, message = "Please select minimum 20 inch")
    lateinit var hipInput: TextInputEditText

    @NotEmpty
    @DecimalMax(20.0, message = "Please select maximum 20 inch")
    @DecimalMin(5.0, message = "Please select minimum 5 inch")
    lateinit var neckInput: TextInputEditText

    val fitnessCalculators = FitnessCalculators()

    override fun create() {
        super.create()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dlg_give_body_data)

        //init
        initialize()

    }

    private fun initialize() {

        roundingFormat.roundingMode = RoundingMode.CEILING

        //setup session
        session = Session(ctx.requireActivity())

        ageInput = age_input
        kgInput = kg_lb_input
        cmInput = cmInputEditText
        ftInput = feetInputEditText
        inInput = inInputEditText
        genderInput = genderInput_textView
        genderInput.inputType = InputType.TYPE_NULL
        waistInput = waistInputEditText
        hipInput = hipInputEditText
        neckInput = neakInputEditText

        ageInput.setText(session.age.toString())
        kgInput.setText(session.weightInKg.toString())
        cmInput.setText(session.heightCm.toString())
        if(session.gender == AppConstants.MALE){
            genderInput.setText(AppConstants.MALE, false)
        }else{
            genderInput.setText(AppConstants.FEMALE, false)
        }



        val items = listOf(AppConstants.FEMALE, AppConstants.MALE)
        val adapter = ArrayAdapter(ctx.requireContext(), R.layout.single_list_item, items)
        (genderInputLayout.editText as? AppCompatAutoCompleteTextView)?.setAdapter(adapter)

        //initialize
        globalValidator = Validator(this);

        //set validation listener
        globalValidator.setValidationListener(this);

        cancel_btn.setOnClickListener(this)
        save_btn.setOnClickListener(this)

        toggle_kg_lb.setOnClickListener {  toggleKgLb() }
        toggle_ft_cm.setOnClickListener { toggleFtCm() }

        genderInput.setOnItemClickListener { parent, view, position, id ->

            val dropDown = (view as AppCompatTextView)
            when (dropDown.text) {
                AppConstants.MALE -> {
                    setMaleVisibility()
                }
                AppConstants.FEMALE -> {
                    setFemaleVisibility()
                }
            }
            genderInput.error = null

        }

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
    private val roundingFormat = DecimalFormat("#.##")
    private fun toggleFtCmEditTextValues() {

        if (!isCm) {
            val ftValueString = ftInput.text.toString()
            val inValueString = inInput.text.toString()
            val ftValue = if (ftValueString == "") 0.toDouble() else ftValueString.toDouble()
            val inValue = if (inValueString == "") 0.toDouble() else inValueString.toDouble()

            cmInput.setText(fitnessCalculators.ftInToCm("$ftValue $inValue").toString())
        } else {
            var ft = 0
            var inch = 0.0
            val cmValueString = cmInput.text.toString()
            val cmValue = if (cmValueString == "") 0.toDouble() else cmValueString.toDouble()
            if (cmValue != 0.toDouble()) {
                val split = fitnessCalculators.cmToftIn(cmValue).split(" ")
                ft = split[0].toInt()
                inch = split[1].toDouble()
            }
            ftInput.setText(ft.toString())
            inInput.setText(roundingFormat.format(inch))
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

    private fun togglekgLbEditTextValues() {
        val valueString = kg_lb_input.text.toString()
        val value =  if(valueString == "") 0.toDouble() else valueString.toDouble()

        if (!iskg) {
            kg_lb_input.setText(fitnessCalculators.lbToKg(value).toString())
        } else {
            kg_lb_input.setText(fitnessCalculators.kgToLb(value).toString())
        }
    }

    //implement clicks
    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.cancel_btn -> {
                dismiss()
            }
            R.id.save_btn -> {

                isSaveClicked = true
                globalValidator.validate()
            }

        }
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
        for (error in errors!!) {
            val view = error.view
            if (view is TextInputEditText) {
                view.error = "please enter this field"
            } else if (view is MaterialAutoCompleteTextView) {
                view.error = "please enter this field"
            }
        }
    }

    override fun onValidationSucceeded() {
        session.age = ageInput.text.toString().toInt()
        session.gender = genderInput.text.toString()
        session.weistInches = waistInput.text.toString().toDouble()
        session.neakInches = neckInput.text.toString().toDouble()
        if(hipInputLayout.visibility == View.VISIBLE){
            session.hipInches = hipInput.text.toString().toDouble()
        }

        session.heightCm = if(isCm){
            cmInput.text.toString().toDouble()
        }else{
            val ftValueString = ftInput.text.toString()
            val inValueString = inInput.text.toString()
            val ftValue = if (ftValueString == "") 0.toDouble() else ftValueString.toDouble()
            val inValue = if (inValueString == "") 0.toDouble() else inValueString.toDouble()

            fitnessCalculators.ftInToCm("$ftValue $inValue")
        }
        session.weightInKg = if(iskg){
            kgInput.text.toString().toDouble()
        }else{
            fitnessCalculators.lbToKg(kgInput.text.toString().toDouble())
        }
        dismiss()
    }
}

