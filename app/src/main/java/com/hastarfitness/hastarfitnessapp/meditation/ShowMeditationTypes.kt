package com.hastarfitness.hastarfitnessapp.meditation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.hastarfitness.hastarfitnessapp.R
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import kotlinx.android.synthetic.main.activity_show_meditation_types.*

class ShowMeditationTypes : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_meditation_types)

        //title
        supportActionBar!!.title = "Select a Meditation Type"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        intro.setOnClickListener(this)
        steps.setOnClickListener(this)
        exercises.setOnClickListener(this)
        activities.setOnClickListener(this)
        seven_days.setOnClickListener(this)
//        fav.setOnClickListener(this)

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun onClick(v: View?) {
        val type= v!!.getTag().toString()
       val i = Intent(applicationContext, MeditationListActivity::class.java)
        i.putExtra(AppConstants.MEDITATION_TYPE,type)
        startActivity(i)
    }
}
