package com.hastarfitness.hastarfitnessapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log.v
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.countDownTimerWithPause.CountDownTimerWithPause
import com.hastarfitness.hastarfitnessapp.settings.DlgSetRestTime
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.database.ExerciseDbModel
import com.hastarfitness.hastarfitnessapp.database.RestTimeModel
import com.hastarfitness.hastarfitnessapp.database.UserDailyDataDbModel
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import com.hastarfitness.hastarfitnessapp.selectPlanForDailyWorkout.SelectPlanForDailyWorkoutActivity
import kotlinx.android.synthetic.main.activity_start_exercise.*
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

/**
 * this activity is for starting the workout session
 *
 * @author Bhushan Kadu
 */

class ActivityStartExercise : AppCompatActivity(), View.OnClickListener {

    private lateinit var session: Session
    var currentInterval = 1
    private var noOfExerciseSlots = 0
    var isRestTime = false
    private var isFinalTimer = false
    private var isInitialTimer = true
    lateinit var timer: CountDownTimerWithPause
    lateinit var restTimer: CountDownTimerWithPause
    lateinit var initialTimer: CountDownTimerWithPause
    lateinit var finalTimer: CountDownTimerWithPause
    private lateinit var mainTimer: CountDownTimer
    private lateinit var workoutList: ArrayList<ExerciseDbModel>
    var currentExercise = 0
    private var isActivityInPauseState = false
    var restInterval = 5
    var restTime: Long = 10000
    var initialTime: Long = 6000
    private var timeRemaining: Long = 10000
    var timerInitialTime: Long = 10000//10 seconds
    var totalWorkoutTime: Long = 15000//15 seconds
    var timeOfActivityPerformedSec: Int = 0//15 seconds
    //1 second = 1000 milliseconds

    var isSuggestedPlan = true
    var isShuffleEnabled = false

    var timePerExerciseHashMap: HashMap<Int, Int> = HashMap()

    //last time user clicked
    private var mLastClickTime: Long = 0

    private lateinit var viewModel: ViewModel
    lateinit var db: AppDatabase

    private lateinit var dlgSetRestTime: DlgSetRestTime
    lateinit var restTimeModel: RestTimeModel




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_exercise)
        setSupportActionBar(toolbar)

        //init class
        init()

        //load gif
        val pathUri = ""
        loadBitMapAndGif(pathUri)


        Calendar.getInstance().timeInMillis = Calendar.getInstance().timeInMillis

        //set click listeners
        play_pause_button.setOnClickListener(this)
        next_button.setOnClickListener(this)
        back_button.setOnClickListener(this)
        end_exercise.setOnClickListener(this)
    }

    //initialize the class
    private fun init() {

//        videoUri = "android.resource://" + packageName + "/" + R.raw.pushups


        //Instantiate db
        instantiateDb()

        //setup ViewModel
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)

        //init session variable
        session = Session(this)


        roundingFormat = DecimalFormat("#.##")
        roundingFormat.roundingMode = RoundingMode.CEILING

        val i = intent
        //get all the required values sent from parent activity
        val time = i.getIntExtra(AppConstants.WORKOUT_TIME_IN_MINUTES, 15)
        val rest = i.getIntExtra(AppConstants.REST_TIME_IN_SECONDS, 30)
        val interval = i.getIntExtra(AppConstants.REST_INTERVAL, 1)
        val exerciseList = i.getParcelableArrayListExtra<ExerciseDbModel>(AppConstants.EXERCISE_LIST);
        val singleExerciseTime = exerciseList!![0].time
        val noOfExerciseSlots = i.getIntExtra(AppConstants.TOTAL_EXERCISE_SLOTS, 30)
        isSuggestedPlan = i.getBooleanExtra(AppConstants.IS_SUGGESTED_PLAN, true)
        if (!isSuggestedPlan) {
            isShuffleEnabled = i.getBooleanExtra(AppConstants.IS_SHUFFLE_ENABLED, false)
        }



        if (exerciseList.size < noOfExerciseSlots) {
            val remainingExer = (noOfExerciseSlots - exerciseList.size) + 1
            for (i in 0 until remainingExer) {
                exerciseList.add(exerciseList[i])
            }
        }
        //if shuffle is enabled then shuffle exercises
        if (isShuffleEnabled) exerciseList.shuffle()

        //calculate required values
        totalWorkoutTime = (time * 60 * 1000).toLong()
        timerInitialTime = (singleExerciseTime * 1000).toLong()
        timeRemaining = timerInitialTime
        restTime = (rest * 1000).toLong()
        restInterval = interval
