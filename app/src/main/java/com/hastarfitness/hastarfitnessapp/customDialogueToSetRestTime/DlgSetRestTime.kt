package com.hastarfitness.hastarfitnessapp.customDialogueToSetRestTime

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.Window
import com.hastarfitness.hastarfitnessapp.R
import kotlinx.android.synthetic.main.dlg_set_rest_time.*

/**
 * custom dialogue class for getting custom plan info to save the plan from user
 *
 * @author Bhushan Kadu
 */
class DlgSetRestTime(ctx: Context, var restTime: Int) : Dialog(ctx) {


    var mAutoIncrement = false;
    var mAutoDecrement = false;
    var isSaved = false

    //Repetition delay for increment
    val REP_DELAY: Long = 50

    /**
     * function to create the dialogue
     */
    override fun create() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_set_rest_time);

        restNumber_textView.text = restTime.toString()


        /**
         * Inner class to handle long press button clock incrementation on new thread
         */
        val repeatUpdateHandler = Handler()

        class RptUpdater : Runnable {
            override fun run() {
                if (mAutoIncrement) {
                    increment()
                    repeatUpdateHandler.postDelayed(RptUpdater(), REP_DELAY)
                } else if (mAutoDecrement) {
                    decrement()
                    repeatUpdateHandler.postDelayed(RptUpdater(), REP_DELAY)
                }
            }
        }

        /**
         *  for incrementing the clock repeatedly on plus long click
         */
        plus.setOnLongClickListener {
            mAutoIncrement = true
            repeatUpdateHandler.post(RptUpdater())
            false
        }
        //Touch listener for the action up event on minus button
        //It tells system that button is released
        plus.setOnTouchListener { v, event ->
            if ((event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL)
                    && mAutoIncrement) {
                mAutoIncrement = false;
            }
            false;
        }

        /**
         * for decrementing the clock repeatedly on minus long click
         */
        minus.setOnLongClickListener {
            mAutoDecrement = true
            repeatUpdateHandler.post(RptUpdater())
            false
        }
        //Touch listener for the action up event on minus button
        //It tells system that button is released
        minus.setOnTouchListener { v, event ->
            if ((event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL)
                    && mAutoDecrement) {
                mAutoDecrement = false;
            }
            false;
        }

        save_button.setOnClickListener {

            isSaved = true
            restTime = restNumber_textView.text.toString().toInt()
            dismiss()
        }

        /**
         * plus minus button click handling
         */
        plus.setOnClickListener {
            increment()
        }
        minus.setOnClickListener {
            decrement()
        }
    }

    fun increment() {
        val value = restNumber_textView.text.toString().toInt()
        if(value < 180){
            restNumber_textView.text = (value + 1).toString()
        }
    }

    override fun show() {
        super.show()
        restNumber_textView.text = restTime.toString()
    }



    fun decrement() {
        val value = restNumber_textView.text.toString().toInt()
        if(value > 5){
            restNumber_textView.text = (value - 1).toString()
        }
    }

}

