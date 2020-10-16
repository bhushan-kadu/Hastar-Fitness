package com.hastarfitness.hastarfitnessapp.yoga

import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.OnFullscreenListener
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.database.YogaExerciseDbModel
import com.hastarfitness.hastarfitnessapp.yoga.youtube.YoutubeConfig
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_show_video.*
import java.lang.Exception


class ShowYogaDescAndVideoActivity : YouTubeBaseActivity() {
    lateinit var yogaExercise: List<YogaExerciseDbModel>
    var exerciseUrlHash = ""
    var title = ""
    var desc = ""
    var position = 0
    private var isActivityInPauseState = true
    private var isFirstTime = true


    lateinit var ytPlayer: YouTubePlayer
    var fullScreen = false

    private lateinit var mOnInitializedListener: YouTubePlayer.OnInitializedListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_video)

        setActionBar(toolbar)
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        initialize()


        mOnInitializedListener = object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, p1: YouTubePlayer?, wasRestored: Boolean) {

                if (!wasRestored) {
                    ytPlayer = p1!!
                    ytPlayer.setOnFullscreenListener(OnFullscreenListener { _isFullScreen -> fullScreen = _isFullScreen })
                    ytPlayer.loadVideo(exerciseUrlHash)
                }

                ytPlayer.setPlaybackEventListener(object : YouTubePlayer.PlaybackEventListener {
                    override fun onSeekTo(p0: Int) {
                        resetLoading()
                    }

                    override fun onBuffering(p0: Boolean) {

                        if(isActivityInPauseState){
                            setLoading()
                        }
                    }

                    override fun onPlaying() {
                        isActivityInPauseState = false
                        togglePlayPauseDrawable()

                        if (isFirstTime) {
                            ytPlayer.pause()
                            ytPlayer.seekToMillis(1000)
                        }else{
                            resetLoading()
                        }

                    }

                    override fun onStopped() {

                    }

                    override fun onPaused() {
                        progress_circular.visibility = View.INVISIBLE
                        isActivityInPauseState = true
                        togglePlayPauseDrawable()

                        if(isFirstTime){
                            isFirstTime = false
                        }
                    }
                })
                play_button.setOnClickListener {
                    this@ShowYogaDescAndVideoActivity.playPauseBtnClick()
                }
                //set click listerns for back and next as video gets initialized
                next_button.setOnClickListener {

                    if (position != yogaExercise.size - 1) {
                        position++
                        setView()
                    } else {
                        Toast.makeText(applicationContext, "last item", Toast.LENGTH_LONG).show()
                    }


                    ytPlayer.loadVideo(exerciseUrlHash)
                }
                back_button.setOnClickListener {
                    if (position != 0) {
                        position--
                        setView()
                    } else {
                        Toast.makeText(applicationContext, "first item", Toast.LENGTH_LONG).show()
                    }


                    ytPlayer.loadVideo(exerciseUrlHash)
                }
            }

            override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
            }

        }
        youtube_player_view.initialize(YoutubeConfig().getApiKey(), mOnInitializedListener)



    }
    fun setLoading(){
        preview_imageView.visibility = View.VISIBLE
        progress_circular.visibility = View.VISIBLE
    }
    fun resetLoading(){
        preview_imageView.visibility = View.INVISIBLE
        progress_circular.visibility = View.INVISIBLE
    }

    fun initialize() {
        yogaExercise = intent.getSerializableExtra(AppConstants.YOGA_EXERCISES) as List<YogaExerciseDbModel>
        position = intent.getIntExtra("position", 0)

        setView()

    }

    fun playPauseBtnClick() {
        togglePlayPauseDrawable()
        if (isActivityInPauseState) {
            ytPlayer.play()
        } else {
            ytPlayer.pause()
        }

        //toggle state
        isActivityInPauseState = !isActivityInPauseState
    }


    private fun togglePlayPauseDrawable() {

        val playDrawable = R.drawable.ic_play_round
        val pauseDrawable = R.drawable.ic_pause_round
        if (isActivityInPauseState) {
            play_button.setImageResource(playDrawable)
        } else {
            play_button.setImageResource(pauseDrawable)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> showExitDialog();
        }
        return true
    }

    private fun showExitDialog() {
        val dlg = AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle("Quit Workout")
                .setMessage("Are you sure you want to quit current session?")
                .setPositiveButton("QUIT", DialogInterface.OnClickListener { dialog, which ->
                    super.onBackPressed()
                })
                .setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                }).create()
        //change buttons color in on show listener
        dlg.setOnShowListener {
            val colorNegativeBtn = ContextCompat.getColor(this, R.color.yellow)
            val colorPositiveBtn = ContextCompat.getColor(this, R.color.color_gray_66)
            dlg.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(colorNegativeBtn);
            dlg.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorPositiveBtn);
        }

        //show dialogue
        dlg.show()
    }

    private fun setView() {
        title = yogaExercise[position].name
        desc = yogaExercise[position].desc
        exerciseUrlHash = try {
            yogaExercise[position].img.split("?")[1].split("&")[0].split("=")[1]
        }catch (e: Exception){
            yogaExercise[position].img.split("/").last()
        }
        val thumbnailUrl = "https://img.youtube.com/vi/${exerciseUrlHash}/0.jpg"
        Picasso.get()
                .load(thumbnailUrl)
                .into(preview_imageView)

        actionBar!!.title = yogaExercise[position].intensity
        title_text.text = title
        desc_text.text = desc
    }

    override fun onBackPressed() {
        if (fullScreen) {
            ytPlayer.setFullscreen(false);
        } else {
            showExitDialog()
        }
    }
}
