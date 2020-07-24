package com.hastarfitness.hastarfitnessapp.createYourOwnPlan

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.hastarfitness.hastarfitnessapp.models.Exercise
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.ViewModel
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.customDialogueToGetPlanNameInfo.DlgGetCustPlanInfoFrmUser
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.database.ExerciseDbModel
import com.hastarfitness.hastarfitnessapp.database.PlanExercisesDbModel
import com.hastarfitness.hastarfitnessapp.database.WorkoutPlansDbModel
import com.hastarfitness.hastarfitnessapp.selectExerciseForOwnPlan.ExerciseListActivity
import com.hastarfitness.hastarfitnessapp.selectPlanForDailyWorkout.SelectPlanForDailyWorkoutActivity
import kotlinx.android.synthetic.main.activity_create_your_plan.*
import kotlinx.android.synthetic.main.dlg_get_custom_plan_info_dlg.*

/**
 * this class is used for showing user a activity to customize a predefined plan or
 * to create a new custom plan
 */
class CreateYourOwnPlanActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var infoDlg: DlgGetCustPlanInfoFrmUser

    private lateinit var viewModel: ViewModel
    var workoutSubType = ""
    var workoutType = ""
    var intensity = ""
    var planId: Int? = null
    var isCustomize: Boolean = false

    lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_your_plan)

        //set action bar manually
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Edit Plan"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //init class
        init()

        var workoutPlansList = listOf<Exercise>()

        //if plan id is not null that means user called activity from create new plan
        //else it is called from customize plan
        if (planId != null) {
            viewModel.getSinglePlanById(db, planId!!)
        } else {
            if(workoutType == AppConstants.CARDIO) viewModel.selectExercisesFromFinalCardioExerciseDbModelByType(db)
            else viewModel.selectExercisesFromFinalBodyWeightExerciseDbModelByType(db)
        }

        //observe final exercises which come from FinalBodyWeightDbModelTable or FinalCardioDbModelTable
        viewModel.finalCardioExercisesDbModelList.observe(this, Observer { it ->

            val exerciseList = it.map {
                Exercise(
                        it.name,
                        it.type,
                        it.desc,
                        " ",
                        it.intensity,
                        it.id,
                        it.time,
                        it.mmet,
                        it.fmet,
                        false)
            } as ArrayList<Exercise>
            //setup recycler view
            workoutPlansRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = ExercisesListAdapter(exerciseList, this@CreateYourOwnPlanActivity)
            }
        })
        viewModel.finalBodyWeightExercisesDbModelList.observe(this, Observer { it ->

            val exerciseList = it.map {
                Exercise(
                        it.name,
                        it.type,
                        it.desc,
                        " ",
                        it.intensity,
                        it.id,
                        it.time,
                        it.mmet,
                        it.fmet,
                        false)
            } as ArrayList<Exercise>
            //setup recycler view
            workoutPlansRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = ExercisesListAdapter(exerciseList, this@CreateYourOwnPlanActivity)
            }
        })
        //if create plan then observe this
        //1) first get the plan id from name from WorkoutPlanDbModel table
        //2) then get the exercises in the plan using plan id from PlanExerciseDbModelTable
        viewModel.singleBodyWeightPlan.observe(this, Observer { it ->
            viewModel.getPlan(db, it.id, true)
            viewModel.fullPlanBodyWeight.observe(this, Observer { its ->

                workoutPlansList = its.map {
                    Exercise(
                            it.name,
                            it.type,
                            it.desc,
                            " ",
                            it.intensity,
                            it.exerciseId,
                            it.time,
                            it.mmet,
                            it.fmet,
                            false)
                } as ArrayList<Exercise>

                workoutPlansRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = ExercisesListAdapter(workoutPlansList, this@CreateYourOwnPlanActivity)
                }
            })
        })
    }

    fun init() {
        try {
            //get this parameters if user calls this activity from customize plan
            intensity = intent.getStringExtra(AppConstants.INTENSITY)!!
            workoutSubType = intent.getStringExtra(AppConstants.WORKOUT_SUB_TYPE)!!
            workoutType = intent.getStringExtra(AppConstants.WORKOUT_TYPE)!!
            isCustomize = intent.getBooleanExtra(AppConstants.IS_CUSTOMIZE, false)
        } catch (e: Exception) {
            //get this params if user is calling this activity from create new plan
            planId = intent.getIntExtra(AppConstants.WORKOUT_PLAN_ID, 0)
            workoutSubType = intent.getStringExtra(AppConstants.WORKOUT_TYPE)!!
        }


        //Instantiate db
        instantiateDb()

        //setup ViewModel
        viewModel = ViewModelProviders.of(this).get(ViewModel()::class.java)

        //create and instantiate info dialogue that is going to used for saving custom plan
        infoDlg = DlgGetCustPlanInfoFrmUser(this)
        infoDlg.create()
        infoDlg.setOnDismissListener {
            if(infoDlg.isSave){
                Toast.makeText(this, "saved", Toast.LENGTH_LONG).show()
                val name = infoDlg.planNameEditText.text.toString().trim()
                val desc = infoDlg.planDescEditText.text.toString().trim()
                savePlan(name, desc)
                startActivity(Intent(applicationContext, SelectPlanForDailyWorkoutActivity::class.java))
            }
        }

        infoDlg.cancel_action?.setOnClickListener {
            infoDlg.dismiss()
        }

        //set click listener
        addExercisesFromLibrary.setOnClickListener(this)
    }


    /**
     * instantiates the db variable
     */
    private fun instantiateDb() {
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "HasterDb.db")
                .build()
    }

    //inflate the options menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.create_your_plan_menu, menu)
        return true
    }

    //implement menu item click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->{
                onBackPressed()
            }
            //for saving plan
            R.id.save_plans -> {
                if (isCustomize) {
                    if(workoutType == AppConstants.BODY_WEIGHT) saveCustomizationsBodyWeight()
                    else saveCustomizationsCardio()
                } else {
                    infoDlg.show()
                }
            }
            //for undo removal of any exercise from list
            R.id.undo_remove -> {
                val adapter = (workoutPlansRecyclerView.adapter as ExercisesListAdapter)
                val removedItemList = adapter.removedList

                if (removedItemList.size != 0) {
                    val lastItem = removedItemList.last()

                    if (lastItem != null) {
                        adapter.exerciseFilterList.add(lastItem.first, lastItem.second)
                        removedItemList.remove(lastItem)
                        adapter.notifyItemInserted(lastItem.first)
                        workoutPlansRecyclerView.layoutManager?.scrollToPosition(lastItem.first)
                        adapter.notifyItemRangeChanged(lastItem.first, adapter.exerciseFilterList.size)
                    }
                }

            }
            else ->
                return super.onOptionsItemSelected(item);
        }
        return true
    }

    /**
     * save user customization to db and return back to previous activity
     */
    private fun saveCustomizationsBodyWeight() {
        val adapter = (workoutPlansRecyclerView.adapter as ExercisesListAdapter)
        val values = adapter.exerciseFilterList
        val exerciseList = values.map {
            ExerciseDbModel(0,
                    it.name,
                    it.type,
                    it.desc,
                    it.img,
                    it.intensity,
                    it.time,
                    it.fmet,
                    it.mmet)
        }

        //first delete old records
        viewModel.deleteExercisesFromFinalBodyWeightExTable(db)
        viewModel.deletedRowsBodyWeight.observe(this, Observer {
            //now insert records gathered from list
            viewModel.insertFinalExercisesBodyWeight(db, exerciseList)
        })
        viewModel.insertedRowsListBodyWeight.observe(this, Observer {
            Toast.makeText(this, "saved", Toast.LENGTH_LONG).show()
            val returnIntent = Intent()
            setResult(5, returnIntent)
            finish()
        })
    }

    /**
     * save user customization to db and return back to previous activity
     */
    private fun saveCustomizationsCardio() {
        val adapter = (workoutPlansRecyclerView.adapter as ExercisesListAdapter)
        val values = adapter.exerciseFilterList
        val exerciseList = values.map {
            ExerciseDbModel(0,
                    it.name,
                    it.type,
                    it.desc,
                    it.img,
                    it.intensity,
                    it.time,
                    it.fmet,
                    it.mmet)
        }

        //first delete old records
        viewModel.deleteExercisesFromFinalCardioExTable(db)
        viewModel.deletedRowsCardio.observe(this, Observer {
            //now insert records gathered from list
            viewModel.insertFinalExercisesCardio(db, exerciseList)
        })
        viewModel.insertedRowsListCardio.observe(this, Observer {
            Toast.makeText(this, "saved", Toast.LENGTH_LONG).show()
            val returnIntent = Intent()
            setResult(5, returnIntent)
            finish()
        })
    }

    /**
     *  saves a customized plan to db
     *
     *  @param name name of the plan
     *  @param desc description of the plan
     */
    private fun savePlan(name: String, desc: String) {
        val adapter = (workoutPlansRecyclerView.adapter as ExercisesListAdapter)
        var exerciseListToBeAdded = mutableListOf<PlanExercisesDbModel>()
        val values = adapter.exerciseFilterList

        viewModel.insertCustomUserPlan(db, WorkoutPlansDbModel(0, name, desc, "none", workoutSubType, 1, 0))
        viewModel.insertedRecId.observe(this, Observer {
            for (itr in values) {
                exerciseListToBeAdded.add(PlanExercisesDbModel(0, itr.id, it.toInt()))
            }
            viewModel.insertCustomUserPlanExercises(db, exerciseListToBeAdded)
        })
    }

    //handle activity result when user select any exercise/exercises from library or exercise List
    //activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.RESULT_OK -> {
                val exerciseFilterList = data?.getParcelableArrayListExtra<Exercise>(AppConstants.EXERCISE_LIST)
                if ((exerciseFilterList != null) || exerciseFilterList?.size != 0) {
                    val adapter = (workoutPlansRecyclerView.adapter as ExercisesListAdapter)

                    //add list got from intent and notify adapter for data change
                    adapter.exerciseFilterList.addAll(exerciseFilterList!!)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.addExercisesFromLibrary->{
                val i = Intent(applicationContext, ExerciseListActivity::class.java)
               if(workoutType == AppConstants.BODY_WEIGHT) i.putExtra(AppConstants.WORKOUT_TYPE, workoutSubType)
                else i.putExtra(AppConstants.WORKOUT_TYPE, workoutType)
                startActivityForResult(i, AppConstants.RESULT_OK)
            }
        }
    }


}



