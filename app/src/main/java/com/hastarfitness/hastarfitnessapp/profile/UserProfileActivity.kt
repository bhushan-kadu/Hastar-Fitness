package com.hastarfitness.hastarfitnessapp.profile

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hastarfitness.hastarfitnessapp.ActivityDashboard
import com.hastarfitness.hastarfitnessapp.settings.AppSettingsActivity
import com.hastarfitness.hastarfitnessapp.BuildConfig
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : AppCompatActivity() {
    private  var myViewPager: MyViewPager? = null
    private var session: Session? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        try{
            //init class
            initialize()

            //set refer friend btn
            referFriend_btn.setOnClickListener { shareApp() }

//            //set streak number
            setStreakNo()
//
//            //display user data
            displayUser()
        }catch (e:Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }


    }

    private fun displayUser() {
        val userGender = session!!.gender
        val userName = session!!.userName
        val photoUrl = session!!.photoUrl
        val userEmail = session!!.userEmail
        if (userGender == AppConstants.MALE) {
            Picasso.get()
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_man)
                    .into(userImage_ImageView)
        } else {
            Picasso.get()
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_woman)
                    .into(userImage_ImageView)
        }

        userName_textView.text = userName
        userEmail_textView.text = userEmail

    }

    private fun setStreakNo() {
        streak_no_textView.text = session!!.streakNo.toString()
        when (session!!.streakNo) {
            in 10..20 -> emoji_imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_ten_to_twenty_emoji))
            in 20..30 -> emoji_imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_twenty_to_thirty_emoji))
            in 30..40 -> emoji_imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_thirty_to_fourty_emoji))
            in 40..50 -> emoji_imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fourty_to_fifty_emoji))
            in 50..60 -> emoji_imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fifty_to_sixty_emoji))
            in 60..70 -> emoji_imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_sixty_to_seventy_emoji))
            in 70..80 -> emoji_imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_seventy_to_eighty_emoji))
            in 80..90 -> emoji_imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_eighty_to_ninty_emoji))
            in 90..100 -> emoji_imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_ninty_to_hundred_emoji))
        }

    }

    fun initialize() {
        session = Session(this)

        //set view pager
        myViewPager = viewPager as MyViewPager
        var list = listOf<String>("Reports", "Fitness Data")
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
        viewPager.setOnTouchListener(OnTouchListener { v, event -> true })
        viewPager.setOnTouchListener(OnTouchListener { v, event -> true })
        myViewPager!!.disableScroll(true)
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

    private fun shareApp() {
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