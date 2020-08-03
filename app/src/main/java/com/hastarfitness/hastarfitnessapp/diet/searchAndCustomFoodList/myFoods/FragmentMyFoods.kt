package com.hastarfitness.hastarfitnessapp.diet.searchAndCustomFoodList.myFoods

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.ViewModel
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.diet.CreateNewFoodActivity
import java.lang.Exception

class FragmentMyFoods : Fragment() {
    lateinit var foodList: RecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        initialize()
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragments_my_foods, container, false)
        val addFoodBtn = rootView.findViewById<Button>(R.id.addFood_button)
        val addFoodFab = rootView.findViewById<FloatingActionButton>(R.id.addFood_fab)
        val placeholderLayout = rootView.findViewById<LinearLayout>(R.id.placeHolder_layout)
        foodList = rootView.findViewById<RecyclerView>(R.id.foodList_recyclerView)

        viewModel.getAllCustomAddedFoods(db)
        viewModel.allCustomAddedFoods.observe(requireActivity(), Observer {
            foodList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = MyFoodsListAdapter(it, this@FragmentMyFoods)
            }
            if ((foodList.adapter as MyFoodsListAdapter).foodFilterList.size > 0){
                placeholderLayout.visibility = View.GONE
                foodList.visibility = View.VISIBLE
            }else{
                placeholderLayout.visibility = View.VISIBLE
                foodList.visibility = View.INVISIBLE
            }
        })



        addFoodBtn.setOnClickListener {
            startActivity(Intent(context, CreateNewFoodActivity::class.java))
        }
        addFoodFab.setOnClickListener {
            startActivity(Intent(context, CreateNewFoodActivity::class.java))
        }

        return rootView
    }
    private lateinit var viewModel: ViewModel
    lateinit var db: AppDatabase
    fun initialize() {

        instantialteDb()

        //setup ViewModel
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)
    }

    private fun instantialteDb() {
        db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "HasterDb.db")
                .build()
    }

    override fun onResume() {

        try {
            viewModel.getAllCustomAddedFoods(db)
            viewModel.allCustomAddedFoods.observe(requireActivity(), Observer {
                (foodList.adapter as MyFoodsListAdapter).foodFilterList = ArrayList(it)
                (foodList.adapter as MyFoodsListAdapter).notifyDataSetChanged()
            })
             }
        catch (e:Exception){}



        super.onResume()
    }
}