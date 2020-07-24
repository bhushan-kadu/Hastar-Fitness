package com.hastarfitness.hastarfitnessapp.exerciseListForCardioAndBodyWeight

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.TimerActivity
import com.hastarfitness.hastarfitnessapp.ViewModel
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.customDialogueToDownloadVideos.DlgDownloadVideos
import com.hastarfitness.hastarfitnessapp.models.Exercise
import com.hastarfitness.hastarfitnessapp.customDialogueToShowExerciseInfo.DlgShowExerciseInfo
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.database.ExerciseDbModel
import kotlinx.android.synthetic.main.activity_excercise_list.*
import kotlinx.android.synthetic.main.fragment_viewpager_exercise_list.view.*

/**
 * Activity to show list of exercises
 *  - this activity can be used to show cardio as well as body weight exercises
 *  - this activity has the support to slide between ( view pager ) different exercise categories
 */
class ActivityExerciseList : AppCompatActivity(), View.OnClickListener {

    lateinit var viewModel: ViewModel
    private lateinit var demoCollectionAdapter: DemoCollectionAdapter
    private var typeOfWorkout: String = ""
    private var typeOfWorkoutSubtype: String? = ""
    private var dotsCount = 0
    lateinit var dots: Array<ImageView?>
    private val TAB_TITLES_CARDIO = intArrayOf(R.string.title_activity_get_gender,
            R.string.enter_age_text,
            R.string.title_activity_get_height,
            R.string.title_activity_get_weight,
            R.string.title_activity_get_goal_weight,
            R.string.title_activity_get_activity_level)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_excercise_list)
//        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //init class
        init()

        //create and set view pager adpater
        demoCollectionAdapter = DemoCollectionAdapter(this@ActivityExerciseList, typeOfWorkout, typeOfWorkoutSubtype)
        view_pager.adapter = demoCollectionAdapter


        //attach clicklistener to button
        start.setOnClickListener(this)

        //view pager scroll handler
        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                setActionbarTitle(position)

                for (i in 0 until dotsCount) {
                    dots[i]!!.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.non_active_dot))
                }

                dots[position]!!.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.active_dot))

            }
        })

        //Set the dots inside scoller
        setDots();

    }

    fun init() {
        instantialteDb()
        //get the required values from intent
        typeOfWorkout = intent.getStringExtra(AppConstants.WORKOUT_TYPE)
        typeOfWorkoutSubtype = intent.getStringExtra(AppConstants.WORKOUT_SUB_TYPE)

    }

    //this code is needed
    private fun instantialteDb() {
//         Room.databaseBuilder(applicationContext, AppDatabase::class.java, "HasterDb.db")
//                .createFromAsset("databases/HasterDb.db")
//                .build()

    }


    /**
     * This function sets the dots of view pager scroll
     *
     */
    fun setDots() {
        //dots
        dotsCount = demoCollectionAdapter.itemCount
        dots = arrayOfNulls<ImageView>(dotsCount)

        for (i in 0 until dotsCount) {
            dots[i] = ImageView(this)
            dots[i]!!.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.non_active_dot))
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(8, 0, 8, 0)
            SliderDots.addView(dots.get(i), params)
        }
        dots[0]!!.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));


    }

    /**
     * set title of activity based on position of view pager fragment
     *
     * @param position position of view pager fragment
     */
    fun setActionbarTitle(position: Int) {

        val name = getName(typeOfWorkout, position)
        supportActionBar?.title = name.capitalize()
        start.text = "Start $name"
    }

    /**
     * Get title or subtype of workout to be set using type of
     * workout and position of view pager
     *
     * @param type type of workout
     * @param pos position of fragment in view pager
     */
    private fun getName(type: String, pos: Int): String {
        when (type.toLowerCase()) {
            "cardio" -> {
                return getString(cardioIntensityListName[pos])
            }

            "body weight" -> {
                return getString(bodyWeightIntensityListName[pos])
            }
        }
        return ""
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun onClick(v: View?) {
        //go to how many minutes activty
        val i = Intent(this@ActivityExerciseList, TimerActivity::class.java)
        val curItem = view_pager.currentItem
        val curExerciseTypeName = getName(typeOfWorkout, curItem)//here we get id of name
        i.putExtra(AppConstants.INTENSITY, curExerciseTypeName)
        i.putExtra(AppConstants.WORKOUT_SUB_TYPE, typeOfWorkoutSubtype)
        i.putExtra(AppConstants.WORKOUT_TYPE, typeOfWorkout)
        startActivity(i)
    }

}

