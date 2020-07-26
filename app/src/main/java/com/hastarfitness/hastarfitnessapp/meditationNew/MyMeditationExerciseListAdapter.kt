package com.hastarfitness.hastarfitnessapp.meditationNew

import android.content.Intent
import com.hastarfitness.hastarfitnessapp.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.database.YogaExerciseDbModel
import com.squareup.picasso.Picasso
import java.io.Serializable

/**
 * Adapter for yoga list recycler view
 *
 * @param exercise list of yoga exercises to be shown
 * @param activity activity where yoga exercises will be shown
 *
 * @author Bhushan Kadu
 */
class MyMeditationExerciseListAdapter(private val exercise: List<YogaExerciseDbModel>, val activity: MeditationListActivity) : RecyclerView.Adapter<MyMeditationExerciseListAdapter.ViewHolder>() {
    var i: Intent = Intent(activity, ShowMeditationDescAndVideoActivity::class.java)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMeditationExerciseListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_exercise_view_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = exercise.size

    override fun onBindViewHolder(holder: MyMeditationExerciseListAdapter.ViewHolder, position: Int) {
        holder.title.text = exercise[position].name
        holder.linearLayout.tag = exercise[position].id
        val context = holder.linearLayout.context
        holder.linearLayout.setOnClickListener{
            i.putExtra(AppConstants.YOGA_EXERCISES, exercise as Serializable)
            i.putExtra("position", position )
            context.startActivity(i)
        }
        val videoId = exercise[position].img.split("?")[1].split("&")[0].split("=")[1]
        val thumbnailUrl = "https://img.youtube.com/vi/${videoId}/default.jpg"
        Picasso.get()
                .load(thumbnailUrl)
                .placeholder(R.drawable.ic_placeholder_img)
                .into(holder.thumbnailImageView)

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.recycler_linear_layout)
        val thumbnailImageView: ImageView = itemView.findViewById(R.id.image)
    }
}