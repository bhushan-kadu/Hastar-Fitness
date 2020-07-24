package com.hastarfitness.hastarfitnessapp.selectBaseWorkoutPlan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hastarfitness.hastarfitnessapp.R

/**
 * Adapter of Filter items recycler view for filtering workout plans
 *  @param workoutPlansFilterList names of filter items to be added to filter
 *  @param myWorkoutPlansListAdapter MyWorkoutPlansListAdapter adapter of intended filter recycler view
 */


class MyFilterWorkoutPlansListAdapter(private val workoutPlansFilterList: List<String>, private val myWorkoutPlansListAdapter: MyWorkoutPlansListAdapter) : RecyclerView.Adapter<MyFilterWorkoutPlansListAdapter.ViewHolder>() {

    lateinit var recyclerView: RecyclerView
    var selectedItem = 0

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFilterWorkoutPlansListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_horizontal_filter_view_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return workoutPlansFilterList.size
    }


    override fun onBindViewHolder(holder: MyFilterWorkoutPlansListAdapter.ViewHolder, position: Int) {
        val textView = holder.title
        val context = holder.title.context

        if(position == selectedItem){
            textView.background = ContextCompat.getDrawable(context, R.drawable.rectangle_with_cornerradius_and_border_selected)
        }else{
            textView.background = ContextCompat.getDrawable(context, R.drawable.rectangle_with_cornerradius_and_border)
        }

        textView.text = workoutPlansFilterList[position]

        holder.title.setOnClickListener {
            recyclerView.layoutManager?.scrollToPosition(position)
            myWorkoutPlansListAdapter.filter.filter(workoutPlansFilterList[position])
            selectedItem = position
            notifyDataSetChanged()
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
    }
}
