package com.hastarfitness.hastarfitnessapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_about_us.*
//this is a comment added
class AboutUs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        close_Btn.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        //exit activity
        val intent = Intent(applicationContext, ActivityDashboard::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}
