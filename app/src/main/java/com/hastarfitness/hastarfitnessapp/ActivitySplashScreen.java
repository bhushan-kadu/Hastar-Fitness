package com.hastarfitness.hastarfitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants;

import androidx.appcompat.app.AppCompatActivity;


public class ActivitySplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();//hides the current activity action bar
        setContentView(R.layout.activity_splash_screen);

        //back press event from MainActivity should close the application
        if (getIntent().getBooleanExtra(AppConstants.EXIT, false))
        {
            finish();//inbuilt method to perform back pressed operation

        }else{
            //Its a delayed task to show app for 2000 time and then intent to mainActivity
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(ActivitySplashScreen.this, ActivityStartApp.class));
                }
            },500);
        }



    }
}
