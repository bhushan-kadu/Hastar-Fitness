package com.hastarfitness.hastarfitnessapp.diet.searchAndCustomFoodList.recentlySearched

import android.content.Intent
import com.hastarfitness.hastarfitnessapp.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.database.LastSearchedFoods
import com.hastarfitness.hastarfitnessapp.diet.DietViewModel
import com.hastarfitness.hastarfitnessapp.diet.FoodSelectedActivity
import com.hastarfitness.hastarfitnessapp.diet.searchAndCustomFoodList.SearchedAndCustomFoodListActivity
import com.hastarfitness.hastarfitnessapp.models.foodSearchModels.FoodNutrient
import kotlin.collections.ArrayList

/**
 * Adapter for workout plans list
 * @param foodList list of foods
 * @param activity activity where recycler view resides i.e. WorkoutPlansListActivity
 *
 * @author Bhushan Kadu
 */

class RecentlySearchedFoodsListAdapter(private val foodList: List<LastSearchedFoods>, val fragmentLastSearchedFood: FragmentLastSearchedFood) : RecyclerView.Adapter<RecentlySearchedFoodsListAdapter.ViewHolder>() {
    var foodFilterList = ArrayList<LastSearchedFoods>()
    private lateinit var recyclerView: RecyclerView
    val i = Intent(fragmentLastSearchedFood.context, FoodSelectedActivity::class.java)
    init {
        foodFilterList = ArrayList(foodList)
        initialize()
    }
    private lateinit var viewModel: DietViewModel
    lateinit var db: AppDatabase
    fun initialize() {

        instantiateDb()

        //setup ViewModel
        viewModel = ViewModelProvider(fragmentLastSearchedFood.requireActivity()).get(DietViewModel::class.java)
    }

    private fun instantiateDb() {
        db = Room.databaseBuilder(fragmentLastSearchedFood.requireContext(), AppDatabase::class.java, "HasterDb.db")
                .build()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.single_food_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun getItemCount() = foodFilterList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curFood = foodFilterList[position]
        val context = holder.title.context
        holder.title.text = curFood.name
        holder.carbs.text  = "${curFood.carbs}\n(C)"
        holder.fats.text  = "${curFood.fats}\n(F)"
        holder.protein.text  = "${curFood.protein}\n(P)"

        holder.selectBtn.setOnClickListener {
            viewModel.getFoodNutrientsById(db, curFood.fdcId)
            viewModel.foodNutrientDbModel.observe(fragmentLastSearchedFood.requireActivity(),
            Observer { foodNutrientDbModel ->

                val foodNutrient = foodNutrientDbModel.map {
                    FoodNutrient(
                            derivationCode = "",
                            derivationDescription = "",
                            nutrientId = it.nutrientId,
                            nutrientName = it.nutrientName,
                            nutrientNumber = "",
                            unitName = it.unitName,
                            value = it.value
                    )
                }
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                i.putParcelableArrayListExtra(AppConstants.NUTRIENTS, ArrayList(foodNutrient))
                i.putExtra(AppConstants.FOOD_NAME, curFood.name)
                val activity = fragmentLastSearchedFood.activity as SearchedAndCustomFoodListActivity
                val mealType = activity.mealType
                i.putExtra(AppConstants.MEAL_TYPE, mealType)
                i.putExtra(AppConstants.FOOD_ID, curFood.fdcId)
                context.startActivity(i)
            })

        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.name)
        val protein: TextView = itemView.findViewById(R.id.protein_textView)
        val carbs: TextView = itemView.findViewById(R.id.carbs_textView)
        val fats: TextView = itemView.findViewById(R.id.fat_textView)
        val selectBtn: TextView = itemView.findViewById(R.id.select_btn)
    }

//    override fun getFilter(): Filter {
//        return object :Filter(){
//            override fun performFiltering(constraint: CharSequence?): FilterResults {
//                var searchParam = constraint.toString()
//                if(searchParam.isEmpty() || searchParam.equals("All Plans")){
//                    exerciseFilterList = ArrayList(foodList)
//                }else if(searchParam == "My Plans"){
//                    val resultList = ArrayList<CustomFoodsDbModel>()
//                    for (singleExercise in foodList){
//
//                    }
//                    exerciseFilterList = resultList
//                } else if(searchParam == "Fav Plans"){
//                    val resultList = ArrayList<CustomFoodsDbModel>()
//                    for (singleExercise in foodList){
//
//                    }
//                    exerciseFilterList = resultList
//                } else{
//                    val resultList = ArrayList<CustomFoodsDbModel>()
//                    for (singleExercise in foodList){
//                        if(singleExercise.intensity.toLowerCase().contains(searchParam.toLowerCase().trim())|| singleExercise.name.toLowerCase().contains(searchParam.toLowerCase())){
//                            resultList.add(singleExercise)
//                        }
//                    }
//                    exerciseFilterList = resultList
//                }
//                val filterResults = FilterResults()
//                filterResults.values = exerciseFilterList
//                return filterResults
//            }
//
//            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
//               exerciseFilterList = results?.values as ArrayList<WorkoutPlansDbModel>
//                notifyDataSetChanged()
//            }
//
//        }
//    }
}