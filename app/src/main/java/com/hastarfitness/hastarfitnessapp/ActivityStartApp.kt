package com.hastarfitness.hastarfitnessapp

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.manageSharedPrefs.Session
import com.hastarfitness.hastarfitnessapp.startingPages.ActivityStartPages
import kotlinx.android.synthetic.main.activity_start_app.*
import java.io.File
import java.io.FileOutputStream

class ActivityStartApp : AppCompatActivity(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    val RC_SIGN_IN = 140
    val TAG = "firesbase authetication"
    lateinit var session: Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_start_app)

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


        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            showToast(account.displayName.toString())
            setUserSignedIn()
        }

        sign_in_button.setOnClickListener(this);
        guestSignInBtn.setOnClickListener(this);

    }

    fun initialize() {
        session = Session(this)
        sign_in_button.setSize(SignInButton.SIZE_WIDE);
    }

    private fun signIn() {
        progress_circular.visibility = View.VISIBLE
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun setUserSignedIn() {
        if (session.areStartPagesShown!!) {
            startActivity(Intent(this@ActivityStartApp, ActivityDashboard::class.java))
        } else {
            startActivity(Intent(this@ActivityStartApp, ActivityStartPages::class.java))
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
                    progress_circular.visibility = View.INVISIBLE
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
    private fun saveUserInformation(){
        val user = auth.currentUser!!
        session.userName = user.displayName
        session.photoUrl = user.photoUrl.toString()
        session.userEmail = user.email.toString()
    }

    private fun saveFile(localFile: File, fileName: String) {
        val contextWrapper = ContextWrapper(applicationContext)
        val directory: File = contextWrapper.getDir(filesDir.name, Context.MODE_PRIVATE)
        val file =  File(directory,fileName);
        val fos = FileOutputStream(fileName, true); // save
        fos.write(localFile.readBytes());
        fos.close();
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
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
            R.id.guestSignInBtn -> setUserSignedIn()
        }
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            val intent = Intent(this@ActivityStartApp, ActivitySplashScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra(AppConstants.EXIT, true)
            startActivity(intent)
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.app_close_message), Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}