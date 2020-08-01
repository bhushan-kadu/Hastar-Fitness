package com.hastarfitness.hastarfitnessapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.room.Room
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.database.AppDatabase
import com.hastarfitness.hastarfitnessapp.exerciseListForCardioAndBodyWeight.ActivityExerciseList
import com.hastarfitness.hastarfitnessapp.exerciseListForCardioAndBodyWeight.ShowBodyWeightTypesActivity
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import com.hastarfitness.hastarfitnessapp.meditationNew.ShowMeditationTypesActivity
import com.hastarfitness.hastarfitnessapp.profile.UserProfileActivity
import com.hastarfitness.hastarfitnessapp.settings.AppSettingsActivity
import com.hastarfitness.hastarfitnessapp.yoga.ShowYogaTypesActivity

/**
 * Main Activity where all the App Fragments are to be shown
 *
 * @author Bhushan Kadu
 */
class ActivityDashboard : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    lateinit var navView: NavigationView
    lateinit var drawer: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        val switcherId = intent.getIntExtra(AppConstants.SWITCH_FRAGMENT, R.id.navigation_home)


        //Prepopulate database from db file
        instantiateDb()

        //find the required components
        val navViewBottom = findViewById<BottomNavigationView>(R.id.nav_view)
        navView = findViewById(R.id.nav_view_left)
        drawer = findViewById<View>(R.id.container) as DrawerLayout
        val toolbar = findViewById<Toolbar>(R.id.toolbar)


        //manually set actionbar
        setSupportActionBar(toolbar)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_library, R.id.navigation_tools, R.id.navigation_diet)
                .setDrawerLayout(drawer)
                .build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        //setup bottom and left navigation view to the navigationUI
        NavigationUI.setupWithNavController(navViewBottom, navController)
        NavigationUI.setupWithNavController(navView, navController)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_close_message, R.string.app_close_message)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
        navViewBottom.selectedItemId = switcherId
        val logoutBtn = findViewById<LinearLayout>(R.id.logout)
        logoutBtn.setOnClickListener(this)
    }

    private fun instantiateDb() {
        val viewModel = ViewModelProviders.of(this).get(ViewModel::class.java)
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "HasterDb.db")
                .build()
        try {
            viewModel.getAll(db)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var doubleBackToExitPressedOnce = false
    private fun exitApp() {
        if (doubleBackToExitPressedOnce) {
            val intent = Intent(this, ActivitySplashScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra(AppConstants.EXIT, true)
            startActivity(intent)
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.app_close_message), Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.logout) {
            showLogoutAlert()
        } else {
            val tag = v.tag.toString()
            val i: Intent
            when (tag) {
                AppConstants.CARDIO -> {
                    i = Intent(applicationContext, ActivityExerciseList::class.java)
                    i.putExtra(AppConstants.WORKOUT_TYPE, v.tag.toString())
                    startActivity(i)
                }
                AppConstants.BODY_WEIGHT -> {
                    i = Intent(applicationContext, ShowBodyWeightTypesActivity::class.java)
                    startActivity(i)
                }
                AppConstants.YOGA -> {
                    i = Intent(applicationContext, ShowYogaTypesActivity::class.java)
                    startActivity(i)
                }
                AppConstants.MEDITATION -> {
                    i = Intent(applicationContext, ShowMeditationTypesActivity::class.java)
                    startActivity(i)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_settings -> startActivity(Intent(applicationContext, AppSettingsActivity::class.java))
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.report -> {
                startActivity(Intent(this@ActivityDashboard, UserProfileActivity::class.java))
                return true
            }
            R.id.share ->{
                shareApp()
            }
            R.id.rate ->{
                rateApp()
            }
        }
        return true
    }

    private fun logoutUser() {
        val session = Session(this)
        Firebase.auth.signOut()
        Firebase.auth.addAuthStateListener {
            if(it.currentUser == null){
                session.isUserLoggedOut = true
                session.areStartPagesShown = false
                session.isChildLoggedIn = false
                val i = Intent(applicationContext, ActivityStartApp::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(i)
            }
        }
    }

    private fun shareApp() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out a awesome fitness app that I use daily *Hastar Fitness* :\n https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }
    private fun rateApp(){
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
    }

    private fun showLogoutAlert() {
        val dlg = AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle("Logout")
                .setMessage("All Your Progress will get discarded, Do you really want to Logout?")
                .setPositiveButton("LOGOUT") { dialog, which -> logoutUser() }
                .setNegativeButton("CANCEL") { dialog, which -> dialog.dismiss() }.create()
        //change buttons color in on show listener
        dlg.setOnShowListener {
            val colorNegativeBtn = ContextCompat.getColor(applicationContext, R.color.yellow)
            val colorPositiveBtn = ContextCompat.getColor(applicationContext, R.color.color_gray_66)
            dlg.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(colorNegativeBtn)
            dlg.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorPositiveBtn)
        }

        //show dialogue
        dlg.show()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            exitApp()
        }
    }
}