package com.hastarfitness.hastarfitnessapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.customDialogueToDownloadVideos.DlgDownloadVideos
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.database.ExerciseDbModel
import com.hastarfitness.hastarfitnessapp.database.RestTimeModel
import kotlinx.android.synthetic.main.activity_timer.*


class TimerActivity : AppCompatActivity(), View.OnClickListener {

    var mAutoIncrement = false;
    var mAutoDecrement = false;

    //Repetition delay for increment
    val REP_DELAY: Long = 50
    lateinit var viewModel: ViewModel
    private var typeOfIntensity = ""
    private var typeOfWorkout = ""
    private var typeOfWorkoutSubType: String? = ""


    private lateinit var exerciseListArrayList: ArrayList<ExerciseDbModel>
    private lateinit var restTimeModel: RestTimeModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        //init class
        init()

        //setting up the initial state for clock
        seekbar_point.curProcess = 15
        circular_timer_text.text = seekbar_point.curProcess.toString()
        setButtonText()

        //handle seekbar changes
        seekbar_point.setOnSeekBarChangeListener { seekbar, curValue -> setButtonText() }

        /**
         * Inner class to handle long press button clock incrementation on new thread
         */
        val repeatUpdateHandler = Handler()

        class RptUpdater : Runnable {
            override fun run() {
                if (mAutoIncrement) {
                    increment()
                    repeatUpdateHandler.postDelayed(RptUpdater(), REP_DELAY)
                } else if (mAutoDecrement) {
                    decrement()
                    repeatUpdateHandler.postDelayed(RptUpdater(), REP_DELAY)
                }
            }
        }

        /**
         *  for incrementing the clock repeatedly on plus long click
         */
        plus.setOnLongClickListener {
            mAutoIncrement = true
            repeatUpdateHandler.post(RptUpdater())
            false
        }
        //Touch listener for the action up event on minus button
        //It tells system that button is released
        plus.setOnTouchListener { v, event ->
            if ((event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL)
                    && mAutoIncrement) {
                mAutoIncrement = false;
            }
            false;
        }

        /**
         * for decrementing the clock repeatedly on minus long click
         */
        minus.setOnLongClickListener {
            mAutoDecrement = true
            repeatUpdateHandler.post(RptUpdater())
            false
        }
        //Touch listener for the action up event on minus button
        //It tells system that button is released
        minus.setOnTouchListener { v, event ->
            if ((event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL)
                    && mAutoDecrement) {
                mAutoDecrement = false;
            }
            false;
        }