val cardioIntensityListName = arrayOf(R.string.hiit, R.string.light_cardio, R.string.plyometrics, R.string.joint_friendly)
val bodyWeightIntensityListName = arrayOf(R.string.beginner, R.string.intermediate, R.string.advanced)

/**
 * Adapter for view pager2 in dashboard
 * @param fa activity where fragment is being hosted
 * @param workoutType type of workout
 * @param subType subtype of workout or intensity whichever is applicable
 */
class DemoCollectionAdapter(private val fa: FragmentActivity, val workoutType: String, private val subType: String?) : FragmentStateAdapter(fa) {
    private var dialogExerciseInfo: DlgShowExerciseInfo = DlgShowExerciseInfo(fa, listOf<ExerciseDbModel>())

    init {
        dialogExerciseInfo.create()
    }

    override fun getItemCount(): Int {
        when (workoutType.toLowerCase()) {
            AppConstants.CARDIO -> {
                return cardioIntensityListName.size
            }
            AppConstants.BODY_WEIGHT -> {
                bodyWeightIntensityListName.size
            }
        }
        return 3
    }

    override fun createFragment(position: Int): Fragment {

        // Return a NEW fragment instance in createFragment(int)
        var fragment: Fragment? = null
        when (workoutType.toLowerCase()) {
            AppConstants.CARDIO -> {
                fragment = ListFragment(fa.getString(cardioIntensityListName[position]), "cardio", dialogExerciseInfo, null)
            }
            AppConstants.BODY_WEIGHT -> {
                fragment = ListFragment(fa.getString(bodyWeightIntensityListName[position]), "body weight", dialogExerciseInfo, subType)
            }
        }
        return fragment!!
    }
}

/**
 * Fragment class which will be shown for each slide of view pager
 *
 * @param intensity intensity of workout (if applicable)
 * @param workoutType type of workout
 * @param dlg dialogue to be shown when user clicks on any exercise
 * @param subType subtype of workout (if applicable)
 */
class ListFragment(val intensity: String, val workoutType: String, var dlg: DlgShowExerciseInfo, private val subType: String?) : Fragment() {

    private lateinit var viewModel: ViewModel


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_viewpager_exercise_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //set banner
        if (workoutType == AppConstants.BODY_WEIGHT) {

            val resourceName = subType?.split(" ")?.joinToString("_")
            val packageName = this.requireContext().packageName
            val resource = this.requireContext().resources.getIdentifier(resourceName, "drawable", packageName)
            val drawable = ContextCompat.getDrawable(this.requireContext(), resource)
            view.exercise_type_banner.setImageDrawable(drawable)
        }else if(workoutType == AppConstants.CARDIO){
            val resourceName = "banner_" + intensity.toLowerCase().split(" ").joinToString("_")
            val packageName = this.requireContext().packageName
            val resource = this.requireContext().resources.getIdentifier(resourceName, "drawable", packageName)
            val drawable = ContextCompat.getDrawable(this.requireContext(), resource)
            view.exercise_type_banner.setImageDrawable(drawable)
        }


        var recyclerView: RecyclerView = view.findViewById(R.id.exercise_recycler_view)

        //Instantiate db
        val db = Room.databaseBuilder(requireActivity().applicationContext,
                AppDatabase::class.java, "HasterDb.db")
                .build()

        var exerciseListFrmDb = listOf<Exercise>()
        var exerciseList = listOf<ExerciseDbModel>()

        //setup ViewModel
        viewModel = ViewModelProviders.of(requireActivity()).get(ViewModel()::class.java)

        //code runs in background thread
        viewModel.setup(db, workoutType, null)

        //invokes when exercises value change happen
        viewModel.exercises.observe(requireActivity(), Observer { it ->
            exerciseList = it

            dlg.exerciseList = exerciseList
            exerciseListFrmDb = exerciseList.map {
                Exercise(
                        it.name,
                        it.type,
                        it.desc,
                        it.img,
                        it.intensity,
                        it.id,
                        it.time,
                        it.mmet,
                        it.fmet,
                        false)
            }



            when (workoutType.toLowerCase()) {
                AppConstants.CARDIO -> {
                    exerciseListFrmDb = exerciseListFrmDb.filter { it.intensity.equals(intensity) }
                }
                AppConstants.BODY_WEIGHT -> {
                    exerciseListFrmDb = exerciseListFrmDb.filter { it.intensity.toLowerCase().contains(subType!!.toLowerCase()) }
                    exerciseListFrmDb = exerciseListFrmDb.filter { it.intensity.toLowerCase().contains(intensity.toLowerCase()) }
                }
            }

            recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = ExerciseListAdapter(exerciseListFrmDb, dlg)
            }
        })

    }


}

