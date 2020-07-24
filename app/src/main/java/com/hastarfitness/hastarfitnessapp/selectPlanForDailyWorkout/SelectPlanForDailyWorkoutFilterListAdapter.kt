package com.hastarfitness.hastarfitnessapp.selectPlanForDailyWorkout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hastarfitness.hastarfitnessapp.R

/**
 * Filter adapter for WorkoutPlans recycler view
 * @param workoutPlansFilterList list of string to be added to filter
 * @param selectPlanForDailyWorkoutListAdapter adapter where filter will be applied
 */
class SelectPlanForDailyWorkoutFilterListAdapter(private val workoutPlansFilterList: List<String>, private val selectPlanForDailyWorkoutListAdapter: SelectPlanForDailyWorkoutListAdapter) : RecyclerView.Adapter<SelectPlanForDailyWorkoutFilterListAdapter.ViewHolder>() {

    lateinit var recyclerView: RecyclerView
    var selectedItem = 0

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectPlanForDailyWorkoutFilterListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_horizontal_filter_view_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  workoutPlansFilterList.size
    }


    override fun onBindViewHolder(holder: SelectPlanForDailyWorkoutFilterListAdapter.ViewHolder, position: Int) {
        val textView = holder.title
        val context = holder.title.context

        if(position == selectedItem){
            textView.background = ContextCompat.getDrawable(context, R.drawable.rectangle_with_cornerradius_and_border_selected)
        }else{
            textView.background = ContextCompat.getDrawable(context, R.drawable.rectangle_with_cornerradius_and_border)
        }

        textView.text = workoutPlansFilterList[position]

        holder.title.setOnClickListener{
            recyclerView.layoutManager?.scrollToPosition(position)
            selectPlanForDailyWorkoutListAdapter.filter.filter(workoutPlansFilterList[position])
            selectedItem = position
            notifyDataSetChanged()
        }

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
    }
}
