package com.hastarfitness.hastarfitnessapp.diet.foodSearch

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.hastarfitness.hastarfitnessapp.Internet
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.diet.DietViewModel
import com.hastarfitness.hastarfitnessapp.models.foodSearchModels.Food
import kotlinx.android.synthetic.main.activity_food_search.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class FoodSearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_search)


        initialize()

        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        search_bar.requestFocus()
        foodList_recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MyFoodSearchListAdapter(mutableListOf<Food>(), this@FoodSearchActivity)
        }

        var queryString = ""
        //search bar listener - setup search bar
        search_bar.setOnQueryTextListener(object : SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
            init {
                viewModel.isInternetConnected.observe(this@FoodSearchActivity, Observer {

                    if (it) {
                        if (internetSnackbar.isShown) {
                            internetSnackbar.dismiss()
                        }
                        viewModel.setup(queryString)
                    } else {
                        internetSnackbar.show()
                    }

                })

                viewModel.foodSearches.observe(this@FoodSearchActivity, Observer {
                    val adapter = (foodList_recyclerView.adapter as MyFoodSearchListAdapter)
                    if (it.foodSearchCriteria.query == queryString) {
                        if (it.foods.isNotEmpty()) {
                            adapter.foodFilterList = ArrayList(it.foods)
                            adapter.notifyDataSetChanged()

                            setFoundView()
                        } else {
                            setNotFoundView()
                        }
                    }

                })

            }
            override fun onQueryTextSubmit(query: String?): Boolean {
                setLoadingView()
                viewModel.checkIfInternetConnected()
                queryString = query!!

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
               queryString = newText.toString()
                return false
            }
        })

    }

    fun setInitialView() {
        notFound_view.visibility = View.INVISIBLE
        loading_view.visibility = View.INVISIBLE
        recyclerView_layout.visibility = View.INVISIBLE
    }

    fun setNotFoundView() {
        loading_view.visibility = View.INVISIBLE
        notFound_view.visibility = View.VISIBLE
    }

    fun setFoundView() {
        notFound_view.visibility = View.INVISIBLE
        loading_view.visibility = View.INVISIBLE
        recyclerView_layout.visibility = View.VISIBLE
    }

    fun setLoadingView() {
        loading_view.visibility = View.VISIBLE
        recyclerView_layout.visibility = View.INVISIBLE
    }

    private lateinit var viewModel: DietViewModel
    private lateinit var internetSnackbar: Snackbar
    lateinit var db: AppDatabase
    var mealType = ""
    fun initialize() {

        instantiateDb()

        //setup ViewModel
        viewModel = ViewModelProvider(this).get(DietViewModel::class.java)
        mealType = intent.getStringExtra(AppConstants.MEAL_TYPE)!!

        internetSnackbar = Snackbar.make(rootLl, "Internet Disconnected", Snackbar.LENGTH_LONG)
                .setAction("CLOSE") { }
                .setActionTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light))


    }

    fun showSnackBar() {
        Snackbar.make(rootLl, "Internet Disconnected", Snackbar.LENGTH_LONG)
                .setAction("CLOSE") { }
                .setActionTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
                .show()
    }

    private fun instantiateDb() {
        db = Room.databaseBuilder(this, AppDatabase::class.java, "HasterDb.db")
                .build()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }
}