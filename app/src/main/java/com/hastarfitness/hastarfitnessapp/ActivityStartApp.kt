package com.hastarfitness.hastarfitnessapp

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import com.hastarfitness.hastarfitnessapp.startingPages.ActivityStartPages
import com.hastarfitness.hastarfitnessapp.startingPages.AppStartLoadingScreen
import kotlinx.android.synthetic.main.activity_start_app.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

class ActivityStartApp : AppCompatActivity(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var database: DatabaseReference
    val RC_SIGN_IN = 140
    val TAG = "firesbase authetication"
    lateinit var session: Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        supportActionBar!!.hide

        setContentView(R.layout.activity_start_app)

        //manually set actionbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""

        initialize()
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Firebase Auth
        auth = Firebase.auth

        database = Firebase.database.reference

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if ((account != null || session.isChildLoggedIn!!) && !session.isUserLoggedOut) {
//            showToast(account!!.displayName.toString())
            setUserSignedIn()
        }

        sign_in_button.setOnClickListener(this);
        guestSignInBtn.setOnClickListener(this);
        continue_Btn.setOnClickListener(this);
        alreadyAcc_Btn.setOnClickListener(this);

    }

    fun initialize() {
        session = Session(this)
        sign_in_button.setSize(SignInButton.SIZE_WIDE);
    }

    private fun signIn() {
        setLoading()
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signInAnonymously() {
        setLoading()
        auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInAnonymously:success")
                        session.isChildLoggedIn = true
                        setUserSignedIn()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInAnonymously:failure", task.exception)
                        showToast(task.exception!!.message.toString())
                    }

                    // ...
                }
    }

    fun reSetLoading() {
        progress_circular.visibility = View.INVISIBLE
    }

    fun setLoading() {
        progress_circular.visibility = View.VISIBLE
    }

    private fun setUserSignedIn() {

//        FirebaseInstanceId.getInstance().instanceId
//                .addOnCompleteListener(OnCompleteListener { task ->
//                    if (!task.isSuccessful) {
//                        Log.w(TAG, "getInstanceId failed", task.exception)
//                        return@OnCompleteListener
//                    }
//
//                    // Get new Instance ID token
//                    val token = task.result?.token
//
//                    // Log and toast
//                    Log.d(TAG, token)
//                    Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
//                })

        session.isUserLoggedOut = false
        if (session.areStartPagesShown!!) {
            startActivity(Intent(this@ActivityStartApp, ActivityDashboard::class.java))
        } else if (!session.isChildLoggedIn) {
            val reference: DatabaseReference = database.child("users").child(auth.currentUser!!.uid)
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
//                    val user = dataSnapshot.getValue<User>()
                    val user: HashMap<String, String>? = try {
                        (dataSnapshot.value as HashMap<String, String>)
                    } catch (e: Exception) {
                        null
                    }
                    if (user != null) {
                        try {
                            session.dateOfBirth = user[AppConstants.DOB]
                            val birthYear = user[AppConstants.DOB]!!.split("-").last().toInt()
                            val calInstance = Calendar.getInstance()
                            val age = calInstance[Calendar.YEAR] - birthYear
                            session.age = age
                            session.gender = user[AppConstants.GENDER]
                            session.heightCm = user[AppConstants.HEIGHT_CM]!!.toDouble()
                            session.weightInKg = user[AppConstants.WEIGHT_KG]!!.toDouble()
                            session.goalWeight = user[AppConstants.GOAL_WEIGHT_KG]!!.toDouble()
                            session.weeklyActivity = user[AppConstants.WEEKLY_ACTIVITY]
                            session.areStartPagesShown = true
                            saveUserInformation()

                            session.day = calInstance.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
                            session.day = session.day!!.toLowerCase()
                            val day = session.day!!

                            session.todaysWorkoutType = AppConstants.dailyPlanBodyWeight[day]
                            reSetLoading()
                            startActivity(Intent(this@ActivityStartApp, AppStartLoadingScreen::class.java))

                        } catch (e: Exception) {
                            reSetLoading()
                            startActivity(Intent(this@ActivityStartApp, ActivityStartPages::class.java))

                        }
                    } else {
                        reSetLoading()
                        startActivity(Intent(this@ActivityStartApp, ActivityStartPages::class.java))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                    // ...
                }
            }

            reference.addListenerForSingleValueEvent(postListener)
        } else {
            reSetLoading()
            startActivity(Intent(this@ActivityStartApp, ActivityStartPages::class.java))
        }


