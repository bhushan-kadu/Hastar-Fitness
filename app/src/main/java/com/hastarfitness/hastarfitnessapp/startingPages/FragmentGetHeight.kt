package com.hastarfitness.hastarfitnessapp.startingPages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.fitnessCalculators.FitnessCalculators
import com.kevalpatel2106.rulerpicker.RulerValuePicker
import com.kevalpatel2106.rulerpicker.RulerValuePickerListener
import kotlin.math.ceil
import kotlin.math.floor

class FragmentGetHeight : Fragment(), RulerValuePickerListener, View.OnClickListener {
    private var TAG = "tag"
    lateinit var btnSetHeight: Button
    lateinit var rulerValuePicker: RulerValuePicker
    lateinit var heightTextView: TextView
    lateinit var heightUnit: TextView
    lateinit var toggleInCm: Switch
    private val INCH = 0.4
    private val CM = 2.5
    var curUnit = "cm"
    var viewPager: ViewPager2? = null
    fun init(view: View) {
        heightTextView = view.findViewById(R.id.heightText)
        toggleInCm = view.findViewById(R.id.toggleInCm)
        btnSetHeight = view.findViewById(R.id.btnSetHeight)
        rulerValuePicker = view.findViewById(R.id.ruler_picker)
        toggleInCm.setOnClickListener(this)
        btnSetHeight.setOnClickListener(this)
        rulerValuePicker.setValuePickerListener(this)
        viewPager = requireActivity().findViewById<View>(R.id.view_pager) as ViewPager2
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_height, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(view)
        rulerValuePicker.selectValue(170)

        val parentActivity = activity as ActivityStartPages
        btnSetHeight.setOnClickListener {
            val height = if(curUnit == AppConstants.INCH){
                FitnessCalculators().inToCm(rulerValuePicker.currentValue.toDouble())
            }else{
                rulerValuePicker.currentValue.toDouble()
            }
            parentActivity.height = height
            viewPager!!.currentItem = viewPager!!.currentItem + 1
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.toggleInCm -> switchInToCm(v)
        }
    }

    override fun onValueChange(selectedValue: Int) {
        heightTextView.text = "$selectedValue $curUnit"
    }

    override fun onIntermediateValueChange(selectedValue: Int) {
        heightTextView.text = "$selectedValue $curUnit"
    }

    /**
     * This is a onclick method associated with the toggle switch in layout file for inch and cm conversion
     *
     * @param view
     */
    private fun switchInToCm(view: View?) {
        var newVal = 0
        if (rulerValuePicker.minValue == 130) {
            curUnit = AppConstants.INCH
            newVal = ceil(rulerValuePicker.currentValue * INCH).toInt()
            rulerValuePicker.setMinMaxValue(52, 98)
            rulerValuePicker.selectValue(newVal)
        } else if (rulerValuePicker.minValue == 52) {
            curUnit = AppConstants.CM
            newVal = floor(rulerValuePicker.currentValue * CM).toInt()
            rulerValuePicker.setMinMaxValue(130, 250)
            rulerValuePicker.selectValue(newVal)
        }
        Log.d(TAG, "Currently the picker is at $newVal percent.")
    }
}