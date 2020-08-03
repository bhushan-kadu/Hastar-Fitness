package com.hastarfitness.hastarfitnessapp.meditation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.ViewModel
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.database.MeditationDbModel
import com.hastarfitness.hastarfitnessapp.database.MeditationDbModelNew
import kotlinx.android.synthetic.main.activity_yoga_list.*

class MeditationListActivity : AppCompatActivity() {

    private lateinit var viewModel: ViewModel

    var meditationType = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation_list)



        init()
        //Instantialte db
        val db = Room.databaseBuilder(this,
                AppDatabase::class.java, "HasterDb.db")
                .build()

        var meditationExerciseList = listOf<MeditationDbModelNew>()

        //setup ViewModel
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)

        //code runs in background thread
        viewModel.getMeditations(db, meditationType)

        //invokes when exercises value change happen
        viewModel.meditations.observe(this, Observer { it ->
            meditationExerciseList = it

            recycler_view_yoga.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = MyMeditationExerciseListAdapter(meditationExerciseList, this@MeditationListActivity)
            }
        })


    }

    fun init(){
        meditationType = intent.getStringExtra(AppConstants.MEDITATION_TYPE)

        instantialteDb()

    }




    private fun instantialteDb() {
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "HasterDb.db")
                .createFromAsset("databases/HasterDb.db")
                .build()
        val viewModel = ViewModelProvider(this).get(ViewModel::class.java)
        viewModel.getAll(db)
        viewModel.getRest(db, AppConstants.BODY_WEIGHT, AppConstants.BEGINNER)
        viewModel.exercise
        viewModel.restTime
    }




}



