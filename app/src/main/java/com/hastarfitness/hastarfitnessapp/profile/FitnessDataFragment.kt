package com.hastarfitness.hastarfitnessapp.profile

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.room.Room
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.ViewModel
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.fitnessCalculators.FitnessCalculators
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import com.hastarfitness.hastarfitnessapp.profile.customDialogTakeBodyData.DlgGiveBodyData
import com.hastarfitness.hastarfitnessapp.profile.customDialogTakeWeight.DlgGiveWeight
import kotlinx.android.synthetic.main.fragment_fitness_data.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class FitnessDataFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fitness_data, container, false)
    }

    private lateinit var mChart: LineChart
    private var chartValues = ArrayList<Entry>()
    private lateinit var wtDlg: DlgGiveWeight
    private lateinit var bodyDataDlg: DlgGiveBodyData
    private lateinit var viewModel: ViewModel
    lateinit var db: AppDatabase
    lateinit var session: Session
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //find chart from fragment view
        mChart = view.findViewById(R.id.lineChart);

        //init fragment class components
        initialize()

        //sets value to chart when dialog is dismissed
        wtDlg.setOnDismissListener { dialog ->
            val mDialog = (dialog as DlgGiveWeight)
            val isValueChanged = mDialog.isValueChanged
            //if value is changed i.e. save button clicked
            if (isValueChanged) {
                mChart.invalidate()
                mChart.clear()
                val entry = mDialog.entry!!
                val date = mDialog.date!!
                insertWeightInTableWithDate(entry.y.toDouble(), date, entry)


                //reset the value for future
                mDialog.isValueChanged = false
            }
        }

        //sets visibility of temp view, calculates values and sets text of BMI and Body Fat TV
        bodyDataDlg.setOnDismissListener { dialog ->
            val mDialog = (dialog as DlgGiveBodyData)
            val isSaveClicked = mDialog.isSaveClicked
            if (isSaveClicked) {
                //set temp view visibility
                setTempCardViewVisibilityAndShowCardData()

                //reset for future use
                mDialog.isSaveClicked = false
            }
        }

        //set button click listeners
        update_wt_btn.setOnClickListener {
            wtDlg.show()
        }
        addData_btn.setOnClickListener {
            bodyDataDlg.show()
        }
        addData_ImageBtn.setOnClickListener {
            bodyDataDlg.show()
        }

        //set temp view visibility
        setTempCardViewVisibilityAndShowCardData()
    }

    private fun setTempCardViewVisibilityAndShowCardData() {
        //toggle temporary view on basis of value is available in pref or not
        if (isUserBodyDataAvailableOrNot()) {
            temporaryCardView.visibility = View.GONE
            val fitCalc = FitnessCalculators()
            val bodyFat = if(session.gender == AppConstants.MALE) {
                fitCalc.bodyFatMen(session.heightCm!!, session.weistInches!!, session.neakInches!!)
            }else{
                fitCalc.bodyFatWomen(session.heightCm!!, session.weistInches!!,session.hipInches!!, session.neakInches!!)
            }
            val bmi = fitCalc.calculateBMIMetric(session.heightCm!!, session.weightInKg!!)

            val weight = session.weightInKg

            weight_textView.text = roundingFormat.format(weight)
            bmi_textView.text = roundingFormat.format(bmi)
            bodyFat_textView.text = roundingFormat.format(bodyFat)

        }
    }

    private fun isUserBodyDataAvailableOrNot(): Boolean {

        return when (session.gender) {
            AppConstants.MALE -> {
                session.weightInKg != null &&
                        !session.neakInches!!.equals(-1.0) &&
                        !session.weistInches!!.equals(-1.0) &&
                        session.age != -1 &&
                        !session.heightCm!!.equals(-1.0)
            }
            AppConstants.FEMALE -> {
                session.weightInKg != null &&
                        !session.neakInches!!.equals(-1.0) &&
                        !session.hipInches!!.equals(-1.0) &&
                        !session.weistInches!!.equals(-1.0) &&
                        session.age != -1 &&
                        !session.heightCm!!.equals(-1.0)
            }
            else -> {
                false
            }
        }
    }

    private fun insertWeightInTableWithDate(weight: Double, date: Date, entry: Entry) {
        viewModel.insertWeightWithDate(db, weight, date)
        viewModel.insertedRowLong.observe(requireActivity(), Observer {
            it
            drawChartFromDb(entry)
        })

    }

    private fun drawChartFromDb(entry: Entry?) {
        viewModel.getAllWeights(db)
        val calInstance = Calendar.getInstance()
        val entriesList = arrayListOf<Entry>()
        viewModel.weightInfoDbModel.observe(requireActivity(), Observer {

            for (entries in it) {
                calInstance.time = entries.date
                val day = calInstance[Calendar.DAY_OF_YEAR]
                val weight = entries.weight
                entriesList.add(Entry(day.toFloat(), weight.toFloat()))
            }
            setData(entriesList, entry)
        })
    }

    private lateinit var roundingFormat: DecimalFormat

    private fun initialize() {

        //init session
        session = Session(requireActivity())

        //Instantiate db
        instantiateDb()

        //setup ViewModel
        viewModel = ViewModelProviders.of(this).get(ViewModel()::class.java)

        //init dialogue
        wtDlg = DlgGiveWeight(this@FitnessDataFragment)
        wtDlg.create()

        //init body data dialog
        bodyDataDlg = DlgGiveBodyData(this@FitnessDataFragment)
        bodyDataDlg.create()

        //init chart
        val bottom: XAxis = mChart.xAxis
        bottom.setDrawGridLines(false)
        bottom.valueFormatter = StickyDateAxisValueFormatter(mChart, sticky_label)
        bottom.setDrawGridLines(true);
        mChart.description.isEnabled = false
        mChart.legend.isEnabled = false
        mChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        mChart.xAxis.granularity = 1f
        mChart.setPinchZoom(true)
        mChart.isDoubleTapToZoomEnabled = true

        val xAxis = mChart.xAxis
        xAxis.enableGridDashedLine(10f, 10f, 0f)
        xAxis.axisMaximum = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR).toFloat()
        xAxis.axisMinimum = 1f
        xAxis.setDrawLimitLinesBehindData(true)
        mChart.isScaleXEnabled = true
        mChart.isScaleYEnabled = false

        val leftAxis = mChart.axisLeft
        leftAxis.removeAllLimitLines()
        leftAxis.axisMaximum = 100f
        leftAxis.axisMinimum = 20f
        leftAxis.enableGridDashedLine(10f, 10f, 0f)
        leftAxis.setDrawZeroLine(false)
        leftAxis.setDrawLimitLinesBehindData(false)
        mChart.axisRight.isEnabled = false
