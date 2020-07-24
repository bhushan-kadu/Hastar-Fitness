package com.hastarfitness.hastarfitnessapp.selectExerciseForOwnPlan

import android.content.Intent
import com.hastarfitness.hastarfitnessapp.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hastarfitness.hastarfitnessapp.customDialogueToShowExerciseInfo.DlgShowExerciseInfo
import com.hastarfitness.hastarfitnessapp.models.Exercise
import com.hastarfitness.hastarfitnessapp.database.ExerciseDbModel
import com.hastarfitness.hastarfitnessapp.yoga.ShowYogaDescAndVideoActivity
import com.squareup.picasso.Picasso
import kotlin.collections.ArrayList

/**
 * Adapter for exercise list
 */
class ExerciseListAdapter(private val exercise: List<ExerciseDbModel>, val activity: ExerciseListActivity, val dialog: DlgShowExerciseInfo) : RecyclerView.Adapter<ExerciseListAdapter.ViewHolder>(),Filterable {
    var i: Intent = Intent(activity, ShowYogaDescAndVideoActivity::class.java)
    var exerciseFilterList = ArrayList<Exercise>()
    var exerciseOriginalList = ArrayList<Exercise>()
    var selectedItems = ArrayList<Exercise>()
    lateinit var recyclerView: RecyclerView
    init {
        exerciseOriginalList = exercise.map {
            Exercise(
                    it.name,
                    it.type,
                    it.desc,
                    it.img,
                    it.intensity,
                    it.id,
                    it.time,
                    it.mmet,
                    it.fmet,
            false)
        } as ArrayList<Exercise>
        exerciseFilterList = exerciseOriginalList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseListAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.single_select_exercise_view, parent, false)
        return ViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun getItemCount() = exerciseFilterList.size

    override fun onBindViewHolder(holder: ExerciseListAdapter.ViewHolder, position: Int) {
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

        holder.linearLayout.setOnClickListener{
            recyclerView.layoutManager?.scrollToPosition(position)
            dialog.setMessage(curItem.desc, curItem.name)
            dialog.loadThumbnail(curItem.name)
            dialog.downloadVide(curItem.name)
            dialog.pos = position
            dialog.show()
        }

        //in some cases, it will prevent unwanted situations
        holder.selectCbx.setOnCheckedChangeListener(null);

        //if true, your checkbox will be selected, else unselected
        holder.selectCbx.isChecked = curItem.isSelected;

        if (curItem.isSelected){
            //if cur item is selected change backColor and add to selected items list
            holder.linearLayout.background = ContextCompat.getDrawable(context, R.color.color_item_selected)
            selectedItems.add(exerciseFilterList[position])

        }else{
            //else if cur item is not selected change backColor and remove from selected items list
            holder.linearLayout.background = ContextCompat.getDrawable(context, R.color.mdtp_white)
            selectedItems.remove(exerciseFilterList[position])
        }

        //item checkbox click listener
        holder.selectCbx.setOnCheckedChangeListener { _, isChecked ->
            curItem.isSelected = isChecked
            if (isChecked){
                holder.linearLayout.background = ContextCompat.getDrawable(context, R.color.color_item_selected)
                selectedItems.add(exerciseFilterList[position])

            }else{
                holder.linearLayout.background = ContextCompat.getDrawable(context, R.color.mdtp_white)
                selectedItems.remove(exerciseFilterList[position])
            }
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val selectCbx: CheckBox = itemView.findViewById(R.id.is_item_selected_checkbox)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.recycler_linear_layout)
        val thumbnailImageView: ImageView = itemView.findViewById(R.id.image)
    }

    override fun getFilter(): Filter {
        return object :Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchParam = constraint.toString()
                exerciseFilterList = if(searchParam.isEmpty() || searchParam == "All Exercises"){
                    exerciseOriginalList
                }else{
                    val resultList = ArrayList<Exercise>()
                    for (singleExercise  in exerciseOriginalList){
                        if(singleExercise.name.toLowerCase().contains(searchParam.toLowerCase()) ||
                                singleExercise.intensity.toLowerCase().contains(searchParam.toLowerCase().trim())){
                            resultList.add(singleExercise )
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