package com.hastarfitness.hastarfitnessapp.finalExerciseListBeforeStartingWorkout

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.hastarfitness.hastarfitnessapp.ActivityStartExercise
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.ViewModel
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.createYourOwnPlan.CreateYourOwnPlanActivity
import com.hastarfitness.hastarfitnessapp.customDialogueToDownloadVideos.DlgDownloadVideos
import com.hastarfitness.hastarfitnessapp.customDialogueToShowExerciseInfo.DlgShowExerciseInfo
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.database.ExerciseDbModel
import com.hastarfitness.hastarfitnessapp.database.RestTimeModel
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import kotlinx.android.synthetic.main.activity_final_exercise_list.*
import java.lang.Exception

/**
 * activity to show final exercises to user that he is going to perform
 *
 * @author Bhushan Kadu
 */
class FinalExerciseListActivity : AppCompatActivity() {

    private lateinit var viewModel: ViewModel
    var workoutType = "yoga"
    var workoutSubType = "yoga"
    var totalWorkoutTime = 0
    var intensity = "15 days"
    private var isSuggestedPlan = false
    lateinit var exerciseList: List<ExerciseDbModel>

    lateinit var session: Session
    var planId = -1
    lateinit var db: AppDatabase
    private lateinit var dialog: DlgShowExerciseInfo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final_exercise_list)

        supportActionBar?.title = "Final Exercises"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //init class
        init()

        //setup recycler view
        populateList()

    }
    lateinit var dlg:DlgDownloadVideos
    fun init() {
        session = Session(this)
        try {
            isSuggestedPlan = intent.getBooleanExtra(AppConstants.IS_SUGGESTED_PLAN, false)

            if (isSuggestedPlan) {
                workoutSubType = intent.getStringExtra(AppConstants.WORKOUT_SUB_TYPE)
            } else {
                planId = intent.getIntExtra(AppConstants.WORKOUT_PLAN_ID, -1)
            }

            workoutType = intent.getStringExtra(AppConstants.WORKOUT_TYPE)
            if(workoutType == AppConstants.BODY_WEIGHT) {
                intensity = intent.getStringExtra(AppConstants.INTENSITY)
            }
            totalWorkoutTime = intent.getIntExtra(AppConstants.WORKOUT_TIME_IN_MINUTES, 10)

            session.isSuggestedPlan = isSuggestedPlan
            session.planId = planId
            session.totalWorkoutTime = totalWorkoutTime
            session.workoutSubType = workoutSubType
            session.finalWorkoutType = workoutType
            session.finalIntensity = intensity
        } catch (e: Exception) {
            isSuggestedPlan = session.isSuggestedPlan!!
            workoutSubType = session.workoutSubType!!
            planId = session.planId!!
            totalWorkoutTime = session.totalWorkoutTime!!
            intensity = session.finalIntensity!!
            workoutType = session.finalWorkoutType!!
        }

        //inst. db.
        instantiateDb()

        //setup ViewModel
        viewModel = ViewModelProviders.of(this).get(ViewModel()::class.java)

        //create dialogue
        dialog = DlgShowExerciseInfo(this, listOf<ExerciseDbModel>())
        dialog.create()

        dlg = DlgDownloadVideos(this@FinalExerciseListActivity, mutableListOf<String>())
        dlg.setOnDismissListener {
            if((it as DlgDownloadVideos).isDownloaded){
                workoutStart()
            }
        }


    }

    private fun instantiateDb() {
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "HasterDb.db")
                .build()
    }

    //populate recycler view
    private fun populateList() {
        if (!isSuggestedPlan) {
            if(workoutType == AppConstants.BODY_WEIGHT){
                viewModel.getPlan(db, planId, true)
            }else{
                viewModel.getPlan(db, planId, false)
            }

            viewModel.fullPlanBodyWeight.observe(this, Observer { it ->
                exerciseList = it.map {
                    ExerciseDbModel(
                            it.id,
                            it.name,
                            it.type,
                            it.desc,
                            " ",
                            it.intensity,
                            it.time,
                            it.mmet,
                            it.fmet
                    )
                }
                dialog.exerciseList = exerciseList
                workoutPlansRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = FinalExerciseListAdapter(exerciseList, this@FinalExerciseListActivity, dialog)
                }

                start.setOnClickListener {
                    extractExerciseFileNamesFromList()
                }

            })
            viewModel.fullPlanCardio.observe(this, Observer { it ->
                exerciseList = it.map {
                    ExerciseDbModel(
                            it.id,
                            it.name,
                            it.type,
                            it.desc,
                            " ",
                            it.intensity,
                            it.time,
                            it.mmet,
                            it.fmet
                    )
                }
                dialog.exerciseList = exerciseList
                workoutPlansRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = FinalExerciseListAdapter(exerciseList, this@FinalExerciseListActivity, dialog)
                }

                start.setOnClickListener {
                    extractExerciseFileNamesFromList()
                }

            })
        } else {
            if(workoutType == AppConstants.CARDIO){
                viewModel.selectAllFromFinalCardioExerciseDbModel(db)
            }else{
                viewModel.selectAllFromFinalBodyWeightExerciseDbModel(db)
            }
            viewModel.finalCardioExercisesDbModelList.observe(this, Observer { it ->

                exerciseList = it.map {
                    ExerciseDbModel(
                            it.id,
                            it.name,
                            it.type,
                            it.desc,
                            " ",
                            it.intensity,
                            it.time,
                            it.mmet,
                            it.fmet
                    )
                }
                dialog.exerciseList = exerciseList
                workoutPlansRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = FinalExerciseListAdapter(exerciseList, this@FinalExerciseListActivity, dialog)
                }
                start.setOnClickListener {
                    extractExerciseFileNamesFromList()
                }

            })
            viewModel.finalBodyWeightExercisesDbModelList.observe(this, Observer { it ->

                exerciseList = it.map {
                    ExerciseDbModel(
                            it.id,
                            it.name,
                            it.type,
                            it.desc,
                            " ",
                            it.intensity,
                            it.time,
                            it.mmet,
                            it.fmet
                    )
                }
                dialog.exerciseList = exerciseList
                workoutPlansRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = FinalExerciseListAdapter(exerciseList, this@FinalExerciseListActivity, dialog)
                }
                start.setOnClickListener {
                    extractExerciseFileNamesFromList()
                }

            })
        }
    }

    //start the workout
    private fun workoutStart() {
        val i = Intent(this@FinalExerciseListActivity, ActivityStartExercise::class.java)

        if(workoutType == AppConstants.BODY_WEIGHT){
            viewModel.getRest(db, workoutType.toLowerCase(), intensity.toLowerCase())
        }else{
            viewModel.getRest(db, workoutType.toLowerCase(), workoutSubType.toLowerCase())
        }
        viewModel.restTime.observe(this, Observer { restTimeModel ->
            val finalExercisesDbModelList = exerciseList.map {
                ExerciseDbModel(it.id,
                        it.name,
                        it.type,
                        it.desc,
                        it.img,
                        it.intensity,
                        it.time,
                        it.fmet,
                        it.mmet)
            } as ArrayList<ExerciseDbModel>

            i.putExtra(AppConstants.REST_TIME_IN_SECONDS, restTimeModel.restTime)
            i.putExtra(AppConstants.REST_INTERVAL, restTimeModel.numberOfExerciseAfter)
            i.putExtra(AppConstants.TOTAL_EXERCISE_SLOTS, calcNoOfExerciseSlots(restTimeModel))
            i.putExtra(AppConstants.WORKOUT_TIME_IN_MINUTES, totalWorkoutTime)
            i.putParcelableArrayListExtra(AppConstants.EXERCISE_LIST, finalExercisesDbModelList);
            startActivity(i)
        })

    }

    private fun extractExerciseFileNamesFromList(){
        val exerciseNamesList = mutableListOf<String>()
        for(exercise in exerciseList){
            exerciseNamesList.add(exercise.name + ".mp4")
        }
        var remFiles = mutableListOf<String>()

        remFiles = checkWhichFilesExistsAndReturnWhichDoNot(exerciseNamesList)
        if(remFiles.size == 0){
            workoutStart()
        }else{
            dlg = DlgDownloadVideos(this@FinalExerciseListActivity, remFiles)
            dlg.create()
            dlg.show()
        }


        //start workout btn click listener
    }
    private fun checkWhichFilesExistsAndReturnWhichDoNot(fileNames: List<String>): MutableList<String> {
        val fileNotExistList = mutableListOf<String>()
        for (file in fileNames) {
            if (!fileExist(file)) {
                fileNotExistList.add(file)
            }
        }
        return fileNotExistList
    }
    private fun fileExist(fName: String?): Boolean {
        val file = getFileStreamPath(fName)
        return file.exists()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        if (isSuggestedPlan) {
            menuInflater.inflate(R.menu.final_exerciselist_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->{
                onBackPressed()
            }
            R.id.edit_plan -> {
                val i = Intent(applicationContext, CreateYourOwnPlanActivity::class.java)
                i.putExtra(AppConstants.INTENSITY, intensity)
                i.putExtra(AppConstants.WORKOUT_SUB_TYPE, workoutSubType)
                i.putExtra(AppConstants.WORKOUT_TYPE, workoutType)
                i.putExtra(AppConstants.IS_CUSTOMIZE, true)
                startActivityForResult(i, 5)
            }
            R.id.restore -> {
                //restore list to original
                if(workoutType == AppConstants.BODY_WEIGHT){
                    generateFinalExerciseTableAndPopulateListBodyWeight()
                }else{
                    generateFinalExerciseTableAndPopulateListCardio()
                }
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            5 -> {
                populateList()
            }
        }
    }

    private fun calcNoOfExerciseSlots(restTimeModel: RestTimeModel): Int {
        var totalWorkoutTimeInSec = totalWorkoutTime * 60
        val timeForOneExerciseSec = exerciseList[0].time
        val restTimeInSec = restTimeModel.restTime
        val interval = restTimeModel.numberOfExerciseAfter
        var intervalCur = interval
//
//
//        var noOfSlots = totalWorkoutTimeInSec / timeForOneExerciseSec
//
//        var noOfRestSlots = noOfSlots / interval
//        totalWorkoutTimeInSec -= (noOfRestSlots * restTime)
//
//
//        val noOfExerciseSlots: Double = totalWorkoutTimeInSec.toDouble() / timeForOneExerciseSec
//        return noOfExerciseSlots.toInt() + 1

        var rest = 0
        var normal = 0
        var isRest = false
        var restCounter = 0
        var normalCounter = 0
        for (i in totalWorkoutTimeInSec downTo 0) {

            if (!isRest) {
                normal++
                if (normal == timeForOneExerciseSec) {
                    intervalCur--
                    normalCounter++
                    normal = 0
                }
                if (intervalCur == 0) {
                    isRest = true
                }
            } else if (isRest) {
                rest++
                intervalCur = interval
                if (rest == restTimeInSec) {
                    restCounter++
                    rest = 0
                    isRest = false
                }
            }
        }
        if (normal > 1) {
            normalCounter++
        }
        return normalCounter

    }

    private fun generateFinalExerciseTableAndPopulateListBodyWeight() {
        var exerciseList: ArrayList<ExerciseDbModel>
        val session = Session(this)
        val workoutSubType = AppConstants.dailyPlanBodyWeight[session.day]

        viewModel.getSinglePlanByName(db, "${intensity}_${workoutSubType!!.split(" ").joinToString("")}", true)
        viewModel.singleBodyWeightPlan.observe(this, Observer { it ->
            viewModel.getPlan(db, it.id, true)
            viewModel.fullPlanBodyWeight.observe(this, Observer { its ->

                exerciseList = its.map {
                    ExerciseDbModel(
                            it.id,
                            it.name,
                            it.type,
                            it.desc,
                            " ",
                            it.intensity,
                            it.time,
                            it.mmet,
                            it.fmet)
                } as ArrayList<ExerciseDbModel>

                viewModel.deleteExercisesFromFinalBodyWeightExTable(db)
                viewModel.deletedRowsCardio.observe(this, Observer {
                    it
                    viewModel.insertFinalExercisesBodyWeight(db, exerciseList)
                })
                viewModel.insertedRowsListBodyWeight.observe(this, Observer {
                    //populate list after rows has been added
                    populateList()
                })
            })
        })
    }

    private fun generateFinalExerciseTableAndPopulateListCardio() {
        var exerciseList: ArrayList<ExerciseDbModel>
        val session = Session(this)

        viewModel.getSinglePlanByName(db, this@FinalExerciseListActivity.workoutSubType.toLowerCase().split(" ").joinToString(""), false)
        viewModel.singleCardioPlan.observe(this, Observer { it ->
            viewModel.getPlan(db, it.id, false)
            viewModel.fullPlanCardio.observe(this, Observer { its ->

                exerciseList = its.map {
                    ExerciseDbModel(
                            it.id,
                            it.name,
                            it.type,
                            it.desc,
                            " ",
                            it.intensity,
                            it.time,
                            it.mmet,
                            it.fmet)
                } as ArrayList<ExerciseDbModel>

                viewModel.deleteExercisesFromFinalCardioExTable(db)
                viewModel.deletedRowsCardio.observe(this, Observer {
                    it
                    viewModel.insertFinalExercisesCardio(db, exerciseList)
                })
                viewModel.insertedRowsListCardio.observe(this, Observer {
                    //populate list after rows has been added
                    populateList()
                })
            })
        })
    }
}



