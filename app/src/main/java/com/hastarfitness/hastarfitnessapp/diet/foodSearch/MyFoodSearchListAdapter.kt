package com.hastarfitness.hastarfitnessapp.diet.foodSearch

import android.content.Intent
import com.hastarfitness.hastarfitnessapp.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.diet.FoodSelectedActivity
import com.hastarfitness.hastarfitnessapp.models.foodSearchModels.Food
import java.lang.Exception
import kotlin.collections.ArrayList

/**
 * Adapter for workout plans list
 * @param foodList list of foods
 * @param activity activity where recycler view resides i.e. WorkoutPlansListActivity
 *
 */

class MyFoodSearchListAdapter(var foodList: List<Food>, val foodSearchActivity: FoodSearchActivity) : RecyclerView.Adapter<MyFoodSearchListAdapter.ViewHolder>() {
    var foodFilterList = ArrayList<Food>()
    private lateinit var recyclerView: RecyclerView
    init {
        foodFilterList = ArrayList(foodList)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.single_food_search_view, parent, false)
        return ViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun getItemCount() = foodFilterList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curFood = foodFilterList[position]
        holder.title.text = curFood.description
        val context = holder.title.context

        var isSelectActive = true


        var nutrient = curFood.foodNutrients.filter { it.nutrientId == AppConstants.PROTEIN_ID }
        if(nutrient.isEmpty()){
            nutrient = curFood.foodNutrients.filter { it.nutrientName.toLowerCase().contains(AppConstants.PROTEIN) }
        }
        try {
            holder.protein.text = "${nutrient[0].value}g\n(protein)"
        }catch (e:Exception){
            holder.protein.text = "Not Available"
            isSelectActive = false
        }


        nutrient = curFood.foodNutrients.filter { it.nutrientId == AppConstants.CARBS_ID }
        if(nutrient.isEmpty()){
            nutrient = curFood.foodNutrients.filter { it.nutrientName.toLowerCase().contains(AppConstants.CARBS) }
        }
        try {
            holder.carbs.text = "${nutrient[0].value}g\n(carbs)"
        }catch (e:Exception){
            holder.carbs.text = "Not Available"
            isSelectActive = false
        }


        nutrient = curFood.foodNutrients.filter { it.nutrientId == AppConstants.FAT_ID }
        if(nutrient.isEmpty()){
            nutrient = curFood.foodNutrients.filter { it.nutrientName.toLowerCase().contains(AppConstants.FAT) }
        }
        try {
            holder.fats.text = "${nutrient[0].value}g\n(fat)"
        }catch (e:Exception){
            holder.fats.text = "Not Available"
            isSelectActive = false
        }



        holder.selectBtn.setOnClickListener {
            if(isSelectActive){
                val i = Intent(holder.title.context, FoodSelectedActivity::class.java)
                i.putParcelableArrayListExtra(AppConstants.NUTRIENTS, ArrayList(curFood.foodNutrients))
                i.putExtra(AppConstants.FOOD_NAME, curFood.description)
                val activity = foodSearchActivity
                val mealType = activity.mealType
                i.putExtra(AppConstants.MEAL_TYPE, mealType)
                i.putExtra(AppConstants.FOOD_ID, curFood.fdcId)
                context.startActivity(i)
            }else{
                Toast.makeText(context, "All values are not available", Toast.LENGTH_LONG).show()
            }

        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.name)
        val foodItemImageView: ImageView = itemView.findViewById(R.id.foodItem_imageView)
        val selectBtn: Button = itemView.findViewById(R.id.select_btn)
        val protein: TextView = itemView.findViewById(R.id.protein_textView)
        val carbs: TextView = itemView.findViewById(R.id.carbs_textView)
        val fats: TextView = itemView.findViewById(R.id.fat_textView)
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