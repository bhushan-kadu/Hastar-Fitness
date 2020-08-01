package com.hastarfitness.hastarfitnessapp.viewPlansFavAndCustom

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import com.hastarfitness.hastarfitnessapp.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.createYourOwnPlan.CreateYourOwnPlanActivity
import com.hastarfitness.hastarfitnessapp.database.WorkoutPlansDbModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_yoga_list.*
import java.lang.Exception
import kotlin.collections.ArrayList

/**
 * Adapter for workout plans list
 * @param exercise list of workout plans
 * @param activity activity where recycler view resides i.e. WorkoutPlansListActivity
 *
 * @author Bhushan Kadu
 */

class ViewPlansAdapter(private val exercise: List<WorkoutPlansDbModel>, val activity: ViewPlansActivity) : RecyclerView.Adapter<ViewPlansAdapter.ViewHolder>(),Filterable {
    var exerciseFilterList = ArrayList<WorkoutPlansDbModel>()
    private lateinit var recyclerView: RecyclerView
    init {
        exerciseFilterList = ArrayList(exercise)
        activity.viewModel.deletedRowInt.observe(activity, Observer {
            activity.finish();
            activity.startActivity(activity.intent);
        })
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPlansAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.single_view_edit_delet_plan_view_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun getItemCount() = exerciseFilterList.size

    override fun onBindViewHolder(holder: ViewPlansAdapter.ViewHolder, position: Int) {
        val curItem = exerciseFilterList[position]
        holder.title.text = curItem.name
        holder.desc.text = try {
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
                .placeholder(R.drawable.ic_user_plan)
                .into(holder.thumbnailImageView)

        val context = holder.linearLayout.context

        holder.linearLayout.setOnClickListener{
            recyclerView.layoutManager?.scrollToPosition(position)
            val i = Intent(activity, CreateYourOwnPlanActivity::class.java)
            i.putExtra(AppConstants.IS_SUGGESTED_PLAN, false)
            i.putExtra(AppConstants.WORKOUT_TYPE, AppConstants.BODY_WEIGHT)//this needs to be updated
            i.putExtra(AppConstants.INTENSITY, activity.intensity)
            i.putExtra(AppConstants.WORKOUT_TIME_IN_MINUTES, 20)
            i.putExtra(AppConstants.WORKOUT_PLAN_ID, curItem.id)
            context.startActivity(i)
        }
        holder.editBtn.setOnClickListener {
            recyclerView.layoutManager?.scrollToPosition(position)
            val i = Intent(activity, CreateYourOwnPlanActivity::class.java)
            i.putExtra(AppConstants.WORKOUT_PLAN_ID, curItem.id)
            i.putExtra(AppConstants.WORKOUT_TYPE, curItem.type)
            i.putExtra(AppConstants.IS_CUSTOMIZE, true)

            context.startActivity(i)
        }

        holder.deleteBtn.setOnClickListener {
            calculateValuesAndShowExitAlert(curItem.id)
        }
    }
    private fun calculateValuesAndShowExitAlert(id:Int) {
        val dlg = androidx.appcompat.app.AlertDialog.Builder(activity, R.style.AlertDialogStyle)
                .setTitle("Delete")
                .setMessage("Do you really want to delete this plan?")
                .setPositiveButton("DELETE", DialogInterface.OnClickListener { dialog, which ->
                    activity.viewModel.deletePlanById(activity.db, id)
                })
                .setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, which ->
                    //hide close dialogue
                    dialog.dismiss()
                }).create()
        //change buttons color in on show listener
        dlg.setOnShowListener {
            val colorNegativeBtn = ContextCompat.getColor(activity, R.color.yellow)
            val colorPositiveBtn = ContextCompat.getColor(activity, R.color.color_gray_66)
            dlg.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(colorNegativeBtn);
            dlg.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(colorPositiveBtn);
        }

        //show dialogue
        dlg.show()
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val desc: TextView = itemView.findViewById(R.id.desc_text)
        val selectBtn: Button = itemView.findViewById(R.id.select_btn)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.recycler_linear_layout)
        val thumbnailImageView: ImageView = itemView.findViewById(R.id.image)
        val editBtn: ImageButton = itemView.findViewById(R.id.edit_Btn)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.delete_Btn)
    }

    override fun getFilter(): Filter {
        return object :Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                when (val searchParam = constraint.toString()) {
                    "All Plans" -> {
                        exerciseFilterList = ArrayList(exercise)
                    }
                    "Favourites" -> {
                        val resultList = ArrayList<WorkoutPlansDbModel>()
                        for (singleExercise in exercise){
                            if(singleExercise.isFav == 1){
                                resultList.add(singleExercise)
                            }
                        }
                        exerciseFilterList = resultList
                    }
                    else -> {
                        val resultList = ArrayList<WorkoutPlansDbModel>()
                        for (singleExercise in exercise){
                            if(singleExercise.type.toLowerCase().contains(searchParam.toLowerCase().trim())|| singleExercise.name.toLowerCase().contains(searchParam.toLowerCase())){
                                resultList.add(singleExercise)
                            }
                        }
                        exerciseFilterList = resultList
                    }
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