//        mChart.setVisibleXRangeMaximum(10f)


        //prepare chart for first time
        drawChartFromDb(null)
        renderAdded(chartValues)

        //init format
        roundingFormat = DecimalFormat("#.##")
        roundingFormat.roundingMode = RoundingMode.CEILING

    }

    /**
     * instantiates the db variable
     */
    private fun instantiateDb() {
        db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "HasterDb.db")
                .build()
    }


    //render the chart and if entry is added add it to chart
    private fun setData(entryList: ArrayList<Entry>?, entryLatest: Entry?) {
        if (entryList != null && entryList.size != 0) {
            chartValues.addAll(entryList)
            chartValues.sortBy { it.x }
            renderAdded(chartValues)
            val maxEntry = chartValues.maxBy { it.y }//highest weight
            val minEntry = chartValues.minBy { it.y }//lowest weight
            mChart.axisLeft.axisMaximum = maxEntry!!.y + 5//set axis maximum for y axis to high_wt + 5
            mChart.axisLeft.axisMinimum = minEntry!!.y - 5//set axis minimum for y axis to low_wt - 5
            val lastEntry = chartValues[chartValues.size - 1]//last modified weight

            if (entryLatest != null) {
                //if user entered any entry then zoom to that entry
                mChart.zoomAndCenterAnimated(10f, 0f, entryLatest.x, 0f, mChart.axisRight.axisDependency, 500)
                mChart.moveViewToAnimated(entryLatest.x - 15, 0f, mChart.axisRight.axisDependency, 500)
            } else {
                //else zoom to last entry in chart
                mChart.zoomAndCenterAnimated(10f, 0f, lastEntry.x, 0f, mChart.axisRight.axisDependency, 500)
                mChart.moveViewToAnimated(lastEntry.x - 15, 0f, mChart.axisRight.axisDependency, 500)
            }

            //set weight text
            setCurrentWeight(lastEntry.y)

            //calculate average weight
            var sum = 0.0
            for(entry in chartValues){
                sum += entry.y
            }
            val averageWtValue = sum / chartValues.size

            //set lowest and highest weights
            setLowHighAverageWeight(maxEntry.y, minEntry.y, averageWtValue)
        }
    }

    private fun setCurrentWeight(weight: Float) {
        weight_textView.text = roundingFormat.format(weight) + " Kg"
    }
    private fun setLowHighAverageWeight(weightLow: Float, weightHigh: Float, weightAverage: Double) {
        highest_weight_textView.text = roundingFormat.format(weightHigh) + "Kg"
        lowest_weight_textView.text = roundingFormat.format(weightLow) + "Kg"
        average_weight_textView.text = roundingFormat.format(weightAverage) + "Kg"
    }

    //render data to screen for given chart values
    private fun renderAdded(chartValues: ArrayList<Entry>) {
        val set1 = LineDataSet(chartValues, "Sample Data")
        set1.setDrawIcons(false)
        set1.enableDashedLine(10f, 5f, 0f)
        set1.enableDashedHighlightLine(10f, 5f, 0f)
        set1.color = Color.DKGRAY
        set1.setCircleColor(Color.DKGRAY)
        set1.lineWidth = 1f
        set1.circleRadius = 3f
        set1.setDrawCircleHole(false)
        set1.valueTextSize = 9f
        set1.setDrawFilled(true)
        set1.formLineWidth = 1f
        set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
        set1.formSize = 15f
        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(set1)
        val data = LineData(dataSets)
        mChart.data = data
        mChart.notifyDataSetChanged()
        mChart.invalidate()
    }
}