//        restInterval++
        workoutList = exerciseList
        this.noOfExerciseSlots = noOfExerciseSlots

        videoUri = applicationContext.filesDir.toString() + "/" + workoutList[currentExercise].name + ".mp4"



        //setup Indicator
        seekbar_exercises_completed_indicator.max = noOfExerciseSlots
        seekbar_exercises_completed_indicator.progress = 0
        seekbar_exercises_completed_indicator.isEnabled = false


        //set current exercise title
        setExerciseTitle()
        //set main workout clock timer
        setTotalWorkoutTimeText()
        startInitialTimer()
        changeInitialTimerColorScheme()
        setCircularTimerMaxProgress(initialTime)
        setInitialTimerTimeTextAndCircularProgressText(initialTime)
        initialTimer.resume()



        dlgSetRestTime = DlgSetRestTime(this@ActivityStartExercise, 10)//temp
        dlgSetRestTime.create()

        dlgSetRestTime.setOnShowListener {
            dlgSetRestTime.isSaved = false
        }

        dlgSetRestTime.setOnDismissListener {
            if(dlgSetRestTime.isSaved){
                val changedRestTime = dlgSetRestTime.restTime
                viewModel.updateRest(db, RestTimeModel(restTimeModel.id, restTimeModel.type, restTimeModel.intensity, restTimeModel.numberOfExerciseAfter,  changedRestTime))
//                restTime_textView.text = "$changedRestTime Sec"
            }

        }

        viewModel.getRest(db, AppConstants.BODY_WEIGHT, session.intensity!!.toLowerCase())

        viewModel.restTime.observe(this, androidx.lifecycle.Observer { it ->
            restTimeModel = it

//            restTime_Seekbar.setProgress(it.restTime.toFloat())

            dlgSetRestTime.restTime = it.restTime

//            restTime_textView.text = "${restTimeModel.restTime} Sec"
        })

        settings_button.setOnClickListener {
            dlgSetRestTime.show()
        }

        val volumeOnDrawable = ContextCompat.getDrawable(this, R.drawable.ic_volume)
        val volumeOffDrawable = ContextCompat.getDrawable(this, R.drawable.ic_volume_off)

        //this is for first time set image manually
        volume_button.setImageDrawable(if(session.isVolumeOn!!){
            volumeOnDrawable
        }else{
            volumeOffDrawable
        })



        volume_button.setOnClickListener {
            val newDrawable = if((it as ImageView).drawable == volumeOnDrawable){
                session.isVolumeOn = false
                volumeOffDrawable
            }else{
                session.isVolumeOn = true
                volumeOnDrawable
            }
            it.setImageDrawable(newDrawable)

        }
    }

    /**
     * instantiates the db variable
     */
    private fun instantiateDb() {
        db = Room.databaseBuilder(this, AppDatabase::class.java, "HasterDb.db")
                .build()
    }

    private fun incrementSeekbarExercisesCompletedIndicator() {
        seekbar_exercises_completed_indicator.progress = currentExercise + 1
    }

    private fun setSeekbarExercisesCompletedIndicator() {
        seekbar_exercises_completed_indicator.progress = currentExercise
    }



    private fun addWorkoutTime(time: Long) {
        totalWorkoutTime += (time + timerInitialTime)
    }

    private fun addRestTime(time: Long) {
        totalWorkoutTime += (time + restTime)
    }


    fun reduceTotalWorkoutTime(time: Long) {
        totalWorkoutTime -= time
    }

    /**
     * play/pause button click function
     *  - starts/stops gif
     *  - toggles play/pause icon
     */
    private fun playPauseClick() {

        //toggle button and gif
        togglePlayPauseDrawableAndGif()

        //if not rest time set gif
        if (!isRestTime) setGif()

        //check for pause state
        if (isActivityInPauseState) {
            isActivityInPauseState = false
            when {
                isRestTime -> {
                    changeRestColorScheme()
                    //if rest time then resume rest timer
                    restTimer.resume()
                }
                isFinalTimer -> {
                    changeNormalColorScheme()
                    //if final timer then resume final timer
                    finalTimer.resume()
                }
                isInitialTimer -> {
                    //if initial timer then resume initial timer
                    initialTimer.resume()
                }
                //else resume normal timer
                else -> {
                    changeNormalColorScheme()
                    //at last else resume normal timer
                    timer.resume()
                }
            }
        } else {//for not pause state
            isActivityInPauseState = true
            when {
                isRestTime -> {
                    //if rest time then pause rest timer
                    restTimer.pause()
                }
                isInitialTimer -> {
                    //if initial timer then pause initial timer
                    initialTimer.pause()
                }
                else -> {
                    //else pause normal timer
                    timer.pause()
                }
            }
        }
    }

    fun startFinalTimer(time: Long) {
        finalTimer = object : CountDownTimerWithPause(time + 200, 1000, false) {
            override fun onFinish() {

                //pause Workout
                if (!isActivityInPauseState) {
                    playPauseClick()
                }

                Toast.makeText(this@ActivityStartExercise, "Finished", Toast.LENGTH_SHORT).show()

                //loop and calculate
                loopThroughTimePerExerciseHashMapAndCalculateCalories()


                //show dialogue message having current workout stats info
                showFinishDialogue()

            }

            override fun onTick(millisUntilFinished: Long) {
                //reduce total workout time on tick
                reduceTotalWorkoutTime(1000)
                //update all texts on tick
                setTimeTextAndCircularProgressText(((millisUntilFinished / 1000) - 1) * 1000)

                setPerExerciseTime()

            }
        }.create()

    }

    var isRestTimer = false
    private var intervalCurrent = 0
    var counter = 0.toLong()
    var lastTime = 0.toLong()
    var timeToViewOnProgress = 0.toLong()
    var counterTime = 0
    private fun newInit() {
        isRestTimer = false
        intervalCurrent = restInterval
        counter = (timerInitialTime / 1000) * intervalCurrent.toLong()
        timeToViewOnProgress = timerInitialTime
        setCircularTimerMaxProgress(timerInitialTime)
        setGif()
        updateExerciseTitle()


        startMainTimer(totalWorkoutTime)
//        mainTimer!!.resume()


    }

    fun updateExerciseVideo() {
        exercise_video_bitmap.visibility = View.VISIBLE
        videoUri = applicationContext.filesDir.toString() + "/" + workoutList[currentExercise].name + ".mp4"
        loadBitMapAndGif(videoUri)
    }

    var isUpdated = false
    private fun startMainTimer(time: Long) {

        mainTimer = object : CountDownTimer(time, 1000) {
            override fun onFinish() {

                finishWorkout()

            }

            override fun onTick(millisUntilFinished: Long) {
                lastTime = millisUntilFinished

                if (timeToViewOnProgress == 0.toLong() && !isRestTimer && !isUpdated) {
                    currentExercise++
                    updateExerciseVideo()
                    updateExerciseTitle()
                    setGif()
                    v("exercise", currentExercise.toString())
                    setSeekbarExercisesCompletedIndicator()
                }

                isUpdated = false

                if (totalWorkoutTime != 0.toLong()) {
                    val initialCounterSize = (timerInitialTime / 1000) * restInterval.toLong()

                    if (counter == 0.toLong()) {

                        isRestTimer = !isRestTimer
                        if (totalWorkoutTime < timerInitialTime) {
                            counter = totalWorkoutTime / 1000
                            timeToViewOnProgress = totalWorkoutTime
                            updateExerciseTitle()
                            setCircularTimerMaxProgress(totalWorkoutTime)
                            changeNormalColorScheme()
                        } else {
                            counter = if (isRestTimer) {
                                resetGif()
                                setExerciseTitle()
//                                exercise_video_bitmap.visibility = View.VISIBLE
//                                videoUri = if(videoUri == "android.resource://" + packageName + "/" + R.raw.pushups){
//                                    "android.resource://" + packageName + "/" + R.raw.leg
//                                }else{
//                                    "android.resource://" + packageName + "/" + R.raw.pushups
//                                }
//                                loadBitMapAndGif(videoUri)
                                updateExerciseVideo()
                                setCircularTimerMaxProgress(restTime)
                                changeRestColorScheme()
                                restTime / 1000 //rest time in seconds
                            } else {
                                setGif()
                                updateExerciseTitle()
                                setCircularTimerMaxProgress(timerInitialTime)
                                changeNormalColorScheme()
                                (timerInitialTime / 1000) * restInterval.toLong() //one set time
                            }

                            if (isRestTimer && currentExercise - 1 == noOfExerciseSlots - 1) {
                                cancel()
                                finishWorkout()
                            }
                        }

                    }




                    if (timeToViewOnProgress == 0.toLong()) {

                        timeToViewOnProgress = if (isRestTimer) {
                            startAudio(AppConstants.WHISTLE_WAV)
                            setSeekbarExercisesCompletedIndicator()
                            restTime
                        } else {
                            startAudio(AppConstants.LETS_GO_AAC)
                            timerInitialTime
                        }
                    }
                    if (isRestTimer) {
                        when (timeToViewOnProgress) {

                            1000.toLong() -> {
                                remaining_clock_time.text = "Go!"
                                circular_timer.progress = (timeToViewOnProgress / 1000).toInt()
                                //set main workout clock timer
                                setTotalWorkoutTimeText()
                                startAudio(AppConstants.TICK_AAC)
                            }
                            2000.toLong() -> {
                                remaining_clock_time.text = "Ready"
                                circular_timer.progress = (timeToViewOnProgress / 1000).toInt()
                                //set main workout clock timer
                                setTotalWorkoutTimeText()
                                startAudio(AppConstants.TICK_AAC)
                            }
                            3000.toLong() -> {
                                //start the gif
                                setGif()
                                setTimeTextAndCircularProgressText(timeToViewOnProgress)
                                startAudio(AppConstants.TICK_AAC)
                            }
                            else -> {
                                setTimeTextAndCircularProgressText(timeToViewOnProgress)
                            }
                        }
                    } else {
                        setTimeTextAndCircularProgressText(timeToViewOnProgress)
                        setPerExerciseTime()
                        when (timeToViewOnProgress) {

                            1000.toLong() -> {
                                startAudio(AppConstants.TIME1_AAC)
                            }
                            2000.toLong() -> {
                                startAudio(AppConstants.TIME2_AAC)
                            }
                            3000.toLong() -> {
                                startAudio(AppConstants.TIME3_AAC)
                            }
                            10000.toLong() -> {
                                startAudio(AppConstants.TIME10_AAC)
                            }

                        }

                    }
                    counter--
                    timeToViewOnProgress -= 1000
                    //reduce total workout time on tick
                    reduceTotalWorkoutTime(1000)
                } else if (totalWorkoutTime == 0.toLong()) {
                    cancel()
                    finishWorkout()
                }


            }
        }.start()


    }

    fun startAudio(audioName: String) {
        if (session.isVolumeOn!!){
            val mediaPlayer = MediaPlayer();
            val afd: AssetFileDescriptor
            try {
                afd = assets.openFd(audioName);
                mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (e: IOException) {
                e.printStackTrace();
            }

            mediaPlayer.setOnCompletionListener {
                it.release()
            }
        }
    }


    private fun finishWorkout() {
        //set flag that user exited activity
        isExitedByUser = true
        resetGif()
        setTimeTextAndCircularProgressText(0)
        Toast.makeText(this@ActivityStartExercise, "finished", Toast.LENGTH_LONG).show()
        //loop and calculate
        loopThroughTimePerExerciseHashMapAndCalculateCalories()
        //show stats dialogue
        showFinishDialogue()
    }

    private fun updatedNextClick() {

        if (isInitialTimer) {

            isInitialTimer = false
            initialTimer.cancel()

            changeNormalColorScheme()
            newInit()

        } else {
            if (currentExercise < noOfExerciseSlots - 1) {
//        mainTimer!!.pause()
                try {
                    mainTimer.cancel()
                } catch (e: Exception) {
                    e.printStackTrace()
                }


                val timeToRemove = timeToViewOnProgress
//        val timeToRemove = timerInitialTime / 1000 - ((timerInitialTime / 1000 * intervalCurrent) - counter)
                counter -= timeToRemove / 1000
                timeToViewOnProgress = 0
                lastTime -= timeToRemove
                if (!isActivityInPauseState) {
                    startMainTimer(lastTime)
                }
//        mainTimer!!.resume()
                reduceTotalWorkoutTime(timeToRemove)


                intervalCurrent--
                if (intervalCurrent == 0) {
                    intervalCurrent = restInterval
                }



                if (timeToViewOnProgress == 0.toLong()) {
                    if (!isRestTimer) {
                        currentExercise++
                        updateExerciseVideo()
                        updateExerciseTitle()
                        if (!isActivityInPauseState) setGif()
                        v("exercise", currentExercise.toString())
                        setSeekbarExercisesCompletedIndicator()
                        isUpdated = true
                    }
                }

                if (isRestTimer) {
                    setExerciseTitle()
                    counterTime += 10
                } else {
                    updateExerciseTitle()
                    counterTime += 30
                    v("countertime", counterTime.toString())
                }
//                setTimeTextAndCircularProgressText(time)

                if (isActivityInPauseState) {
                    timeToViewOnProgress = if (isRestTimer) {
                        restTime
                    } else {
                        timerInitialTime
                    }

                    setTimeTextAndCircularProgressText(timeToViewOnProgress)
                }


            } else {
                Toast.makeText(this, "last", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun updatedPlayPauseClick() {
        //toggle button and gif
        togglePlayPauseDrawableAndGif()

        //if not rest time set gif
//        if (!isRestTime) setGif()
//        else resetGif()

        //check for pause state
        if (isActivityInPauseState) {
            isActivityInPauseState = false
            if (isInitialTimer) {
                initialTimer.resume()
            } else startMainTimer(lastTime)
        } else {
            isActivityInPauseState = true
            if (isInitialTimer) {
                initialTimer.pause()
            } else mainTimer.cancel()
        }
    }

    fun changeRestColorScheme() {
        val color = ContextCompat.getColor(this, R.color.yellow)
        circular_timer.progressColor = color
        remaining_clock_time.setTextColor(color)
        circular_timer_indicator_text.setTextColor(color)
        circular_timer_indicator_text.visibility = View.VISIBLE
        circular_timer_indicator_text.text = "Rest"

    }

    private fun changeInitialTimerColorScheme() {
        val color = ContextCompat.getColor(this, R.color.yellow)
        circular_timer.progressColor = color
        remaining_clock_time.setTextColor(color)
        circular_timer_indicator_text.visibility = View.GONE

    }

    fun changeNormalColorScheme() {
        val color = ContextCompat.getColor(this, R.color.babyBlue)
        circular_timer.progressColor = color
        remaining_clock_time.setTextColor(color)
        circular_timer_indicator_text.setTextColor(color)
        circular_timer_indicator_text.visibility = View.VISIBLE
        circular_timer_indicator_text.text = "Remaining"
    }

    var caloriesBurned = 0.0
    private lateinit var roundingFormat: DecimalFormat

    fun showFinishDialogue() {

        val dialogBuilder = AlertDialog.Builder(this@ActivityStartExercise, R.style.AlertDialogStyle)
        val dialog = dialogBuilder
                .setPositiveButton("ok", DialogInterface.OnClickListener() { a, b ->

                    //update Db
                    saveCalculatedValuesToDb()

                    //update streak if at least one exercise is performed
                    if (timePerExerciseHashMap.size >= 1) updateStreak()

                    //exit activity
                    exitActivity()
                })
                .create()

        dialog.setMessage("Total time of exercise : $timeOfActivityPerformedSec Sec\n" +
                "Total no of exercises  : ${timePerExerciseHashMap.size}\n" +
                "Total calories burned  : ${roundingFormat.format(caloriesBurned)} Kcal")

        //change buttons color in on show listener
        dialog.setOnShowListener {
            val colorPositiveBtn = ContextCompat.getColor(this, R.color.yellow)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorPositiveBtn);
        }

        //show dialogue
        dialog.show()
    }

    private fun exitActivity() {
        //exit activity

        val intent = if (isSuggestedPlan) {
            Intent(applicationContext, SelectPlanForDailyWorkoutActivity::class.java)
        } else {
            Intent(applicationContext, ActivityDashboard::class.java)
        }

        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun updateStreak() {
        val calInstance = Calendar.getInstance()
        calInstance.set(Calendar.MINUTE, 0)
        calInstance.set(Calendar.SECOND, 0)
        calInstance.set(Calendar.HOUR, 0)
        calInstance.set(Calendar.MILLISECOND, 0)
        val todayDate = calInstance.time
        val lastStreakDate = if (session.streakDate == null) {
            session.streakNo = 1
            session.streakDate = todayDate
            todayDate
        } else session.streakDate!!


        calInstance.time = lastStreakDate
        val lastStreakDateInMillis = calInstance.timeInMillis
        calInstance.time = todayDate
        val todaysDateInMillis = calInstance.timeInMillis

        val timeBetweenLastStreakDateAndToday = todaysDateInMillis - lastStreakDateInMillis
        val seconds = timeBetweenLastStreakDateAndToday / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        if (lastStreakDate != todayDate) {
            if (hours <= 24) {
                session.streakNo += 1
                session.streakDate = todayDate
            } else {
                session.streakNo = 1
                session.streakDate = todayDate
            }
        }

    }

    private fun saveCalculatedValuesToDb() {

        viewModel.getTodaysUserData(db)
        viewModel.todayData.observe(this, androidx.lifecycle.Observer {
            val today = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)
            val noOfWorkouts = timePerExerciseHashMap.size
            val todayDataDbModel = it
            if (todayDataDbModel == null) {
                //if todays row is not created create new row for today
                val todayData = UserDailyDataDbModel(today.time, caloriesBurned, noOfWorkouts, timeOfActivityPerformedSec / 60.0, 1)
                viewModel.insertUserTodayData(db, todayData)
            } else {
                //else update the existing todays row
                val totalWorkoutNo = noOfWorkouts + todayDataDbModel.noOfWorkout
                val totalCaloriesBurned = caloriesBurned + todayDataDbModel.calories
                val date = todayDataDbModel.date
                val totalTime = timeOfActivityPerformedSec / 60.0 + todayDataDbModel.timeWorkoutPerformed
                val todayData = UserDailyDataDbModel(date, totalCaloriesBurned, totalWorkoutNo, totalTime, 1)
                viewModel.updateUserTodayData(db, todayData)
            }
        })

        viewModel.insertedRowLong.observe(this, androidx.lifecycle.Observer {
            it
        })

        viewModel.insertedRowInt.observe(this, androidx.lifecycle.Observer {
            it
        })

    }

    private fun calculateCalories(met: Double, timeInSec: Int): Double {
        val timeInMinute = timeInSec / 60.0
        val weightInKg = session.weightInKg!!
        val calorieBurnedPerMin = met * 3.5 * weightInKg / 200
        return (calorieBurnedPerMin * timeInMinute)
    }

    private fun loopThroughTimePerExerciseHashMapAndCalculateCalories() {
        for (entry in timePerExerciseHashMap) {
            val exerciseId = entry.key
            val exerciseTime = entry.value
            val exerciseModel = workoutList.filter { it.id == exerciseId }
            val gender = session.gender
            val met = if (gender == AppConstants.MALE) {
                exerciseModel[0].mmet
            } else {
                exerciseModel[0].fmet
            }

            caloriesBurned += calculateCalories(met, exerciseTime)

        }
    }


    /**
     * sets how much time is taken for particular exercise
     *  - may used to calculate calories burn
     *  - timePerExerciseHashMap contains exercise_id to time_exercise_performed_in_sec combination
     */
    fun setPerExerciseTime() {
        timeOfActivityPerformedSec++
        val curExerciseId = workoutList[currentExercise].id
        if (timePerExerciseHashMap.containsKey(curExerciseId)) {

            var time: Int = timePerExerciseHashMap[curExerciseId]!!
            time++
            timePerExerciseHashMap[curExerciseId] = time

        } else {
            timePerExerciseHashMap[curExerciseId] = 1
        }
    }



    //start rest timer
    private fun startInitialTimer() {

        initialTimer = object : CountDownTimerWithPause(initialTime, 1000, false) {
            override fun onFinish() {
                isInitialTimer = false

                changeNormalColorScheme()
                newInit()
            }

            override fun onTick(millisUntilFinished: Long) {

                val actualMillisUntilFinish = (((millisUntilFinished / 1000) - 1) * 1000)
                v("millis", actualMillisUntilFinish.toString())
                //update all texts on tick
                when (actualMillisUntilFinish) {
                    1000.toLong() -> {
                        remaining_clock_time.text = "Ready"
                        circular_timer.progress = (actualMillisUntilFinish / 1000).toInt()
                        startAudio(AppConstants.TICK_AAC)
                    }
                    0.toLong() -> {
                        remaining_clock_time.text = "Go!"
                        startAudio(AppConstants.LETS_GO_AAC)
                        circular_timer.progress = (actualMillisUntilFinish / 1000).toInt()
                    }
                    5000.toLong() -> {
                        setInitialTimerTimeTextAndCircularProgressText(actualMillisUntilFinish)
                        startAudio(AppConstants.TICK_AAC)
                    }
                    4000.toLong() -> {
                        startAudio(AppConstants.TICK_AAC)
                        setInitialTimerTimeTextAndCircularProgressText(actualMillisUntilFinish)
                    }
                    3000.toLong() -> {
                        startAudio(AppConstants.TICK_AAC)
                        setInitialTimerTimeTextAndCircularProgressText(actualMillisUntilFinish)
                    }
                    2000.toLong() -> {
                        startAudio(AppConstants.TICK_AAC)
                        setInitialTimerTimeTextAndCircularProgressText(actualMillisUntilFinish)
                    }
                    else -> {
                        setInitialTimerTimeTextAndCircularProgressText(actualMillisUntilFinish)
                    }
                }
            }
        }.create()
    }


    private fun setGif() {
        //make bitmap invisible and gif visible
        exercise_video.visibility = View.VISIBLE
        exercise_video.start()
    }

    private fun resetGif() {
        //make gif invisible and bitmap visible
        exercise_video.pause()
    }


    //SetGif Image and gif both to achieve smoothness
    private fun loadBitMapAndGif(path: String) {
        val interval: Long = 1000
        val options: RequestOptions = RequestOptions().frame(interval)
        Glide.with(applicationContext).asDrawable()
                .load(videoUri)
                .apply(options)
                .placeholder(ContextCompat.getDrawable(this, R.color.white))
                .into(exercise_video_bitmap)


        playVideo("pushups")

    }

    var videoUri = ""
    private fun playVideo(fileName: String) {

        val uri: Uri = Uri.parse(videoUri)

        exercise_video.setVideoURI(uri)


        exercise_video.setOnPreparedListener { mp ->
            mp.isLooping = true

            mp.setOnInfoListener { mp, what, extra ->

                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    // video started; hide the placeholder.
                    exercise_video_bitmap.visibility = View.GONE;
                    return@setOnInfoListener true
                }
                return@setOnInfoListener false
            }

        }


    }


    fun setCircularTimerMaxProgress(time: Long) {
        val circularTimerRange = TimeUnit.MILLISECONDS.toSeconds(time)
        circular_timer.max = circularTimerRange.toInt()
    }


    private fun setExerciseTitle() {
        title_of_exercise.text = workoutList[currentExercise].name
        next_up_textView.visibility = View.VISIBLE
        title_of_exercise.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
        title_of_exercise.textSize = 18f
    }

    private fun updateExerciseTitle() {
        next_up_textView.visibility = View.GONE
        title_of_exercise.text = workoutList[currentExercise].name
        title_of_exercise.gravity = Gravity.CENTER
        title_of_exercise.textSize = 20f
    }


    fun setInitialTimerTimeTextAndCircularProgressText(time: Long) {
        //calculate int progress from remaining time
        circular_timer.progress = (((time) / 1000)).toInt()
        val seconds = time / 1000 % 60
        remaining_clock_time.text = "$seconds"
    }

    fun setTimeTextAndCircularProgressText(remainingTime: Long) {

        //calculate int progress from remaining time
        circular_timer.progress = (((remainingTime) / 1000)).toInt()
        val minutes = remainingTime / 1000 / 60
        val seconds = remainingTime / 1000 % 60

        remaining_clock_time.text = "${checkIfSingleDigit(minutes)}:${checkIfSingleDigit(seconds)}"

        //set main workout clock timer
        setTotalWorkoutTimeText()
    }

    //function to convert m:s to mm:ss
    private fun checkIfSingleDigit(digit: Long): String {
        return if (digit / 10 == 0.toLong()) {
            "0$digit"
        } else digit.toString()
    }

    private fun setTotalWorkoutTimeText() {

        val totalWorkoutRemainingMinutes = totalWorkoutTime / 1000 / 60//to minutes
        val totalWorkoutRemainingSeconds = totalWorkoutTime / 1000 % 60//to seconds
        remaining_time.text = "${checkIfSingleDigit(totalWorkoutRemainingMinutes)}:${checkIfSingleDigit(totalWorkoutRemainingSeconds)}"
    }

    //toggle play pause drawable
    private fun togglePlayPauseDrawableAndGif() {

        val playDrawable = R.drawable.ic_play_round
        val pauseDrawable = R.drawable.ic_pause_round
        val drawable = exercise_gif.drawable

        if (isActivityInPauseState) {
            play_pause_button.setImageResource(pauseDrawable)
            //if activity is  in pause state start gif
//            if (drawable is Animatable && drawable != null) (drawable as Animatable).start()
            setGif()
        } else {
            //if activity is not in pause state stop gif
            play_pause_button.setImageResource(playDrawable)
//            if (drawable is Animatable && drawable != null) (drawable as Animatable).stop()
            resetGif()
        }
    }


    override fun onBackPressed() {

        calculateValuesAndShowExitAlert()
    }

    private var isExitedByUser = false
    private fun calculateValuesAndShowExitAlert() {

        //set flag that user exited activity
        isExitedByUser = true

        //toggle play pause state
        if (!isActivityInPauseState) {
//            playPauseClick()
            updatedPlayPauseClick()
        }

        //loop and calculate
        loopThroughTimePerExerciseHashMapAndCalculateCalories()

        val dlg = AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle("Quit Workout")
                .setMessage("Are you sure you want to quit current workout session?")
                .setPositiveButton("QUIT", DialogInterface.OnClickListener { dialog, which ->
                    //hide close dialogue
                    dialog.dismiss()

                    //show stats dialogue
                    showFinishDialogue()

                })
                .setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, which ->
                    if (!isActivityInPauseState) {
                        updatedPlayPauseClick()
                    }
                }).create()
        //change buttons color in on show listener
        dlg.setOnShowListener {
            val colorNegativeBtn = ContextCompat.getColor(this, R.color.yellow)
            val colorPositiveBtn = ContextCompat.getColor(this, R.color.color_gray_66)
            dlg.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(colorNegativeBtn);
            dlg.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorPositiveBtn);
        }

        //show dialogue
        dlg.show()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.play_pause_button -> {
                //Disable click
                play_pause_button.isEnabled = false
                //if user clicked the button half seconds ago then do nothing
                if (SystemClock.elapsedRealtime() - mLastClickTime >= 1000) {
                    mLastClickTime = SystemClock.elapsedRealtime()
//                    playPauseClick()
                    updatedPlayPauseClick()
                }
                //enable click
                play_pause_button.isEnabled = true
            }
            R.id.next_button -> {
                //Disable click
                next_button.isEnabled = false
                //if user clicked the button half seconds ago then do nothing
                if (SystemClock.elapsedRealtime() - mLastClickTime >= 0) {
                    mLastClickTime = SystemClock.elapsedRealtime()

//                    nextClick()
                    updatedNextClick()
                }
                //enable click
                next_button.isEnabled = true
            }
            R.id.back_button -> {
//                v.isEnabled = false
//                if (!(SystemClock.elapsedRealtime() - mLastClickTime < 500)) {
//                    mLastClickTime = SystemClock.elapsedRealtime()
//                    backClick()
//                }
//                v.isEnabled = true
            }
            R.id.end_exercise -> {
                v.isEnabled = false
                if (SystemClock.elapsedRealtime() - mLastClickTime >= 500) {
                    calculateValuesAndShowExitAlert()
                }
                v.isEnabled = true
            }

        }

    }


    override fun onPause() {

        if (!isExitedByUser) {
            if (isInitialTimer) {
                initialTimer.pause()
            } else {
                mainTimer!!.cancel()
            }


            //Creates a new launcher intent, equivalent to the intent generated by
            //clicking the icon on the home screen.
            val intent = Intent(this, ActivityStartExercise::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.action = Intent.ACTION_MAIN
            intent.addCategory(Intent.CATEGORY_LAUNCHER)

            val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

            val builder = NotificationCompat.Builder(this, AppConstants.CHANNEL_ID)
                    .setSmallIcon(R.drawable.dot_one)
                    .setContentTitle("Hastar Fitness")
                    .setContentText("go to your ongoing workout")
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOngoing(true)//to make notification un-swipable


            //this method is for oreo and above
            // - doesn't affect lower apis
            createNotificationChannel()

            //show notification by this code
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(AppConstants.APP_WORKOUT_RESTART_NOTIFICATION, builder.build())
            }

        }
        super.onPause()
    }


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.next_up)
            val descriptionText = getString(R.string.next)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel1 = NotificationChannel(AppConstants.CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setSound(null, null)
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
        }
    }

    
    override fun onRestart() {

        //disable notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(AppConstants.APP_WORKOUT_RESTART_NOTIFICATION); // Notification ID to cancel

        //pause timer depending on which timer is running
        //and if activity was in pause state

        updatedPlayPauseClick()
        updateExerciseVideo()
        resetGif()
        when {
            isInitialTimer -> {
                initialTimer.pause()
            }
            else -> {
                mainTimer!!.cancel()
            }
        }

        super.onRestart()
    }
}
