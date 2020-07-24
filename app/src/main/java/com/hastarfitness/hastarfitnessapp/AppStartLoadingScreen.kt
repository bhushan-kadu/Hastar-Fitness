package com.hastarfitness.hastarfitnessapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hastarfitness.hastarfitnessapp.database.AppDatabase

class AppStartLoadingScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_app_start_loading_screen)


        instantiateDb()


        //Its a delayed task to show app for 2000 time and then intent to mainActivity
        Handler().postDelayed({
            startActivity(Intent(applicationContext, ActivityDashboard::class.java))
        }, 2000)
    }

    private fun instantiateDb() {
        val viewModel = ViewModelProviders.of(this).get(ViewModel::class.java)
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "HasterDb.db")
                .createFromAsset("databases/HasterDb.db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Log.v("database callback", "create called")
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        Log.v("database callback", "open called")
                    }
                })
                .build()
        try {
            viewModel.getAll(db)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}