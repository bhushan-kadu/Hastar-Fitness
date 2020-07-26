package com.hastarfitness.hastarfitnessapp.diet.searchAndCustomFoodList

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.diet.foodSearch.FoodSearchActivity
import com.hastarfitness.hastarfitnessapp.profile.MyViewPager
import kotlinx.android.synthetic.main.activity_searched_and_custom_food_list.*
import kotlinx.android.synthetic.main.activity_user_profile.toolbar
import kotlinx.android.synthetic.main.activity_user_profile.viewPager

class SearchedAndCustomFoodListActivity : AppCompatActivity() {
    private lateinit var myViewPager: MyViewPager
    var mealType = ""
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searched_and_custom_food_list)

        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        rootLinearLayout.requestFocus()

        initialize()

        //set view pager
        myViewPager = viewPager as MyViewPager
        var list = listOf<String>("Reports", "Fitness Data")
        viewPager.adapter = SearchedAndCustomFoodViewPagerAdapter(supportFragmentManager)


        invisibleSearch_layout.setOnClickListener {
            val i = Intent(this, FoodSearchActivity::class.java)
            i.putExtra(AppConstants.MEAL_TYPE, mealType)
            startActivity(i)
            overridePendingTransition(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
        }
    }

    fun initialize() {
        mealType = intent.getStringExtra(AppConstants.MEAL_TYPE)!!
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> onBackPressed()
        }
        return true
    }




}