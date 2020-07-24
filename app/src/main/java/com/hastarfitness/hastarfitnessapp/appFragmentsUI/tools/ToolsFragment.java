package com.hastarfitness.hastarfitnessapp.appFragmentsUI.tools;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hastarfitness.hastarfitnessapp.R;
import com.hastarfitness.hastarfitnessapp.fitnessCalculators.BMICalculator;
import com.hastarfitness.hastarfitnessapp.fitnessCalculators.BMRCalculator;
import com.hastarfitness.hastarfitnessapp.fitnessCalculators.BodyFatCalculator;
import com.hastarfitness.hastarfitnessapp.fitnessCalculators.MacroCalculator;

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tools, container, false);
        root.findViewById(R.id.bmiCalculator).setOnClickListener(v -> startActivity(new Intent(getContext(), BMICalculator.class)));
        root.findViewById(R.id.bmrCalculator).setOnClickListener(v -> startActivity(new Intent(getContext(), BMRCalculator.class)));
        root.findViewById(R.id.bodyFatCalculator).setOnClickListener(v -> startActivity(new Intent(getContext(), BodyFatCalculator.class)));
        root.findViewById(R.id.macroCalculator).setOnClickListener(v -> startActivity(new Intent(getContext(), MacroCalculator.class)));

        return root;
    }
}
