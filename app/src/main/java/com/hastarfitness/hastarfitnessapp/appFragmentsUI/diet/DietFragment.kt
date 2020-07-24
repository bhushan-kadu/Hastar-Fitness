package com.hastarfitness.hastarfitnessapp.appFragmentsUI.diet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.room.Room
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar
import com.hastarfitness.hastarfitnessapp.ActivityDashboard
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.diet.DietViewModel
import com.hastarfitness.hastarfitnessapp.diet.dietStartPages.DietStartPagesActivity
import com.hastarfitness.hastarfitnessapp.diet.searchAndCustomFoodList.SearchedAndCustomFoodListActivity
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import com.triggertrap.seekarc.SeekArc
import kotlinx.android.synthetic.main.fragment_diet.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class DietFragment : Fragment() {
    private lateinit var proteinSeekArc: SeekArc
    private lateinit var carbsSeekArc: SeekArc
    private lateinit var fatSeekArc: SeekArc
    private lateinit var caloriesSeekArc: SeekArc
    private lateinit var carbsConsumedPercentTextView: TextView
    private lateinit var proteinConsumedPercentTextView: TextView
    private lateinit var fatConsumedPercentTextView: TextView
    private lateinit var proteinLeftGmTextView: TextView
    private lateinit var carbsLeftGmTextView: TextView
    private lateinit var fatLeftGmTextView: TextView
    private lateinit var caloriesLeftKcalTextView: TextView
    private lateinit var caloriesConsumedKcalTextView: TextView
    private lateinit var breakfastRecommendedTextView: TextView
    private lateinit var lunchRecommendedTextView: TextView
    private lateinit var snacksRecommendedTextView: TextView
    private lateinit var dinnerRecommendedTextView: TextView

    private lateinit var breakfastConsumedCalTextView: TextView
    private lateinit var lunchConsumedCalTextView: TextView
    private lateinit var snacksConsumedCalTextView: TextView
    private lateinit var dinnerConsumedCalTextView: TextView

    private lateinit var breakfastProgress: RoundCornerProgressBar
    private lateinit var lunchProgress: RoundCornerProgressBar
    private lateinit var snacksProgress: RoundCornerProgressBar
    private lateinit var dinnerProgress: RoundCornerProgressBar

    private lateinit var dailyGoalKcalTextView: TextView
    private lateinit var dietView: ScrollView
    private lateinit var temporaryLinearLayout: LinearLayout
    private lateinit var dietStartPagesBtn: Button

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_diet, container, false)
        val dietPagesBtn = rootView.findViewById<Button>(R.id.diet_start_pages_button)
        val breakfastBtn = rootView.findViewById<Button>(R.id.breakfast_btn)
        val lunchBtn = rootView.findViewById<Button>(R.id.lunch_btn)
        val snacksBtn = rootView.findViewById<Button>(R.id.snacks_btn)
        val dinnerBtn = rootView.findViewById<Button>(R.id.dinner_btn)

        temporaryLinearLayout = rootView.findViewById<LinearLayout>(R.id.temporary_linearLayout)

        proteinSeekArc = rootView.findViewById<SeekArc>(R.id.protein_seekArc)
        caloriesSeekArc = rootView.findViewById<SeekArc>(R.id.calories_seekArc)
        fatSeekArc = rootView.findViewById<SeekArc>(R.id.fat_seekArc)
        carbsSeekArc = rootView.findViewById<SeekArc>(R.id.carbs_seekArc)

        carbsConsumedPercentTextView = rootView.findViewById(R.id.carbsConsumedPercent_TextView)
        proteinConsumedPercentTextView = rootView.findViewById(R.id.proteinConsumedPercent_TextView)
        fatConsumedPercentTextView = rootView.findViewById(R.id.fatConsumedPercent_TextView)

        proteinLeftGmTextView = rootView.findViewById(R.id.proteinLeftGm_textView)
        carbsLeftGmTextView = rootView.findViewById(R.id.carbsLeftGm_textView)
        fatLeftGmTextView = rootView.findViewById(R.id.fatLeftGm_textView)
        caloriesLeftKcalTextView = rootView.findViewById(R.id.caloriesRemainKcal_textView)
        caloriesConsumedKcalTextView = rootView.findViewById(R.id.caloriesConsumedKcal_textView)
        dailyGoalKcalTextView = rootView.findViewById(R.id.dailyGoalKcal_textView)

        breakfastRecommendedTextView = rootView.findViewById(R.id.breakfastRecommended_textView)
        lunchRecommendedTextView = rootView.findViewById(R.id.lunchRecommended_textView)
        snacksRecommendedTextView = rootView.findViewById(R.id.snacksRecommended_textView)
        dinnerRecommendedTextView = rootView.findViewById(R.id.dinnerRecommended_textView)

        breakfastConsumedCalTextView = rootView.findViewById(R.id.breakfastConsumedCal_textView)
        lunchConsumedCalTextView = rootView.findViewById(R.id.lunchConsumedCal_textView)
        snacksConsumedCalTextView = rootView.findViewById(R.id.snacksConsumedCal_textView)
        dinnerConsumedCalTextView = rootView.findViewById(R.id.dinnerConsumedCal_textView)

        breakfastProgress = rootView.findViewById(R.id.breakfast_progress)
        lunchProgress = rootView.findViewById(R.id.lunch_progress)
        snacksProgress = rootView.findViewById(R.id.snacks_progress)
        dinnerProgress = rootView.findViewById(R.id.dinner_progress)

        dietView = rootView.findViewById(R.id.diet_view)
        dietStartPagesBtn = rootView.findViewById(R.id.diet_start_pages_button)

        initialize()

        val breakfastLl = rootView.findViewById<LinearLayout>(R.id.breakfast_linearLayout)
        val dinnerLl = rootView.findViewById<LinearLayout>(R.id.dinner_linearLayout)
        val snacksLl = rootView.findViewById<LinearLayout>(R.id.snacks_linearLayout)
        val lunchLl = rootView.findViewById<LinearLayout>(R.id.lunch_linearLayout)

        val i = Intent(requireActivity(), SearchedAndCustomFoodListActivity::class.java)
        breakfastBtn.setOnClickListener {
            i.putExtra(AppConstants.MEAL_TYPE, AppConstants.BREAKFAST)
            startActivity(i)
        }
        lunchBtn.setOnClickListener {
            i.putExtra(AppConstants.MEAL_TYPE, AppConstants.LUNCH)
            startActivity(i)
        }
        snacksBtn.setOnClickListener {
            i.putExtra(AppConstants.MEAL_TYPE, AppConstants.SNACKS)
            startActivity(i)
        }
        dinnerBtn.setOnClickListener {
            i.putExtra(AppConstants.MEAL_TYPE, AppConstants.DINNER)
            startActivity(i)
        }
        dietPagesBtn.setOnClickListener {
            startActivity(Intent(context, DietStartPagesActivity::class.java))
        }
        val view = inflater.inflate(R.layout.single_food_consumed_view, null)
        breakfastLl.addView(view)
        (view.parent as ViewGroup).removeView(view)
