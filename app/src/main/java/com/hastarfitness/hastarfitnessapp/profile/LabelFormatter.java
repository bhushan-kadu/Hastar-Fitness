package com.hastarfitness.hastarfitnessapp.profile;


import com.github.mikephil.charting.formatter.ValueFormatter;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class LabelFormatter extends ValueFormatter {
 String name;
 List<String> days = Arrays.asList( "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
    public LabelFormatter(@NotNull String s) {
        name = s;
    }


    @Override
    public String getFormattedValue(float value) {
        if (name.equals("Day")){
            return days.get((int)value - 1 ) ;
        }else if(name.equals("KCal")){
            return  value + " KCal";
        }
      return value+"";
    }
}