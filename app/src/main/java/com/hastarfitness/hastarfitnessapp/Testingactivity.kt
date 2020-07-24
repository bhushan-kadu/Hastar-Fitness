package com.hastarfitness.hastarfitnessapp

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils


class Testingactivity : AppCompatActivity() {

    private lateinit var mChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testingactivity)

        mChart = findViewById(R.id.lineChart);
        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);


        renderData()

    }

    private fun renderData() {
        val llXAxis = LimitLine(10f, "Index 10")
        llXAxis.lineWidth = 4f
        llXAxis.enableDashedLine(10f, 10f, 0f)
        llXAxis.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
        llXAxis.textSize = 10f
        val xAxis = mChart.xAxis
        xAxis.enableGridDashedLine(10f, 10f, 0f)
        xAxis.axisMaximum = 10f
        xAxis.axisMinimum = 0f
        xAxis.setDrawLimitLinesBehindData(true)
        val ll1 = LimitLine(215f, "Maximum Limit")
        ll1.lineWidth = 4f
        ll1.enableDashedLine(10f, 10f, 0f)
        ll1.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
        ll1.textSize = 10f
        val ll2 = LimitLine(70f, "Minimum Limit")
        ll2.lineWidth = 4f
        ll2.enableDashedLine(10f, 10f, 0f)
        ll2.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
        ll2.textSize = 10f
        val leftAxis = mChart.axisLeft
        leftAxis.removeAllLimitLines()
        leftAxis.addLimitLine(ll1)
        leftAxis.addLimitLine(ll2)
        leftAxis.axisMaximum = 350f
        leftAxis.axisMinimum = 0f
        leftAxis.enableGridDashedLine(10f, 10f, 0f)
        leftAxis.setDrawZeroLine(false)
        leftAxis.setDrawLimitLinesBehindData(false)
        mChart.axisRight.isEnabled = false
        setData()
    }

    private fun setData() {
        val values: ArrayList<Entry> = ArrayList()
        values.add(Entry(1f, 50f))
        values.add(Entry(2f, 100f))
        values.add(Entry(3f, 80f))
        values.add(Entry(4f, 120f))
        values.add(Entry(5f, 110f))
        values.add(Entry(7f, 150f))
        values.add(Entry(8f, 250f))
        values.add(Entry(9f, 190f))
        val set1: LineDataSet
        if (mChart.data != null &&
                mChart.data.dataSetCount > 0) {
            set1 = mChart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            mChart.data.notifyDataChanged()
            mChart.notifyDataSetChanged()
        } else {
            set1 = LineDataSet(values, "Sample Data")
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
            if (Utils.getSDKInt() >= 18) {
                val drawable = ContextCompat.getDrawable(applicationContext, R.drawable.blue_gradient_background)
                set1.fillDrawable = drawable
            } else {
                set1.fillColor = Color.DKGRAY
            }
            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(set1)
            val data = LineData(dataSets)
            mChart.data = data
        }
    }
}
