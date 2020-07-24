package com.hastarfitness.hastarfitnessapp.yoga

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import kotlinx.android.synthetic.main.activity_show_yoga_types.*

/**
 * Activity to shown yoga type cards to user
 */
class ShowYogaTypesActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_yoga_types)

        //title
        supportActionBar!!.title = "Select a Yoga Type"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        fifteen_days.setOnClickListener(this)
        stress_anxiety.setOnClickListener(this)
        yoga_poses.setOnClickListener(this)
        suryanamaskar_and_seven_chakras.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> onBackPressed()
        }
        return true
    }
    override fun onClick(v: View?) {
        val type = v!!.tag.toString()
        val i = Intent(this@ShowYogaTypesActivity, YogaListActivity::class.java);
        i.putExtra(AppConstants.YOGA_TYPE, type)
        i.putExtra(AppConstants.WORKOUT_TYPE, AppConstants.YOGA)
        startActivity(i)
    }
}
