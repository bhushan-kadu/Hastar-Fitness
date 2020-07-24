package com.hastarfitness.hastarfitnessapp.exerciseListForCardioAndBodyWeight

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants

class ShowBodyWeightTypesActivity : AppCompatActivity(), View.OnClickListener {
    var typeOfIntensity = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_body_weight_types)

        supportActionBar!!.title = "Select a Body Weight Type"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun onClick(v: View?) {
        val subType = v!!.getTag().toString()
        val i = Intent(this@ShowBodyWeightTypesActivity, ActivityExerciseList::class.java)
        i.putExtra(AppConstants.WORKOUT_TYPE, AppConstants.BODY_WEIGHT)
        i.putExtra(AppConstants.WORKOUT_SUB_TYPE, subType)
        startActivity(i)
    }
}
