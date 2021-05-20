package com.hastarfitness.hastarfitnessapp.settings

import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.view.MotionEvent
import android.view.Window
import android.widget.Toast
import com.hastarfitness.hastarfitnessapp.R
import kotlinx.android.synthetic.main.dlg_set_pedometer_goal.*
import kotlinx.android.synthetic.main.dlg_set_pedometer_goal.save_button

/**
 * custom dialogue class for getting user steps goal
 *
 * @author Bhushan Kadu
 */
class DlgSetWalkingGoal(var ctx: Context, var stepsGoal: Int) : Dialog(ctx) {
    var isSaved = false
    /**
     * function to create the dialogue
     */
    override fun create() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_set_pedometer_goal);

        stepsGoal_textView.setText(stepsGoal.toString())

        save_button.setOnClickListener {
            isSaved = true
            val goal = stepsGoal_textView.text.toString().toInt()
            if(goal >= 500){
                stepsGoal = goal
                Toast.makeText(ctx, "Goal Saved Successfully", Toast.LENGTH_LONG).show()
                dismiss()
            }else{
                Toast.makeText(ctx, "Please select minimum 500 steps", Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun show() {
        super.show()
        stepsGoal_textView.setText(stepsGoal.toString())
    }
}

