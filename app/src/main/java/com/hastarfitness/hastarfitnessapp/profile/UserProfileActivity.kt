package com.hastarfitness.hastarfitnessapp.profile

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.OnTouchListener
import androidx.appcompat.app.AppCompatActivity
import com.hastarfitness.hastarfitnessapp.ActivityDashboard
import com.hastarfitness.hastarfitnessapp.AppSettingsActivity
import com.hastarfitness.hastarfitnessapp.BuildConfig
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : AppCompatActivity() {
    private lateinit var myViewPager: MyViewPager
    private lateinit var session:Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //init class
        initialize()

        //set refer friend btn
        referFriend_btn.setOnClickListener { shareApp() }

        //set streak number
        setStreakNo()

        //display user data
        displayUser()

    }
    private fun displayUser(){
        val userGender = session.gender
        val userName = session.userName
        val photoUrl = session.photoUrl
        val userEmail = session.userEmail
        if(userGender == AppConstants.MALE){
            Picasso.get()
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_person)
                    .into(userImage_ImageView)
        }else{
            Picasso.get()
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_woman)
                    .into(userImage_ImageView)
        }

        userName_textView.text = userName
        userEmail_textView.text = userEmail

    }
    private fun setStreakNo(){
        streak_no_textView.text = session.streakNo.toString()
    }
    fun initialize(){
        session = Session(this)

        //set view pager
        myViewPager = viewPager as MyViewPager
        var list = listOf<String>("Reports", "Fitness Data")
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
        viewPager.setOnTouchListener(OnTouchListener { v, event -> true })
        viewPager.setOnTouchListener(OnTouchListener { v, event -> true })
        myViewPager.disableScroll(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_act_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_settings -> startActivity(Intent(applicationContext, AppSettingsActivity::class.java))
            android.R.id.home -> onBackPressed();
        }
        return true
    }
    private fun shareApp(){
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out a awesome fitness app that I use daily *Hastar Fitness* :\n https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    override fun onBackPressed() {
        //exit activity
        val intent = Intent(applicationContext, ActivityDashboard::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}