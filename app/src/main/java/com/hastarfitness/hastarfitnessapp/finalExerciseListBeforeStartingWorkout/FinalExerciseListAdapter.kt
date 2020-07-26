package com.hastarfitness.hastarfitnessapp.finalExerciseListBeforeStartingWorkout

import com.hastarfitness.hastarfitnessapp.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.hastarfitness.hastarfitnessapp.customDialogueToShowExerciseInfo.DlgShowExerciseInfo
import com.hastarfitness.hastarfitnessapp.database.ExerciseDbModel
import com.squareup.picasso.Picasso
import kotlin.collections.ArrayList

/**
 * Adapter to show final exercise recycler view
 * @param exercises list of exercises to be shown
 * @param activity activity where list is being showed
 *
 * @author Bhushan Kadu
 */
class FinalExerciseListAdapter(private val exercises: List<ExerciseDbModel>, val activity: FinalExerciseListActivity, val dialog: DlgShowExerciseInfo) : RecyclerView.Adapter<FinalExerciseListAdapter.ViewHolder>(), Filterable {
    var exerciseFilterList = ArrayList<ExerciseDbModel>()
    lateinit var recyclerView: RecyclerView

    init {
        exerciseFilterList = ArrayList(exercises)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinalExerciseListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_exercise_view_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun getItemCount() = exerciseFilterList.size

    override fun onBindViewHolder(holder: FinalExerciseListAdapter.ViewHolder, position: Int) {
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

        //item click listener
        holder.linearLayout.setOnClickListener {
            recyclerView.layoutManager?.scrollToPosition(position)
            dialog.setMessage(curItem.desc, curItem.name)
            dialog.loadThumbnail(curItem.name)
            dialog.downloadVide(curItem.name)
            dialog.pos = position
            dialog.show()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.recycler_linear_layout)
        val thumbnailImageView: ImageView = itemView.findViewById(R.id.image)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchParam = constraint.toString()
                exerciseFilterList = if (searchParam.isEmpty() || searchParam.equals("All Plans")) {
                    ArrayList(exercises)
                } else {
                    val resultList = ArrayList<ExerciseDbModel>()
                    for (singleExercise in exercises) {
                        if (singleExercise.intensity.toLowerCase().contains(searchParam.toLowerCase().trim()) || singleExercise.name.toLowerCase().contains(searchParam.toLowerCase())) {
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
                exerciseFilterList = results?.values as ArrayList<ExerciseDbModel>
                notifyDataSetChanged()
            }
        }
    }
}