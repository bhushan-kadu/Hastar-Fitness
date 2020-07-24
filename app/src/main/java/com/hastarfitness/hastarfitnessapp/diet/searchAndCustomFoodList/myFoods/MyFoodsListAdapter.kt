package com.hastarfitness.hastarfitnessapp.diet.searchAndCustomFoodList.myFoods

import android.content.Intent
import com.hastarfitness.hastarfitnessapp.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.database.CustomFoodsDbModel
import com.hastarfitness.hastarfitnessapp.diet.FoodSelectedActivity
import com.hastarfitness.hastarfitnessapp.diet.searchAndCustomFoodList.SearchedAndCustomFoodListActivity
import kotlin.collections.ArrayList

/**
 * Adapter for workout plans list
 * @param foodList list of foods
 * @param activity activity where recycler view resides i.e. WorkoutPlansListActivity
 *
 */

class MyFoodsListAdapter(private val foodList: List<CustomFoodsDbModel>, val fragmentMyFoods: FragmentMyFoods) : RecyclerView.Adapter<MyFoodsListAdapter.ViewHolder>() {
    var foodFilterList = ArrayList<CustomFoodsDbModel>()
    private lateinit var recyclerView: RecyclerView
    init {
        foodFilterList = ArrayList(foodList)
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
        holder.fats.text  = "${curFood.fat}\n(F)"
        holder.protein.text  = "${curFood.protein}\n(P)"
        val measurement = curFood.measurement
        val servingOrGram = curFood.servingsOrGrams
        if(measurement == AppConstants.SERVINGS_MEASUREMENT){
            holder.measurement.text = "per ${servingOrGram.toInt()} serving/s"
        }else{
            holder.measurement.text = "per $servingOrGram grams"
        }

        holder.selectBtn.setOnClickListener {
            val i = Intent(context, FoodSelectedActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            i.putExtra(AppConstants.PROTEIN, curFood.protein)
            i.putExtra(AppConstants.CARBS, curFood.carbs)
            i.putExtra(AppConstants.FAT, curFood.fat)
            i.putExtra(AppConstants.ENERGY, curFood.calories)
            i.putExtra(AppConstants.MEASUREMENT, curFood.measurement)
            i.putExtra(AppConstants.SERVINGS_OR_GRAM_VALUE, curFood.servingsOrGrams)
            i.putExtra(AppConstants.IS_CUSTOM_FOOD, true)
            i.putExtra(AppConstants.FOOD_NAME, curFood.name)
            val activity = fragmentMyFoods.activity as SearchedAndCustomFoodListActivity
            val mealType = activity.mealType
            i.putExtra(AppConstants.MEAL_TYPE, mealType)
            context.startActivity(i)
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.name)
        val protein: TextView = itemView.findViewById(R.id.protein_textView)
        val carbs: TextView = itemView.findViewById(R.id.carbs_textView)
        val fats: TextView = itemView.findViewById(R.id.fat_textView)
        val measurement: TextView = itemView.findViewById(R.id.measurement_textView)
        val selectBtn: Button = itemView.findViewById(R.id.select_btn)
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