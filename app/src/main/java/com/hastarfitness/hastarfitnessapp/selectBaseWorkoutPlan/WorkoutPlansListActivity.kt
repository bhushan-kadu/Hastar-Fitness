package com.hastarfitness.hastarfitnessapp.selectBaseWorkoutPlan

import android.os.Bundle
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.ViewModel
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.database.WorkoutPlansDbModel
import kotlinx.android.synthetic.main.activity_workoutplans_list.*
import java.lang.Exception

/**
 *  activity to select base plan
 *
 *  @author Bhushan Kadu
 */
class WorkoutPlansListActivity : AppCompatActivity() {

    private lateinit var viewModel: ViewModel
    var workoutType = "yoga"
    var intensity = "15 days"

    var isCalledFromHome = false

    lateinit var db:AppDatabase
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workoutplans_list)

        supportActionBar?.title = getString(R.string.select_base_plan)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        //init class
        init()

        var workoutPlansList = listOf<WorkoutPlansDbModel>()
        var filterByNamesList = listOf<String>("All Plans", "My Plans", "Fav Plans", "Beginner", "Intermediate", "Advanced")

        if(workoutType == AppConstants.ALL_PLANS){
            viewModel.getAllPlans(db)
        }else{
            //code runs in background thread
            viewModel.getAllAppSavedPlansByType(db, workoutType)
        }




        //invokes when exercises value change happen
        viewModel.plansList.observe(this, Observer { it ->
            workoutPlansList = it

            //setup workout plans list recycler view
            workoutPlansRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = MyWorkoutPlansListAdapter(workoutPlansList, this@WorkoutPlansListActivity)
            }

            //setup filter recycler view
            filterWorkoutHorizontalRecyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = MyFilterWorkoutPlansListAdapter(filterByNamesList, (workoutPlansRecyclerView.adapter as MyWorkoutPlansListAdapter))
            }



            //search bar listener - setup search bar
            search_bar.setOnQueryTextListener(object: SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    (workoutPlansRecyclerView.adapter as MyWorkoutPlansListAdapter).filter.filter(newText)
                    return false
                }
            })
        })

    }

    var isUserPlans = false
    fun init(){
        //get required intent
        workoutType = intent.getStringExtra(AppConstants.WORKOUT_TYPE)
        isCalledFromHome = intent.getBooleanExtra(AppConstants.IS_CALLED_FROM_HOME, false)

        //instantiate db
        instantialteDb()

        //setup ViewModel
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    private fun instantialteDb() {
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "HasterDb.db")
                .build()
    }

}



