package com.hastarfitness.hastarfitnessapp.createYourOwnPlan

import android.content.Intent
import com.hastarfitness.hastarfitnessapp.R

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.hastarfitness.hastarfitnessapp.models.Exercise
import com.hastarfitness.hastarfitnessapp.yoga.ShowYogaDescAndVideoActivity
import com.squareup.picasso.Picasso
import kotlin.collections.ArrayList

/**
 * this is a adapter for list view with features to remove add items to and from list
 */
class ExercisesListAdapter(private val exercise: List<Exercise>, val activity: CreateYourOwnPlanActivity) : RecyclerView.Adapter<ExercisesListAdapter.ViewHolder>(), Filterable {
    var i: Intent = Intent(activity, ShowYogaDescAndVideoActivity::class.java)

    //list to maintain recycler view
    var exerciseFilterList = ArrayList<Exercise>()

    //list to save removed items
    val removedList = ArrayList<Pair<Int, Exercise>>()

    lateinit var recyclerView: RecyclerView

    init {
        exerciseFilterList = ArrayList(exercise)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercisesListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_exercise_view_in_createyourplan, parent, false)
        return ViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun getItemCount() = exerciseFilterList.size

    override fun onBindViewHolder(holder: ExercisesListAdapter.ViewHolder, position: Int) {
        val curItem = exerciseFilterList[position]
        val context = holder.linearLayout.context
        holder.title.text = curItem.name
        holder.linearLayout.tag = curItem.id
        val imageFilePath = "file:///android_asset/exerciseThumbnails/"+curItem.name+".webp"
        // Load the image into image view from assets folder
        Picasso.get()
                .load(imageFilePath)
                .placeholder(R.drawable.ic_placeholder_img)
                .into(holder.thumbnailImageView)

        holder.linearLayout.setOnClickListener {
            recyclerView.layoutManager?.scrollToPosition(position)
        }

        holder.removeItemBtn.setOnClickListener {
            //add removed item to remove list
            removedList.add(Pair(position, exerciseFilterList[position]))
            //remove item from main list
            exerciseFilterList.remove(exerciseFilterList[position])

            notifyItemRemoved(position)
            notifyItemRangeChanged(position, exerciseFilterList.size)
        }

    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.recycler_linear_layout)
        val removeItemBtn: ImageButton = itemView.findViewById<ImageButton>(R.id.remove_item_btn)
        val thumbnailImageView: ImageView = itemView.findViewById(R.id.image)
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchParam = constraint.toString()
                exerciseFilterList = if (searchParam.isEmpty()) {
                    ArrayList(exercise)
                } else {
                    val resultList = ArrayList<Exercise>()
                    for (singleExercise in exercise) {
                        if (singleExercise.name.toLowerCase().contains(searchParam.toLowerCase())) {
                            resultList.add(singleExercise)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = exerciseFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                exerciseFilterList = results?.values as ArrayList<Exercise>
                notifyDataSetChanged()
            }
        }
    }
}