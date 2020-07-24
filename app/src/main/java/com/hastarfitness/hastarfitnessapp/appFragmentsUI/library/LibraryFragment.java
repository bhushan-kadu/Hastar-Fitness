package com.hastarfitness.hastarfitnessapp.appFragmentsUI.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hastarfitness.hastarfitnessapp.R;

public class LibraryFragment extends Fragment  {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
      return inflater.inflate(R.layout.fragment_library, container, false );
    }


}
