package com.hastarfitness.hastarfitnessapp.settings

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import com.hastarfitness.hastarfitnessapp.settings.ItemMoveCallback.ItemTouchHelperContract
import com.hastarfitness.hastarfitnessapp.settings.RecyclerViewAdapter.MyViewHolder
import kotlinx.coroutines.withContext
import java.util.*

class RecyclerViewAdapter(private val data: ArrayList<String>, private val mStartDragListener: StartDragListener, val activity: AppSettingsActivity) : RecyclerView.Adapter<MyViewHolder>(), ItemTouchHelperContract {

    lateinit var session:Session
    inner class MyViewHolder(var rowView: View) : RecyclerView.ViewHolder(rowView) {
        var imageView: ImageView = rowView.findViewById(R.id.imageView)
        val spinner: Spinner = rowView.findViewById(R.id.planType_spinner)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.single_drag_drop_view, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {

    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.imageView.setOnTouchListener { v, event ->
            if (event.action ==
                    MotionEvent.ACTION_DOWN) {
                mStartDragListener.requestDrag(holder)
            }
            false
        }

        session = Session(activity)
        val types: MutableList<String> = ArrayList()
        types.add(AppConstants.FULL_BODY.capitalize())
        types.add(AppConstants.UPPER_BODY.capitalize())
        types.add(AppConstants.CORE_STRENGTH.capitalize())
        types.add(AppConstants.LOWER_BODY.capitalize())


        // Creating adapter for spinner
        val dataAdapter = ArrayAdapter(holder.imageView.context, android.R.layout.simple_spinner_item, types)

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        var day = "";
        day = when(position){
            0 -> AppConstants.MONDAY
            1 -> AppConstants.TUESDAY
            2 -> AppConstants.WEDNESDAY
            3 -> AppConstants.THURSDAY
            4 -> AppConstants.FRIDAY
            5 -> AppConstants.SATURDAY
            6 -> AppConstants.SUNDAY
            else -> ""
        }
        val workoutSubType = when(day){
            AppConstants.MONDAY -> session.mondayBodyWeight
            AppConstants.TUESDAY -> session.tuesdayBodyWeight
            AppConstants.WEDNESDAY -> session.wednesdayBodyWeight
            AppConstants.THURSDAY -> session.thursdayBodyWeight
            AppConstants.FRIDAY -> session.fridayBodyWeight
            AppConstants.SATURDAY -> session.saturdayBodyWeight
            AppConstants.SUNDAY -> session.sundayBodyWeight
            else -> ""
        }
        val itemPos = when(workoutSubType){
            AppConstants.FULL_BODY -> 0
            AppConstants.UPPER_BODY -> 1
            AppConstants.CORE_STRENGTH -> 2
            AppConstants.LOWER_BODY -> 3
            else -> -1
        }

        // attaching data adapter to spinner
        holder.spinner.adapter = dataAdapter
        holder.spinner.setSelection(itemPos)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(data, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(data, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onRowSelected(myViewHolder: MyViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.GRAY)
    }

    override fun onRowClear(myViewHolder: MyViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE)
    }

}