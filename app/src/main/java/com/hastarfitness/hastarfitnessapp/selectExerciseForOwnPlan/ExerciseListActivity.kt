package com.hastarfitness.hastarfitnessapp.selectExerciseForOwnPlan

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.ViewModel
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.customDialogueToShowExerciseInfo.DlgShowExerciseInfo
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.database.ExerciseDbModel
import kotlinx.android.synthetic.main.activity_workoutplans_list.*

/**
 * activity for selecting exercise/s to be added in custom plan exercise list
 */
class ExerciseListActivity : AppCompatActivity() {
    //code for applying itemClickListener to recyclerView
    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }
    private fun RecyclerView.addOnItemClickListener(onClickListener: OnItemClickListener) {
        this.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {

            override fun onChildViewDetachedFromWindow(view: View) {
                view.setOnClickListener(null)
            }

            override fun onChildViewAttachedToWindow(view: View) {
                view.setOnClickListener {
                    val holder = getChildViewHolder(view)
                    onClickListener.onItemClicked(holder.adapterPosition, view)
                }
            }
        })
    }

    private lateinit var viewModel: ViewModel
    var workoutType = "yoga"
    var intensity = "15 days"

    lateinit var db: AppDatabase
    private lateinit var dialog: DlgShowExerciseInfo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workoutplans_list)

        supportActionBar?.title = "Select Exercises to add"

        //init class
        init()

        var workoutPlansList = listOf<ExerciseDbModel>()
        var workoutPlansFilterList = listOf<String>("All Exercises", "Beginner", "Intermediate", "Advanced")


        //code runs in background thread
        viewModel.getAllExercisesByType(db, workoutType)


        //invokes when exercises value change happen
        viewModel.exercises.observe(this, Observer { it ->
            workoutPlansList = it

            dialog.exerciseList = workoutPlansList
            workoutPlansRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = ExerciseListAdapter(workoutPlansList, this@ExerciseListActivity, dialog)
            }


            filterWorkoutHorizontalRecyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = ExerciseListHoriFilter(workoutPlansFilterList, (workoutPlansRecyclerView.adapter as ExerciseListAdapter))
            }

            //search bar listener
            search_bar.setOnQueryTextListener(object : SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    (workoutPlansRecyclerView.adapter as ExerciseListAdapter).filter.filter(newText)
                    return false
                }
            })
        })

    }

    fun init() {
        workoutType = intent.getStringExtra(AppConstants.WORKOUT_TYPE)!!

        instantialteDb()

        //setup ViewModel
        viewModel = ViewModelProviders.of(this).get(ViewModel()::class.java)

        //create dialogue
        dialog= DlgShowExerciseInfo(this, listOf<ExerciseDbModel>())
        dialog.create()
    }


    private fun instantialteDb() {
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "HasterDb.db")
                .build()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.done_or_save_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                Toast.makeText(applicationContext, "Saved", Toast.LENGTH_LONG).show()
                val exerciseList = (workoutPlansRecyclerView.adapter as ExerciseListAdapter).selectedItems
                val intent = Intent()
                intent.putExtra(AppConstants.EXERCISE_LIST, exerciseList)
                setResult(5, intent)
                finish() //finishing activity
            }
            else ->
                return super.onOptionsItemSelected(item);
        }
        return true
    }

    override fun onBackPressed() {
        Toast.makeText(applicationContext, "Saved", Toast.LENGTH_LONG).show()
        val exerciseList = (workoutPlansRecyclerView.adapter as ExerciseListAdapter).selectedItems
        val intent = Intent()
        intent.putExtra(AppConstants.EXERCISE_LIST, exerciseList)
        setResult(5, intent)
        finish()
    }
}



