package com.hastarfitness.hastarfitnessapp.meditation

import android.content.Intent
import com.hastarfitness.hastarfitnessapp.R

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.database.MeditationDbModel
import com.hastarfitness.hastarfitnessapp.database.MeditationDbModelNew
import java.io.Serializable

class MyMeditationExerciseListAdapter(private val exercise: List<MeditationDbModelNew>, val activity: MeditationListActivity) : RecyclerView.Adapter<MyMeditationExerciseListAdapter.ViewHolder>() {
    lateinit var i: Intent

    init {
        i = Intent(activity, AudioActivity::class.java)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMeditationExerciseListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_meditation_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = exercise.size

    override fun onBindViewHolder(holder: MyMeditationExerciseListAdapter.ViewHolder, position: Int) {
        var desc = exercise[position].desc.replace("�", "")
        val name = exercise[position].name
        val context = holder.title.context

        holder.title.text = name

        if (desc.length > 100) {
            holder.desc.text = exercise[position].desc.substring(0, 100) + "....."
        } else {
            holder.desc.text = desc
        }

        holder.downOrOpenBtn.setOnClickListener {
            i.putExtra(AppConstants.MEDITATION_DATA, exercise[position] as Serializable)
            context.startActivity(i)
        }


    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val desc: TextView = itemView.findViewById(R.id.desc_text)
        val downOrOpenBtn: Button = itemView.findViewById(R.id.down_or_open_btn)
    }
}