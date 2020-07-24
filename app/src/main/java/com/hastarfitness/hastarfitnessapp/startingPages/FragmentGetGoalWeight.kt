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

class FragmentGetGoalWeight : Fragment(), RulerValuePickerListener, View.OnClickListener {
    private var TAG = "tag"
    lateinit var btnSetWeight: Button
    lateinit var rulerValuePicker: RulerValuePicker
    lateinit var weightTextView: TextView
    lateinit var weightUnit: TextView
    lateinit var switchKgToLb: Switch
    private val LB = 2.206
    var curUnit = "Kg"
    lateinit var viewPager: ViewPager2
    fun init(view: View) {
        weightTextView = view.findViewById(R.id.weightText)
        switchKgToLb = view.findViewById(R.id.switchKgToLb)
        btnSetWeight = view.findViewById(R.id.btnSetGoalWeight)
        rulerValuePicker = view.findViewById(R.id.ruler_weight_picker)
        rulerValuePicker.setValuePickerListener(this)
        btnSetWeight.setOnClickListener(this)
        switchKgToLb.setOnClickListener(this)
        viewPager = requireActivity().findViewById<View>(R.id.view_pager) as ViewPager2
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_goal_weight, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(view)
        rulerValuePicker.selectValue(73)

        val parentActivity = activity as ActivityStartPages
        btnSetWeight.setOnClickListener {
            val weight = if(curUnit == AppConstants.LB){
                FitnessCalculators().lbToKg(rulerValuePicker.currentValue.toDouble())
            }else{
                rulerValuePicker.currentValue.toDouble()
            }
            parentActivity.goalWeight = weight
            viewPager.currentItem = viewPager.currentItem + 1
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.switchKgToLb -> switchKgToLb(v)
        }
    }

    override fun onValueChange(selectedValue: Int) {
        weightTextView.text = "$selectedValue $curUnit"
    }

    override fun onIntermediateValueChange(selectedValue: Int) {
        weightTextView.text = "$selectedValue $curUnit"
    }

    /**
     * This is a onclick method associated with the toggle switch in layout file for LB and KG conversion
     *
     * @param view
     */
    fun switchKgToLb(view: View?) {
        var newVal = 0
        /**
         * if kg then convert to Lb else if lb then convert to KG
         */
        if (rulerValuePicker.minValue == 40) {
            curUnit = "lb"
            newVal = Math.ceil(rulerValuePicker.currentValue * LB).toInt()
            rulerValuePicker.setMinMaxValue(80, 300)
            rulerValuePicker.selectValue(newVal)
        } else if (rulerValuePicker.minValue == 80) {
            curUnit = "Kg"
            newVal = Math.floor(rulerValuePicker.currentValue / LB).toInt()
            rulerValuePicker.setMinMaxValue(40, 150)
            rulerValuePicker.selectValue(newVal)
        }
        Log.d(TAG, "Currently the picker is at $newVal percent.")
    }
}