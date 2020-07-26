package com.hastarfitness.hastarfitnessapp.meditationNew

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.yoga.YogaListActivity
import kotlinx.android.synthetic.main.activity_show_meditation_types.*

/**
 * Activity to shown yoga type cards to user
 *
 * @author Bhushan Kadu
 */
class ShowMeditationTypesActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_meditation_types)

        //title
        supportActionBar!!.title = "Select a Meditation Type"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        exercises.setOnClickListener(this)
        activities.setOnClickListener(this)
        seven_days.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> onBackPressed()
        }
        return true
    }
    override fun onClick(v: View?) {
        val type = v!!.tag.toString()
        val i = Intent(this@ShowMeditationTypesActivity, YogaListActivity::class.java);
        i.putExtra(AppConstants.YOGA_TYPE, type)
        i.putExtra(AppConstants.WORKOUT_TYPE, AppConstants.MEDITATION)
        startActivity(i)
    }
}