//        lunchLl.addView(view)
//        lunchLl.addView(view)
//        snacksLl.addView(view)
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)
        viewModel.getAllFoodConsumedByDate(db, today.time)

        viewModel.consumedFoods.observe(requireActivity(), androidx.lifecycle.Observer {
            var caloriesConsumed = 0.0
            var carbsConsumed = 0.0
            var fatConsumed = 0.0
            var proteinConsumed = 0.0

            var breakfastCalories = 0.0
            var lunchCalories = 0.0
            var snacksCalories = 0.0
            var dinnerCalories = 0.0

            for (food in it) {
                val singleFoodView = inflater.inflate(R.layout.single_food_consumed_view, null)
                val proteinTv = singleFoodView.findViewById<TextView>(R.id.protein_textView)
                val carbsTv = singleFoodView.findViewById<TextView>(R.id.carbs_textView)
                val fatTv = singleFoodView.findViewById<TextView>(R.id.fat_textView)
                val energyTv = singleFoodView.findViewById<TextView>(R.id.kcal_textView)
                val nameTv = singleFoodView.findViewById<TextView>(R.id.name)

                proteinTv.text = "${food.protein.toString()}g\n(Protein)"
                carbsTv.text = "${food.carbs.toString()}g\n(Carbs)"
                fatTv.text = "${food.fats.toString()}g\n(Fat)"
                energyTv.text = "${food.calories.toString()}\nkcal"
                nameTv.text = food.foodName

                when (food.mealType) {
                    AppConstants.BREAKFAST -> {
                        breakfast_linearLayout.addView(singleFoodView)
                    }
                    AppConstants.DINNER -> {
                        dinner_linearLayout.addView(singleFoodView)
                    }
                    AppConstants.SNACKS -> {
                        snacks_linearLayout.addView(singleFoodView)
                    }
                    AppConstants.LUNCH -> {
                        lunch_linearLayout.addView(singleFoodView)
                    }
                }
                proteinConsumed += food.protein
                carbsConsumed += food.carbs
                fatConsumed += food.fats
                caloriesConsumed += food.calories

                when (food.mealType) {
                    AppConstants.BREAKFAST -> {
                        breakfastCalories += food.calories
                    }
                    AppConstants.LUNCH -> {
                        lunchCalories += food.calories
                    }
                    AppConstants.SNACKS -> {
                        snacksCalories += food.calories
                    }
                    AppConstants.DINNER -> {
                        dinnerCalories += food.calories
                    }
                }
            }
            val proteinConsumedPercent = roundingFormat.format(proteinConsumed / goalProtein * 100)
            val carbsConsumedPercent = roundingFormat.format(carbsConsumed / goalCarbs * 100)
            val fatsConsumedPercent = roundingFormat.format(fatConsumed / goalFat * 100)

            carbsConsumedPercentTextView.text = "$carbsConsumedPercent%"
            proteinConsumedPercentTextView.text = "$proteinConsumedPercent%"
            fatConsumedPercentTextView.text = "$fatsConsumedPercent%"


            val fatLeft = goalFat - fatConsumed
            val caloriesLeft = goalCalories - caloriesConsumed
            val proteinLeft = goalProtein - proteinConsumed
            val carbsLeft = goalCarbs - carbsConsumed

            fatLeftGmTextView.text = if (fatLeft > 0) {
                fatLeft.toInt().toString()
            } else {
                "0"
            }

            carbsLeftGmTextView.text = if (carbsLeft > 0) {
                carbsLeft.toInt().toString()
            } else {
                "0"
            }

            caloriesLeftKcalTextView.text = if (caloriesLeft > 0) {
                caloriesLeft.toInt().toString()
            } else {
                "0"
            }

            proteinLeftGmTextView.text = if (proteinLeft < 0) {
                proteinLeft.toInt().toString()
            } else {
                "0"
            }




            caloriesConsumedKcalTextView.text = caloriesConsumed.toInt().toString()

            dailyGoalKcalTextView.text = goalCalories.toInt().toString()

            caloriesSeekArc.progress = caloriesConsumed.toInt()
            carbsSeekArc.progress = carbsConsumed.toInt()
            proteinSeekArc.progress = proteinConsumed.toInt()
            fatSeekArc.progress = fatConsumed.toInt()


            breakfastConsumedCalTextView.text = roundingFormat.format(breakfastCalories)
            lunchConsumedCalTextView.text = roundingFormat.format(lunchCalories)
            snacksConsumedCalTextView.text = roundingFormat.format(snacksCalories)
            dinnerConsumedCalTextView.text = roundingFormat.format(dinnerCalories)

            val brekfastCalsRecommended = (goalCalories * AppConstants.BREAKFAST_CALORIES_PERCENTAGE / 100).toFloat()
            val lunchCalsRecommended = (goalCalories * AppConstants.LUNCH_CALORIES_PERCENTAGE / 100).toFloat()
            val snacksCalsRecommended = (goalCalories * AppConstants.SNACKS_CALORIES_PERCENTAGE / 100).toFloat()
            val dinnerCalsRecommended = (goalCalories * AppConstants.DINNER_CALORIES_PERCENTAGE / 100).toFloat()
            breakfastProgress.max = brekfastCalsRecommended
            lunchProgress.max = lunchCalsRecommended
            snacksProgress.max = snacksCalsRecommended
            dinnerProgress.max = dinnerCalsRecommended

            breakfastRecommendedTextView.text = brekfastCalsRecommended.toInt().toString()
            lunchRecommendedTextView.text = lunchCalsRecommended.toInt().toString()
            snacksRecommendedTextView.text = snacksCalsRecommended.toInt().toString()
            dinnerRecommendedTextView.text = dinnerCalsRecommended.toInt().toString()

            breakfastProgress.progress = breakfastCalories.toFloat()
            lunchProgress.progress = lunchCalories.toFloat()
            snacksProgress.progress = snacksCalories.toFloat()
            dinnerProgress.progress = dinnerCalories.toFloat()
        })

        return rootView
    }

    private lateinit var viewModel: DietViewModel
    lateinit var db: AppDatabase
    private var goalCalories = 1400.0
    private var goalProtein = 210.0
    private var goalCarbs = 88.0
    private var goalFat = 23.0
    lateinit var session: Session
    private lateinit var roundingFormat: DecimalFormat
    fun initialize() {
        //init format
        roundingFormat = DecimalFormat("#")
        roundingFormat.roundingMode = RoundingMode.CEILING
        instantiateDb()

        //setup ViewModel
        viewModel = ViewModelProviders.of(this).get(DietViewModel()::class.java)



        caloriesSeekArc.max = goalCalories.toInt()
        proteinSeekArc.max = goalProtein.toInt()
        carbsSeekArc.max = goalCarbs.toInt()
        fatSeekArc.max = goalFat.toInt()

        dailyGoalKcalTextView.text = goalCalories.toString()
        proteinLeftGmTextView.text = goalProtein.toString()
        carbsLeftGmTextView.text = goalCarbs.toString()
        fatLeftGmTextView.text = goalFat.toString()

        session = Session(activity as ActivityDashboard)

        if (isGoalDataAvailable()) {
            goalCalories = session.goalCalories!!
            goalFat = session.goalFat!!
            goalCarbs = session.goalCarbs!!
            goalProtein = session.goalProtein!!
            setGoalDataAvailableView()
        } else {
            setGoalDataNotAvailableView()
        }

    }

    private fun setGoalDataNotAvailableView() {
        dietView.visibility = View.GONE
        temporaryLinearLayout.visibility = View.VISIBLE
    }

    private fun setGoalDataAvailableView() {
        dietView.visibility = View.VISIBLE
        temporaryLinearLayout.visibility = View.GONE
    }

    private fun isGoalDataAvailable(): Boolean {
        return session.goalCalories != -1.0 &&
                session.goalCarbs != -1.0 &&
                session.goalFat != -1.0 &&
                session.goalProtein != -1.0
    }

    private fun instantiateDb() {
        db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "HasterDb.db")
                .build()
    }
}