//        if (session.areStartPagesShown!!) {
//            startActivity(Intent(this@ActivityStartApp, ActivityDashboard::class.java))
//        } else {
//            startActivity(Intent(this@ActivityStartApp, ActivityStartPages::class.java))
//        }

    }

    private fun saveUserInformation() {

        try {
            val user = auth.currentUser!!
            session.userName = user.displayName
            session.photoUrl = user.photoUrl.toString()
            session.userEmail = user.email.toString()
        } catch (e: Exception) {
            session.userName = "Child User"
            session.userEmail = ""
            val imageFilePath = "file:///android_asset/images/child.webp"
            session.photoUrl = imageFilePath
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                showToast(e.message.toString())
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {
                        setUserSignedIn()
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        showToast("${getString(R.string.loginSuccessful)} ${user!!.displayName}")
//                        saveUserInformation()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        // ...
                        showToast(task.exception!!.message.toString())
                    }

                    // ...
                }
    }
//    private fun saveUserInformation(){
//        val user = auth.currentUser!!
//        session.userName = user.displayName
//        session.photoUrl = user.photoUrl.toString()
//        session.userEmail = user.email.toString()
//    }

    private fun saveFile(localFile: File, fileName: String) {
        val contextWrapper = ContextWrapper(applicationContext)
        val directory: File = contextWrapper.getDir(filesDir.name, Context.MODE_PRIVATE)
        val file = File(directory, fileName);
        val fos = FileOutputStream(fileName, true); // save
        fos.write(localFile.readBytes());
        fos.close();
    }

    private fun showToast(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
//        if (currentUser != null) showToast("user already signed in")

    }

    override fun onClick(v: View) {

        when (v.id) {
            R.id.sign_in_button -> signIn()
            R.id.guestSignInBtn -> signInAnonymously()
            R.id.alreadyAcc_Btn -> {
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.title = getString(R.string.sign_in_page)

                calenderLayout.visibility = View.GONE
                logo_ImageView.visibility = View.VISIBLE

                sign_in_button.visibility = View.VISIBLE

                isLayoutSignIn = true
            }
            R.id.continue_Btn -> {
                isLayoutSignIn = true

                val curYear = Calendar.getInstance()[Calendar.YEAR]
                val birthYear = datePicker1.year
                val remYear = curYear - birthYear

                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.title = getString(R.string.sign_in_page)

                calenderLayout.visibility = View.GONE
                logo_ImageView.visibility = View.VISIBLE

                session.age = remYear
                session.dateOfBirth = "${datePicker1.dayOfMonth}-${datePicker1.month}-${datePicker1.year}"



                if (remYear <= 13) {
                    guestSignInBtn.visibility = View.VISIBLE
                } else {
                    sign_in_button.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun displaySignInLayout() {
        guestSignInBtn.visibility = View.GONE
        sign_in_button.visibility = View.GONE
        calenderLayout.visibility = View.VISIBLE
        logo_ImageView.visibility = View.GONE
        progress_circular.visibility = View.GONE
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.title = ""

    }


    var isLayoutSignIn = false
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                displaySignInLayout()
            }
        }
        return true
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {

        if (progress_circular.visibility == View.VISIBLE) {
            progress_circular.visibility = View.INVISIBLE
        } else {
            if (isLayoutSignIn) {
                displaySignInLayout()
            } else {
                if (doubleBackToExitPressedOnce) {
                    val intent = Intent(this@ActivityStartApp, ActivitySplashScreen::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra(AppConstants.EXIT, true)
                    startActivity(intent)
                }
                doubleBackToExitPressedOnce = true
                Toast.makeText(this, getString(R.string.app_close_message), Toast.LENGTH_SHORT).show()
                Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
            }
        }


    }
}