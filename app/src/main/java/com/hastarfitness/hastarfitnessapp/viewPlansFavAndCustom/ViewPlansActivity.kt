package com.hastarfitness.hastarfitnessapp.viewPlansFavAndCustom

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
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
import com.hastarfitness.hastarfitnessapp.selectBaseWorkoutPlan.WorkoutPlansListActivity
import kotlinx.android.synthetic.main.activity_searched_and_custom_food_list.*
import kotlinx.android.synthetic.main.activity_workoutplans_list.*
import kotlinx.android.synthetic.main.activity_workoutplans_list.rootLinearLayout
import kotlinx.android.synthetic.main.activity_workoutplans_list.search_bar

/**
 *  activity to select base plan
 *
 *  @author Bhushan Kadu
 */
val filterByNamesListCustomPlans = mutableListOf<String>("All Plans", "Upper Body", "Lower Body", "Core Strength", "Full Body", "Favourites")
val filterByNamesListFavouritePlans = mutableListOf<String>("All Plans", "Upper Body", "Lower Body", "Core Strength", "Full Body")
class ViewPlansActivity : AppCompatActivity() {

    lateinit var viewModel: ViewModel
    private var filterBy = "yoga"
    var intensity = "15 days"

    lateinit var db:AppDatabase
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workoutplans_list)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        rootLinearLayout.requestFocus()

        //init class
        init()

        var workoutPlansList = listOf<WorkoutPlansDbModel>()

        if(filterBy == AppConstants.FAVOURITE_PLANS){
            viewModel.getAllFavPlans(db)
        }else{
            //code runs in background thread
            viewModel.getAllUserPlans(db)
        }


        //invokes when exercises value change happen
        viewModel.plansList.observe(this, Observer { it ->
            workoutPlansList = it

            //setup workout plans list recycler view
            workoutPlansRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = ViewPlansAdapter(workoutPlansList, this@ViewPlansActivity)
            }
            if((workoutPlansRecyclerView.adapter as ViewPlansAdapter).exerciseFilterList.size == 0
                    && filterBy == AppConstants.MY_PLANS){
                placeHolder_layout_customPlans.visibility = View.VISIBLE
            }else if((workoutPlansRecyclerView.adapter as ViewPlansAdapter).exerciseFilterList.size == 0
                    && filterBy == AppConstants.FAVOURITE_PLANS){
                placeHolder_layout_FavPlans.visibility = View.VISIBLE
            }

            //setup filter recycler view
            filterWorkoutHorizontalRecyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = if(filterBy == AppConstants.FAVOURITE_PLANS){
                    ViewPlansFilterAdapter(filterByNamesListFavouritePlans, (workoutPlansRecyclerView.adapter as ViewPlansAdapter))
                }else{
                    ViewPlansFilterAdapter(filterByNamesListCustomPlans, (workoutPlansRecyclerView.adapter as ViewPlansAdapter))
                }
            }

            //search bar listener - setup search bar
            search_bar.setOnQueryTextListener(object: SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    (workoutPlansRecyclerView.adapter as ViewPlansAdapter).filter.filter(newText)
                    return false
                }
            })
        })

        createPlan_button.setOnClickListener {
            creteNewPlan()
        }

    }
    fun init(){
        //get required intent
//        workoutType = intent.getStringExtra(AppConstants.WORKOUT_TYPE)
        filterBy = intent.getStringExtra(AppConstants.FILTER_BY)!!


        if(filterBy == AppConstants.MY_PLANS){
            supportActionBar?.title = "My Custom Plans"
        }else if(filterBy == AppConstants.FAVOURITE_PLANS){
            supportActionBar?.title = "My Favourite Plans"
        }


        //instantiate db
        instantiateDb()

        //setup ViewModel
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(filterBy == AppConstants.MY_PLANS){
            menuInflater.inflate(R.menu.select_plan_for_workout_activity_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.add_plan -> {
                creteNewPlan()
            }
            android.R.id.home -> onBackPressed();

        }
        return true
    }

    //create new plan button click
    private fun creteNewPlan() {
        val i = Intent(applicationContext, WorkoutPlansListActivity::class.java)
        i.putExtra(AppConstants.WORKOUT_TYPE, getWorkoutType())
        i.putExtra(AppConstants.IS_CALLED_FROM_HOME, true)
        startActivity(i)
    }

    private fun getWorkoutType():String{
        val adapter = filterWorkoutHorizontalRecyclerView.adapter as ViewPlansFilterAdapter
        val selectedItemNumber = adapter.selectedItem
        return filterByNamesListFavouritePlans[selectedItemNumber]
    }


    private fun instantiateDb() {
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "HasterDb.db")
                .build()
    }

}



