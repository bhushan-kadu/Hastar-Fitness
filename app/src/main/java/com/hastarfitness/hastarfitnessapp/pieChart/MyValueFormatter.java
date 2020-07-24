package com.hastarfitness.hastarfitnessapp.pieChart;

import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

/**
 * custom value formatter for pie chart values on any slice
 */
public class MyValueFormatter extends ValueFormatter {

    public MyValueFormatter() {
        DecimalFormat mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
    }

    @Override
    public String getPieLabel(float value, PieEntry pieEntry) {

        return value + " gm";
    }
}