        /**
         * plus minus button click handling
         */
        plus.setOnClickListener {
            increment()
        }
        minus.setOnClickListener {
            decrement()
        }
    }

    private fun init() {
        typeOfIntensity = intent.getStringExtra(AppConstants.INTENSITY)!!
        typeOfWorkout = intent.getStringExtra(AppConstants.WORKOUT_TYPE)!!
        typeOfWorkoutSubType = intent.getStringExtra(AppConstants.WORKOUT_SUB_TYPE)
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)
        instantiateDb()
        collectWorkoutData()

        dlgDownloadVideos = DlgDownloadVideos(applicationContext, mutableListOf<String>())

    }

    lateinit var db: AppDatabase
    private fun instantiateDb() {
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "HasterDb.db")
                .build()
    }

    private fun collectWorkoutData() {

        if (typeOfWorkout.toLowerCase() == AppConstants.BODY_WEIGHT) {
            viewModel.setup(db, typeOfWorkout, null)
            viewModel.getRest(db, typeOfWorkout, typeOfIntensity)

            viewModel.exercises.observe(this, Observer { list ->
                //first observe exercise list and collect it in exerCiselistArrayList

                var exerciseListFrmDb = list

                exerciseListFrmDb = exerciseListFrmDb.filter { it.intensity.toLowerCase().contains(typeOfWorkoutSubType!!.toLowerCase()) }
                exerciseListFrmDb = exerciseListFrmDb.filter { it.intensity.toLowerCase().contains(typeOfIntensity.toLowerCase()) }
                exerciseListArrayList = ArrayList<ExerciseDbModel>(exerciseListFrmDb)


            })

            viewModel.restTime.observe(this, Observer {
                //now observe rest time model var and collect it in resttime
                restTimeModel = it

            })

        } else {
            //get exercises from type of exercise or intensity
            viewModel.setup(db, typeOfWorkout, typeOfIntensity)
            viewModel.getRest(db, typeOfWorkout, typeOfIntensity)

            viewModel.exercises.observe(this, Observer { list ->
                //first observe exercise list and collect it in exerciseListArrayList
                exerciseListArrayList = ArrayList<ExerciseDbModel>(list)
            })

            viewModel.restTime.observe(this, Observer {
                //now observe rest time model var and collect it in restTime
                restTimeModel = it
            })
        }


    }


    override fun onClick(v: View?) {
        val timeSelected = seekbar_point.curProcess
        if (timeSelected >= 4) {
            val noOfExerciseSlots = calcNoOfexerciceSlots()
            val isShuffleEnabled = shuffle_switch.isChecked

            val i = Intent(this@TimerActivity, ActivityStartExercise::class.java)

            i.putExtra(AppConstants.WORKOUT_TIME_IN_MINUTES, timeSelected)

            i.putExtra(AppConstants.TOTAL_EXERCISE_SLOTS, noOfExerciseSlots)
            i.putExtra(AppConstants.REST_TIME_IN_SECONDS, restTimeModel.restTime)
            i.putExtra(AppConstants.REST_INTERVAL, restTimeModel.numberOfExerciseAfter)
            i.putParcelableArrayListExtra(AppConstants.EXERCISE_LIST, exerciseListArrayList)
            i.putExtra(AppConstants.IS_SUGGESTED_PLAN, false)
            i.putExtra(AppConstants.IS_SHUFFLE_ENABLED, isShuffleEnabled)
            extractExerciseFileNamesFromList(exerciseListArrayList.toList(), i)
//            startActivity(i)
        } else {
            Toast.makeText(this, "Please select at least 4 minutes", Toast.LENGTH_SHORT).show()
        }

    }

    private fun calcNoOfexerciceSlots(): Int {
        var totalWorkoutTimeInSec = seekbar_point.curProcess * 60
        val timeForOneExerciseSec = exerciseListArrayList[0].time.toLong()
        val restTimeInSec = restTimeModel.restTime
        val interval = restTimeModel.numberOfExerciseAfter
        var intervalCur = interval


//        val noOfSlots = totalWorkoutTimeInSec / timeForOneExerciseSec
//
//        val noOfRestSlots = noOfSlots / interval
////        noOfRestSlots--
//        totalWorkoutTimeInSec -= (noOfRestSlots * restTime)
//
//
//        val noOfExerciseSlots: Double = totalWorkoutTimeInSec.toDouble() / timeForOneExerciseSec
//        return noOfExerciseSlots.toInt() + 1

        var rest = 0
        var normal:Long = 0
        var isRest = false
        var restCounter = 0
        var normalCounter = 0
        for(i in totalWorkoutTimeInSec downTo 0){

            if(!isRest){
                normal++
                if(normal == timeForOneExerciseSec){
                    intervalCur--
                    normalCounter++
                    normal = 0
                }
                if(intervalCur == 0){
                    isRest = true
                }
            }else if(isRest){
                rest++
                intervalCur = interval
                if(rest == restTimeInSec){
                    restCounter++
                    rest = 0
                    isRest = false
                }
            }
        }
        if(normal > 1.toLong()){
            normalCounter++
        }
        return normalCounter


    }

    fun calcTime(restTime: RestTimeModel, totalExercisesNo: Int): Int {

        val totalWorkoutTimeInMins = seekbar_point.curProcess
        val totalWorkoutTime = totalWorkoutTimeInMins * 60
        val restTimeOfOneExercise = restTime.restTime
        val interval = restTime.numberOfExerciseAfter
        var totalRestNo = totalExercisesNo / interval
        totalRestNo -= 1
        val totalRestTime = totalRestNo * restTimeOfOneExercise
        val totalTimeRemaining = totalWorkoutTime - totalRestTime

        return totalTimeRemaining / totalExercisesNo

    }

    //function to increment the timer
    fun increment() {
        seekbar_point.curProcess++
        setButtonText()
    }

    //function to decrement the timer
    fun decrement() {
        if (seekbar_point.curProcess != 0) {
            seekbar_point.curProcess--
            setButtonText()
        }
    }

    private fun setButtonText() {
        val curProcess = seekbar_point.curProcess
        circular_timer_text.text = curProcess.toString()
        start_workout.text = "Select $curProcess minutes"
    }
    private lateinit var dlgDownloadVideos:DlgDownloadVideos
    private fun extractExerciseFileNamesFromList(exerciseList: List<ExerciseDbModel>, i:Intent){
        val exerciseNamesList = mutableListOf<String>()
        for(exercise in exerciseList){
            exerciseNamesList.add(exercise.name + ".mp4")
        }
        var remFiles = mutableListOf<String>()

        remFiles = checkWhichFilesExistsAndReturnWhichDoNot(exerciseNamesList)
        if(remFiles.size == 0){
            startActivity(i)
        }else{
            dlgDownloadVideos =  DlgDownloadVideos(this@TimerActivity, remFiles)
            dlgDownloadVideos.create()
            dlgDownloadVideos.show()
        }

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



}
