package com.hastarfitness.hastarfitnessapp.selectBaseWorkoutPlan

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.createYourOwnPlan.CreateYourOwnPlanActivity
import com.hastarfitness.hastarfitnessapp.database.WorkoutPlansDbModel
import com.squareup.picasso.Picasso

/**
 * Adapter for workout plans list
 * @param exercise list of workout plans
 * @param activity activity where recycler view resides i.e. WorkoutPlansListActivity
 *
 */

class MyWorkoutPlansListAdapter(private val exercise: List<WorkoutPlansDbModel>, val activity: WorkoutPlansListActivity) : RecyclerView.Adapter<MyWorkoutPlansListAdapter.ViewHolder>(),Filterable {
    var exerciseFilterList = ArrayList<WorkoutPlansDbModel>()
    private lateinit var recyclerView: RecyclerView
    init {
        exerciseFilterList = ArrayList(exercise)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyWorkoutPlansListAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.single_base_plan_view_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun getItemCount() = exerciseFilterList.size

    override fun onBindViewHolder(holder: MyWorkoutPlansListAdapter.ViewHolder, position: Int) {
        val curItem = exerciseFilterList[position]
        holder.title.text = curItem.name
        holder.desc.text =  try {
            curItem.desc.substring(0, 225) + "....."
        }catch (e: Exception){
            curItem.desc
        }
        holder.selectBtn
        holder.linearLayout.tag = curItem.id

        val imageFilePath = "file:///android_asset/plansThumbnails/"+curItem.name+".webp"
        // Load the image into image view from assets folder
        Picasso.get()
                .load(imageFilePath)
                .placeholder(R.drawable.ic_placeholder_img)
                .into(holder.thumbnailImageView)

        val context = holder.linearLayout.context
        holder.linearLayout.setOnClickListener{
            recyclerView.layoutManager?.scrollToPosition(position)
        }
        holder.linearLayout.setOnClickListener{
            val i = Intent(activity, CreateYourOwnPlanActivity::class.java)
            i.putExtra(AppConstants.WORKOUT_PLAN_ID, curItem.id)
            i.putExtra(AppConstants.WORKOUT_TYPE, curItem.type)
            it.context.startActivity(i)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val desc: TextView = itemView.findViewById(R.id.desc_text)
        val selectBtn: Button = itemView.findViewById(R.id.select_btn)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.recycler_linear_layout)
        val thumbnailImageView: ImageView = itemView.findViewById(R.id.image)
    }

    override fun getFilter(): Filter {
        return object :Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var searchParam = constraint.toString()
                if(searchParam.isEmpty() || searchParam.equals("All Plans")){
                    exerciseFilterList = ArrayList(exercise)
                }else if(searchParam == "My Plans"){
                    val resultList = ArrayList<WorkoutPlansDbModel>()
                    for (singleExercise in exercise){
                        if(singleExercise.isUserPlan == 1){
                            resultList.add(singleExercise)
                        }
                    }
                    exerciseFilterList = resultList
                } else if(searchParam == "Fav Plans"){
                    val resultList = ArrayList<WorkoutPlansDbModel>()
                    for (singleExercise in exercise){
                        if(singleExercise.isFav == 1){
                            resultList.add(singleExercise)
                        }
                    }
                    exerciseFilterList = resultList
                } else{
                    val resultList = ArrayList<WorkoutPlansDbModel>()
                    for (singleExercise in exercise){
                        if(singleExercise.intensity.toLowerCase().contains(searchParam.toLowerCase().trim())|| singleExercise.name.toLowerCase().contains(searchParam.toLowerCase())){
                            resultList.add(singleExercise)
                        }
                    }
                    exerciseFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = exerciseFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
               exerciseFilterList = results?.values as ArrayList<WorkoutPlansDbModel>
                notifyDataSetChanged()
            }

        }
    }
}