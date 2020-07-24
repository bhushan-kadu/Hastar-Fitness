package com.hastarfitness.hastarfitnessapp.exerciseListForCardioAndBodyWeight

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.models.Exercise
import com.hastarfitness.hastarfitnessapp.customDialogueToShowExerciseInfo.DlgShowExerciseInfo
import com.squareup.picasso.Picasso

class ExerciseListAdapter(private val exercise: List<Exercise>, private val dialogue: DlgShowExerciseInfo) : RecyclerView.Adapter<ExerciseListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_exercise_view_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = exercise.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curItem = exercise[position]
        holder.title.text = curItem.name
        holder.linearLayout.tag = curItem.id

        val imageFilePath = "file:///android_asset/exerciseThumbnails/"+curItem.name+".webp"
        // Load the image into image view from assets folder
        Picasso.get()
                .load(imageFilePath)
                .placeholder(R.drawable.ic_placeholder_img)
                .into(holder.thumbnailImageView)

        holder.itemView.setOnClickListener {
            dialogue.setMessage(exercise[position].desc, exercise[position].name)
            dialogue.pos = position
            dialogue.loadThumbnail(curItem.name)
            dialogue.downloadVide(curItem.name)
            dialogue.show()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.recycler_linear_layout)
        val thumbnailImageView: ImageView = itemView.findViewById(R.id.image)
    }
}