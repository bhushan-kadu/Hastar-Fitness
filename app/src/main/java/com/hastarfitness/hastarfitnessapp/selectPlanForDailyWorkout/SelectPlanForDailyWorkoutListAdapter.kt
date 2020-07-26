package com.hastarfitness.hastarfitnessapp.selectPlanForDailyWorkout

import android.content.Intent
import com.hastarfitness.hastarfitnessapp.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.codesgood.views.JustifiedTextView
import com.hastarfitness.hastarfitnessapp.models.Exercise
import com.hastarfitness.hastarfitnessapp.ViewModel
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.database.WorkoutPlansDbModel
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import com.hastarfitness.hastarfitnessapp.finalExerciseListBeforeStartingWorkout.FinalExerciseListActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception
import kotlin.collections.ArrayList

/**
 * Adapter for workout plans list
 * @param workoutPlans workout plans to be shown to recycler view
 * @param activity activity where plans to be shown
 */
class SelectPlanForDailyWorkoutListAdapter(private val workoutPlans: List<WorkoutPlansDbModel>, val activity: SelectPlanForDailyWorkoutActivity) : RecyclerView.Adapter<SelectPlanForDailyWorkoutListAdapter.ViewHolder>(), Filterable {
    var viewModel: ViewModel = ViewModelProviders.of(activity).get(ViewModel()::class.java)
    var db: AppDatabase = Room.databaseBuilder(activity,
            AppDatabase::class.java, "HasterDb.db")
            .build()
    var i: Intent = Intent(activity, FinalExerciseListActivity::class.java)
    var exerciseFilterList = ArrayList<Exercise>()
    var exerciseOriginalList = ArrayList<Exercise>()
    lateinit var recyclerView: RecyclerView
    val session = Session(activity)

    init {
        exerciseOriginalList = workoutPlans.map {
            Exercise(
                    it.name,
                    it.type,
                    it.desc,
                    " ",
                    it.intensity,
                    it.id,
                    0,
                    0.0,
                    0.0,
                    it.isFav == 1)
        } as ArrayList<Exercise>
        exerciseFilterList = exerciseOriginalList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectPlanForDailyWorkoutListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_workout_plan_view, parent, false)
        return ViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun getItemCount() = exerciseFilterList.size

    override fun onBindViewHolder(holder: SelectPlanForDailyWorkoutListAdapter.ViewHolder, position: Int) {
        val curItem = exerciseFilterList[position]
        val context = holder.linearLayout.context
        holder.title.text = curItem.name
        holder.desc.text = try {
            curItem.desc.substring(0, 225) + "....."
        }catch (e:Exception){
            curItem.desc
        }
        holder.linearLayout.tag = curItem.id
        val imageFilePath = "file:///android_asset/plansThumbnails/"+curItem.name.trim()+".webp"
        // Load the image into image view from assets folder
        Picasso.get()
                .load(imageFilePath)
                .placeholder(R.drawable.ic_placeholder_img)
                .into(holder.thumbnailImageView, object :Callback{
                    override fun onSuccess() {
1
                    }

                    override fun onError(e: Exception?) {
                       e
                    }
                })

        holder.linearLayout.setOnClickListener {
            recyclerView.layoutManager?.scrollToPosition(position)

            i.putExtra(AppConstants.IS_SUGGESTED_PLAN, false)
            i.putExtra(AppConstants.WORKOUT_TYPE, AppConstants.BODY_WEIGHT)//this needs to be updated
            i.putExtra(AppConstants.INTENSITY, session.intensity)
            i.putExtra(AppConstants.WORKOUT_TIME_IN_MINUTES, 20)
            i.putExtra(AppConstants.WORKOUT_PLAN_ID, curItem.id)

            context.startActivity(i)
        }
        //if cur item is selected then change drawable to fav else change to not fav
        if (curItem.isSelected) {
            holder.favIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_filled))
        } else {
            holder.favIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_empty))
        }

        //fav Icon item click listener
        holder.favIcon.setOnClickListener {
            curItem.isSelected = !curItem.isSelected
            if (curItem.isSelected) {
                //if item is selected then make it fav in db
                viewModel.togglePlanFavById(db, curItem.id, 1)
            } else {
                //else make item un favourite
                viewModel.togglePlanFavById(db, curItem.id, 0)
            }
            //change the drawable after values changed in db
            viewModel.insertedRowInt.observe(activity, Observer {
                if (curItem.isSelected) {
                    holder.favIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_filled))
                } else {
                    holder.favIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_empty))
                }
            })
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val desc: TextView = itemView.findViewById(R.id.desc_text)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.recycler_linear_layout)
        val favIcon: ImageView = itemView.findViewById(R.id.fav_icon)
        val thumbnailImageView: ImageView = itemView.findViewById(R.id.image)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchParam = constraint.toString()
                exerciseFilterList = if (searchParam.isEmpty() || searchParam == "All Plans") {
                    exerciseOriginalList
                } else if (searchParam == "My Plans") {
                    val resultList = ArrayList<Exercise>()
                    for (singleExercise in workoutPlans) {
                        if (singleExercise.isUserPlan == 1) {
                            resultList.add(exerciseOriginalList.find { it.id == singleExercise.id }!!)
                        }
                    }
                    resultList
                } else if (searchParam == "Fav Plans") {
                    val resultList = ArrayList<Exercise>()
                    for (singleExercise in exerciseOriginalList) {
                        if (singleExercise.isSelected) {
                            resultList.add(exerciseOriginalList.find { it.id == singleExercise.id }!!)
                        }
                    }
                    resultList
                } else {
                    val resultList = ArrayList<Exercise>()
                    for (singleExerciese in exerciseOriginalList) {
                        if (singleExerciese.intensity.toLowerCase().contains(searchParam.toLowerCase().trim())) {
                            resultList.add(singleExerciese)
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