package com.hastarfitness.hastarfitnessapp.diet.searchAndCustomFoodList.recentlySearched

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.diet.DietViewModel
import com.hastarfitness.hastarfitnessapp.diet.FoodSelectedActivity
import com.hastarfitness.hastarfitnessapp.diet.searchAndCustomFoodList.SearchedAndCustomFoodListActivity
import com.hastarfitness.hastarfitnessapp.selectExerciseForOwnPlan.ExerciseListActivity
import kotlinx.android.synthetic.main.fragment_last_searched_foods.*
import java.lang.Exception

class FragmentLastSearchedFood : Fragment() {
    private fun RecyclerView.addOnItemClickListener(onClickListener: ExerciseListActivity.OnItemClickListener) {
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

    lateinit var recyclerView: RecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        initialize()
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_last_searched_foods, container, false)
        recyclerView = rootView.findViewById<RecyclerView>(R.id.lastSearched_recyclerView)
        val i = Intent(requireActivity(), FoodSelectedActivity::class.java)

        viewModel.getAllLastSearchedFoods(db)
        viewModel.lastSearchedFoodsList.observe(requireActivity(), Observer {

            recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = RecentlySearchedFoodsListAdapter(it, this@FragmentLastSearchedFood)
            }
            val adapter = recyclerView.adapter as RecentlySearchedFoodsListAdapter
            if (adapter.foodFilterList.size > 0) {
                setNonEmptyView()
            }
        })


        (activity as SearchedAndCustomFoodListActivity).mealType
        return rootView
    }

    private fun setNonEmptyView() {
        emptyRecycler_layout.visibility = View.INVISIBLE
        recyclerView.visibility = View.VISIBLE

    }

    fun setEmptyView() {
        emptyRecycler_layout.visibility = View.VISIBLE
        recyclerView.visibility = View.INVISIBLE

    }

    private lateinit var viewModel: DietViewModel
    lateinit var db: AppDatabase
    fun initialize() {
        instantiateDb()

        //setup ViewModel
        viewModel = ViewModelProviders.of(this).get(DietViewModel()::class.java)
    }

    private fun instantiateDb() {
        db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "HasterDb.db")
                .build()
    }

    override fun onResume() {
        try {
            viewModel.getAllLastSearchedFoods(db)
            viewModel.lastSearchedFoodsList.observe(requireActivity(), Observer {
                (recyclerView.adapter as RecentlySearchedFoodsListAdapter).foodFilterList = ArrayList(it)
                (recyclerView.adapter as RecentlySearchedFoodsListAdapter).notifyDataSetChanged()
            })
        } catch (e: Exception) {
        }
        super.onResume()
    }
}