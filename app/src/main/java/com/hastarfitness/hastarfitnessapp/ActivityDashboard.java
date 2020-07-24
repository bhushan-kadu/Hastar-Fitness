package com.hastarfitness.hastarfitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants;
import com.hastarfitness.hastarfitnessapp.database.AppDatabase;
import com.hastarfitness.hastarfitnessapp.exerciseListForCardioAndBodyWeight.ActivityExerciseList;
import com.hastarfitness.hastarfitnessapp.exerciseListForCardioAndBodyWeight.ShowBodyWeightTypesActivity;
import com.hastarfitness.hastarfitnessapp.meditationNew.ShowMeditationTypesActivity;
import com.hastarfitness.hastarfitnessapp.profile.UserProfileActivity;
import com.hastarfitness.hastarfitnessapp.yoga.ShowYogaTypesActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import static android.util.Log.v;

/**
 * Main Activity where all the App Fragments are to be shown
 */
public class ActivityDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    NavigationView navView;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        int switcherId = getIntent().getIntExtra(AppConstants.SWITCH_FRAGMENT, R.id.navigation_home);


        //Prepopulate database from db file
        instantiateDb();

        //find the required components

        BottomNavigationView navViewBottom = findViewById(R.id.nav_view);
        navView = findViewById(R.id.nav_view_left);
        drawer = (DrawerLayout) findViewById(R.id.container);
        Toolbar toolbar = findViewById(R.id.toolbar);


        //manually set actionbar
        setSupportActionBar(toolbar);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_library, R.id.navigation_tools, R.id.navigation_diet)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        //setup bottom and left navigation view to the navigationUI
        NavigationUI.setupWithNavController(navViewBottom, navController);
        NavigationUI.setupWithNavController(navView, navController);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_close_message, R.string.app_close_message);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);

        navViewBottom.setSelectedItemId(switcherId);

    }

    private void instantiateDb() {
        ViewModel viewModel = ViewModelProviders.of(this).get(ViewModel.class);

        AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, "HasterDb.db")
                .build();
        try {
            viewModel.getAll(db);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private Boolean doubleBackToExitPressedOnce = false;

    private void exitApp() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(this, ActivitySplashScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(AppConstants.EXIT, true);
            startActivity(intent);
            return;
        }
        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.app_close_message), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onClick(View v) {
        String tag = v.getTag().toString();

        Intent i;
        switch (tag) {
            case AppConstants.CARDIO:
                i = new Intent(getApplicationContext(), ActivityExerciseList.class);
                i.putExtra(AppConstants.WORKOUT_TYPE, v.getTag().toString());
                startActivity(i);
                break;
            case AppConstants.BODY_WEIGHT:
                i = new Intent(getApplicationContext(), ShowBodyWeightTypesActivity.class);
                startActivity(i);
                break;
            case AppConstants.YOGA:
                i = new Intent(getApplicationContext(), ShowYogaTypesActivity.class);
                startActivity(i);
                break;
//            case AppConstants.MEDITATION:
//                i = new Intent(getApplicationContext(), ShowMeditationTypes.class);
//                startActivity(i);
//                break;
            case AppConstants.MEDITATION:
                i = new Intent(getApplicationContext(), ShowMeditationTypesActivity.class);
                startActivity(i);
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_settings:
                startActivity(new Intent(getApplicationContext(), AppSettingsActivity.class));
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.report:
                startActivity(new Intent(ActivityDashboard.this, UserProfileActivity.class));
                return true;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (this.drawer.isDrawerOpen(GravityCompat.START)) {
            this.drawer.closeDrawer(GravityCompat.START);
        } else {
            exitApp();
        }
    }
}
