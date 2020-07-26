package com.hastarfitness.hastarfitnessapp.startingPages

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.hastarfitness.hastarfitnessapp.ActivityDashboard
import com.hastarfitness.hastarfitnessapp.ActivitySplashScreen
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import com.hastarfitness.hastarfitnessapp.startingPages.ActivityStartPages
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.util.*

/**
 * Activity containing starting pages of the app
 * - This activity takes required info from user for app to run e.g. age, weight..etc
 *
 * @author Bhushan Kadu
 */
class ActivityStartPages : AppCompatActivity() {
    var position = 0
    lateinit var viewPager: ViewPager2
    lateinit var ab: ActionBar


    //values to be added to preferences
    var age = -1
    var gender = ""
    var height = 0.0
    var weight = 0.0
    var goalWeight = 0.0
    var weeklyActivity = ""
    lateinit var session:Session


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_start_pages)
        val viewPagerAdapterForStartingPages = ViewPagerAdapterForStartingPages(this@ActivityStartPages)
        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = viewPagerAdapterForStartingPages

        session = Session(this)

        // Get a support ActionBar corresponding to this toolbar
        ab = supportActionBar!!

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true)

        //this preloads pages for faster loads
        viewPager.offscreenPageLimit = 6
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                ab.setTitle(TAB_TITLES[position])
            }
        })
        viewPager.isUserInputEnabled = false
    }

    private fun goBackPage() {
        if (viewPager.currentItem == 0) {
            exitApp()
        } else {
            viewPager.currentItem = viewPager.currentItem - 1
            position -= 1
        }
    }
    private var doubleBackToExitPressedOnce = false
    private fun exitApp(){
        if (doubleBackToExitPressedOnce) {
            val intent = Intent(this@ActivityStartPages, ActivitySplashScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra(AppConstants.EXIT, true)
            startActivity(intent)
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.app_close_message), Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onBackPressed() {
        goBackPage()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> goBackPage()
        }
        return true
    }





    companion object {
        private val TAB_TITLES = intArrayOf(R.string.title_activity_get_gender,
                R.string.enter_age_text,
                R.string.title_activity_get_height,
                R.string.title_activity_get_weight,
                R.string.title_activity_get_goal_weight,
                R.string.title_activity_get_activity_level,
                R.string.empty_string)
    }
}