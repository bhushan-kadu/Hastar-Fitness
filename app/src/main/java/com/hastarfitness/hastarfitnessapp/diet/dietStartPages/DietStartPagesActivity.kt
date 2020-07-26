package com.hastarfitness.hastarfitnessapp.diet.dietStartPages

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import kotlinx.android.synthetic.main.activity_diet_start_pages.*

/**
 * Activity containing starting pages of the app
 * - This activity takes required info from user for app to run e.g. age, weight..etc
 */
class DietStartPagesActivity : AppCompatActivity(){
    var position = 0
    lateinit var viewPager: ViewPager2
    var ab: ActionBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_diet_start_pages)
        val viewPagerAdapterForStartingPages = ViewPagerAdapterForDietStartPages(this)
        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = viewPagerAdapterForStartingPages

        //init class
        initialize()

        setSupportActionBar(toolbar)

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        // Get a support ActionBar corresponding to this toolbar
        ab = supportActionBar

        // Enable the Up button
        ab!!.setDisplayHomeAsUpEnabled(true)
//        val orangeColor = ContextCompat.getDrawable(this, R.color.orange)
//        ab!!.setBackgroundDrawable(orangeColor)

        //this preloads pages for faster loads
        viewPager.offscreenPageLimit = 1
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                ab!!.setTitle(TAB_TITLES[position])
            }
        })
        viewPager.isUserInputEnabled = false
    }

    fun initialize(){
        session = Session(this)
    }
    private fun goBackPage() {
        if (viewPager.currentItem == 0) {
            finish()
        }else {
            viewPager.currentItem = viewPager.currentItem - 1
            position -= 1
        }

        if(viewPager.currentItem == 0){
            toolbar.visibility = View.GONE
        }
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

    private var mLastClickTime: Long = 0
    var dietPreference = ""
    var dietWeeklyGoal = ""
    var age = -1
    var height = -1.0
    var weight = -1.0
    var weeklyActivity = ""
    var isMale = true
    lateinit var session:Session

//    override fun onClick(v: View) {
//        if (SystemClock.elapsedRealtime() - mLastClickTime < 300) {
//            return
//        }
//        mLastClickTime = SystemClock.elapsedRealtime()
////        when (v.id) {
////            R.id.goalToBodyInfo_button, R.id.bodyInfoToDietFragment_button-> {
////                ab!!.show()
////                viewPager.currentItem += 1
////            }
////            R.id.weeklyGoalToDiet ->{
////                val i = Intent(applicationContext, ActivityDashboard::class.java)
////                i.putExtra(AppConstants.SWITCH_FRAGMENT, R.id.navigation_diet)
////                startActivity(i)
////            }
////        }
//    }


    companion object {
        private val TAB_TITLES = intArrayOf(R.string.title_activity_start_diet,
                R.string.title_activity_get_body_information,
                R.string.title_activity_get_week_goal)
    }
}