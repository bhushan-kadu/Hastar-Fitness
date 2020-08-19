package com.hastarfitness.hastarfitnessapp.startingPages

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hastarfitness.hastarfitnessapp.ActivityDashboard
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.ViewModel
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import com.hastarfitness.hastarfitnessapp.models.User

class AppStartLoadingScreen : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_app_start_loading_screen)

        instantiateDb()

        val session = Session(this)

        //send data to firebase if user is not a child
        if(!session.isChildLoggedIn){

            database = Firebase.database.reference
            val auth = Firebase.auth
            val uid = auth.currentUser!!.uid
            val email = auth.currentUser!!.email

            writeNewUser(uid, session.userName!!, email, session.gender!!, session.dateOfBirth!!, session.weightInKg!!,
                    session.goalWeight!!, session.heightCm!!, session.weeklyActivity!!, session.dietPreference!!,
                    session.dietWeeklyGoal!!)
        }


        //Its a delayed task to show app for 2000 time and then intent to mainActivity
        Handler().postDelayed({
            startActivity(Intent(applicationContext, ActivityDashboard::class.java))
        }, 2000)
    }
    private fun writeNewUser(userId: String,
                             name: String,
                             email: String?,
                             gender: String,
                             dob: String,
                             weight: Double,
                             goalWeight: Double,
                             height: Double,
                             weeklyActivity: String,
                             dietPreference: String,
                             weeklyDietGoal: String) {
//        val user = User(userId, name, email, gender, dob, weight, goalWeight, height, weeklyActivity, dietPreference, weeklyDietGoal)
        val userHashMap = hashMapOf<String, String>()
        userHashMap[AppConstants.FULL_NAME] = name
        userHashMap[AppConstants.EMAIL] = email!!
        userHashMap[AppConstants.GENDER] = gender
        userHashMap[AppConstants.DOB] = dob
        userHashMap[AppConstants.WEIGHT_KG] = weight.toString()
        userHashMap[AppConstants.GOAL_WEIGHT_KG] = goalWeight.toString()
        userHashMap[AppConstants.HEIGHT_CM] = height.toString()
        userHashMap[AppConstants.WEEKLY_ACTIVITY] = weeklyActivity
        userHashMap[AppConstants.DIET_PREFERENCE] = dietPreference
        userHashMap[AppConstants.WEEKLY_GOAL] = weeklyDietGoal
        userHashMap[AppConstants.CREATED_AT] = ServerValue.TIMESTAMP.toString()
        userHashMap[AppConstants.UPDATED_AT] = ServerValue.TIMESTAMP.toString()

        database.child("users").child(userId).setValue(userHashMap)
                .addOnSuccessListener {
                    // Write was successful!
                    // ...
                    1
                }
                .addOnFailureListener {
                    // Write failed
                    // ...
                    1
                }
    }

    private fun instantiateDb() {
        val viewModel = ViewModelProvider(this).get(ViewModel::class.java)
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