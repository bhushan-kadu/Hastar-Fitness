package com.hastarfitness.hastarfitnessapp.meditationNew

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.ViewModel
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.database.YogaExerciseDbModel
import kotlinx.android.synthetic.main.activity_yoga_list.*
import java.io.Serializable

/**
 * Activity to show yoga exercise list
 *
 * @author Bhushan Kadu
 */
class MeditationListActivity : AppCompatActivity() {

    lateinit var db: AppDatabase
    private lateinit var viewModel: ViewModel
    var workoutType = "yoga"
    var intensity = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yoga_list)


        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        //init class
        init()

        var yogaExerciseList = listOf<YogaExerciseDbModel>()

        //setup ViewModel
        viewModel = ViewModelProviders.of(this).get(ViewModel()::class.java)

        //code runs in background thread
        viewModel.getYogaExercises(db, workoutType, intensity)

        //invokes when exercises value change happen
        viewModel.yogaExercises.observe(this, Observer { it ->
            yogaExerciseList = it

            recycler_view_yoga.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = MyMeditationExerciseListAdapter(yogaExerciseList, this@MeditationListActivity)
            }

            supportActionBar!!.title = it[0].intensity
        })

        start.setOnClickListener {
            val i = Intent(applicationContext, ShowMeditationDescAndVideoActivity::class.java)
            i.putExtra(AppConstants.YOGA_EXERCISES, yogaExerciseList as Serializable)
            i.putExtra("position", 0)
            startActivity(i)
        }
    }

    fun init() {
        intensity = intent.getStringExtra(AppConstants.YOGA_TYPE)
        instantiateDb()
    }

    private fun instantiateDb() {
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "HasterDb.db")
                .build()
    }
}



