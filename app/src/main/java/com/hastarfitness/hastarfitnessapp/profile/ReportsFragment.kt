package com.hastarfitness.hastarfitnessapp.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.room.Room
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.ViewModel
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.android.synthetic.main.fragment_reports.*
import java.lang.Exception
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class ReportsFragment : Fragment() {

    private lateinit var viewModel: ViewModel
    lateinit var db: AppDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reports, container, false)
    }

    private lateinit var barChart: BarChart
    private lateinit var calenderWidget: MaterialCalendarView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        barChart = view.findViewById(R.id.barChart)
        calenderWidget = view.findViewById(R.id.calendarView) as MaterialCalendarView

        initialize()


        val activity = activity as UserProfileActivity
        val endDate = Calendar.getInstance()
        endDate.add(Calendar.MONTH, 1)
        endDate.add(Calendar.DAY_OF_MONTH, 0)

//        val mydate = CalendarDay.from(endDate[Calendar.YEAR], endDate[Calendar.MONTH], endDate[Calendar.DAY_OF_MONTH]) // year, month, date
//        calenderWidget.addDecorators(CurrentDayDecorator(activity, mydate))

        calenderWidget.setOnDateChangedListener { widget, date, selected ->
            date
        }


        // From Date to YearMonth


    }


    lateinit var roundingFormat: DecimalFormat
    private fun initialize() {


        //Instantiate db
        instantiateDb()

        //setup ViewModel
        viewModel = ViewModelProviders.of(this).get(ViewModel()::class.java)

        //init format
        roundingFormat = DecimalFormat("#.##")
        roundingFormat.roundingMode = RoundingMode.CEILING

        //init views
        setTotalUserData()

        val startDate = Calendar.getInstance()
        startDate.add(Calendar.MONTH, -3)
        startDate.set(Calendar.DAY_OF_MONTH, 1)

//       //setup calender
        calenderWidget.state().edit()
                .setMinimumDate(CalendarDay.from(startDate[Calendar.YEAR], startDate[Calendar.MONTH], startDate[Calendar.DAY_OF_MONTH]))
                .setMaximumDate(CalendarDay.today())
//                .setMaximumDate(CalendarDay.from(2020, 6, 28))
                .commit();

        calenderWidget.currentDate = CalendarDay.today()

        barChart.xAxis.valueFormatter = LabelFormatter("Day")


        //setup bar chart
//        setBarChart(null)

    }

    private fun setBarChart(entries: List<BarEntry>?) {
        var entriesList = entries
        if (entriesList == null) {
            //setup bar chart
            entriesList = mutableListOf<BarEntry>().also {

                it.add(BarEntry(1f, 500f))
                it.add(BarEntry(2f, 1000f))
                it.add(BarEntry(3f, 600f))
                it.add(BarEntry(4f, 1400f))
                it.add(BarEntry(5f, 300f))
                it.add(BarEntry(6f, 500f))
                it.add(BarEntry(7f, 600f))
            }
        }
        val barDataSet = BarDataSet(entriesList, "KCAl data")
        val data = BarData(barDataSet)
        barChart.animateY(1000)
        barDataSet.setColors(ContextCompat.getColor(requireContext(), R.color.colorAccent))
        barChart.data = data

        barDataSet.valueFormatter = LabelFormatter("KCal")
        barDataSet.valueTextSize = 9f

        barChart.invalidate()

        //init bar chart view
        // data has AxisDependency.LEFT
        val left: YAxis = barChart.axisLeft
        left.setDrawLabels(false) // no axis labels

        left.setDrawAxisLine(false) // no axis line

        left.axisMinimum = 0f

        left.setDrawGridLines(false) // no grid lines

//        left.setDrawZeroLine(true) // draw a zero line

        val bottom: XAxis = barChart.xAxis
        bottom.setDrawGridLines(false)
        barChart.axisRight.isEnabled = false // no right axis
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.setTouchEnabled(false)

    }

    /**
     * instantiates the db variable
     */
    private fun instantiateDb() {
        db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "HasterDb.db")
                .build()
    }

    /**
     * this function sets the total workout, kcal and time data to reports
     */
    private fun setTotalUserData() {

        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        viewModel.getTotalsUserData(db)
        viewModel.totalUserData.observe(requireActivity(), androidx.lifecycle.Observer {
            var totalData = ArrayList(it)
            var totalExerciseNo = 0
            var totalCalories = 0.0
            var totalTime = 0.0
            var date: Date
            val calInstance = Calendar.getInstance()
            val entriesList = mutableListOf<BarEntry>().also { list ->

                list.add(BarEntry(1f, 0f))
                list.add(BarEntry(2f, 0f))
                list.add(BarEntry(3f, 0f))
                list.add(BarEntry(4f, 0f))
                list.add(BarEntry(5f, 0f))
                list.add(BarEntry(6f, 0f))
                list.add(BarEntry(7f, 0f))
            }

            for (entry in totalData) {
                date = entry.date
                calInstance.time = date
                totalExerciseNo += entry.noOfWorkout
                totalCalories += entry.calories
                totalTime += entry.timeWorkoutPerformed

                //set in calender if activity is performed or not
                if (entry.actPerformedOrNot == 1) {
                    //month + 1 -> as month starts from 0 in this function e.g. january = 0
                    val myDate = CalendarDay.from(calInstance[Calendar.YEAR], calInstance[Calendar.MONTH] + 1, calInstance[Calendar.DAY_OF_MONTH]) // year, month, date
                    calenderWidget.addDecorators(CurrentDayDecorator(activity, myDate))
                }
            }
            //set texts views
            time_min_textView.text = roundingFormat.format(totalTime)
            kcal_textView.text = roundingFormat.format(totalCalories)
            workout_no_textView.text = totalExerciseNo.toString()

            //go reverse till monday appears as we have to show this week's calorie count
            for (entry in totalData.reversed()) {
                date = entry.date
                calInstance.time = date
                calInstance.firstDayOfWeek = Calendar.MONDAY
                val day = calInstance[Calendar.DAY_OF_WEEK]

                if (day != Calendar.MONDAY) {
                    try {
                        entriesList[day - 2].y = roundingFormat.format(entry.calories).toFloat()
                    }catch (e:Exception){
                        entriesList[6].y = roundingFormat.format(entry.calories).toFloat()
                    }
                } else {
                    entriesList[day - 2].y = roundingFormat.format(entry.calories).toFloat()
                    break
                }
            }

            //set barChart
            barChart.clear()
            barChart.invalidate()
            setBarChart(entriesList)

        })
    }
}