package com.hastarfitness.hastarfitnessapp.customDialogueToShowExerciseInfo

import android.app.Dialog
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.database.ExerciseDbModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dlg_custom_dialogue.*
import kotlinx.android.synthetic.main.dlg_custom_dialogue.exercise_video
import kotlinx.android.synthetic.main.dlg_custom_dialogue.exercise_video_bitmap
import java.io.File
import kotlin.properties.Delegates

/**
 * Custom Dialogue for showing exercise desc to user
 */
class DlgShowExerciseInfo(private val ctx: Context, var exerciseList: List<ExerciseDbModel>) : Dialog(ctx), View.OnClickListener {

    var pos by Delegates.notNull<Int>()
    override fun create() {
        super.create()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dlg_custom_dialogue)

        //init class
        initialize()
    }

    //initialize class
    private fun initialize(){
        btn_prev.setOnClickListener(this)
        btn_next.setOnClickListener(this)

        //load gif
        videoUri = ""


        if(this.isShowing){
            val v = Firebase.storage
            val refrance = v.reference
            downloadAndSaveVideos(refrance, exerciseList[pos].name+".mp4")
        }


        exercise_video.setOnErrorListener { mp, what, extra ->
            true
        }
    }
    var videoUri =""
    private fun playVideo(){

        val uri: Uri = Uri.parse(videoUri)

        exercise_video.setVideoURI(uri)

        exercise_video.start()

        exercise_video.setOnPreparedListener { mp ->
            mp.isLooping = true

            mp.setOnInfoListener { mp, what, extra ->

                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    // video started; hide the placeholder.
                    exercise_video_bitmap.visibility = View.GONE;
                    return@setOnInfoListener true
                }
                return@setOnInfoListener false
            }

        }


    }
    private fun downloadAndSaveVideos(storageRef: StorageReference, fileName: String) {
        videoUri = context.filesDir.toString() + "/" + fileName
        if(fileExist(fileName)){
            playVideo()
        }else{
            val islandRef = storageRef.child(fileName)
            val filePrefix = fileName.split(".")[0]
            val fileSuffix = fileName.split(".")[1]
            val localFile = File.createTempFile(filePrefix, fileSuffix)

            val downlandRef = islandRef.getFile(localFile).addOnSuccessListener {
                // Local temp file has been created
//            Toast.makeText(ctx, "success", Toast.LENGTH_LONG).show()
                saveFile(localFile, fileName)
                localFile.delete()
                playVideo()
            }.addOnFailureListener {
                // Handle any errors
                Toast.makeText(ctx, it.message, Toast.LENGTH_LONG).show()
            }
            downlandRef.addOnCompleteListener() {
//            progress_horizontal.progress += 1f
            }
        }

    }
    private fun saveFile(localFile: File, fileName: String) {
        val fOut = ctx.openFileOutput(fileName, Context.MODE_PRIVATE); // save
        fOut.write(localFile.readBytes());
        fOut.close();
    }
    private fun fileExist(fName: String?): Boolean {
        val file = ctx.getFileStreamPath(fName)
        return file.exists()
    }

fun loadThumbnail(name:String){
    exercise_video_bitmap.visibility = View.VISIBLE;
    val imageFilePath = "file:///android_asset/exerciseThumbnails/$name.webp"
    // Load the image into image view from assets folder
    Picasso.get()
            .load(imageFilePath)
            .placeholder(ContextCompat.getDrawable(context, android.R.color.white)!!)
            .into(exercise_video_bitmap)

}
    //implement clicks
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_prev -> {
                if (pos != 0) {
                    --pos
                    val curItem = exerciseList[pos]
                    setMessage(curItem.desc, curItem.name)
                    loadThumbnail(curItem.name)
                    downloadVide(curItem.name)
                    videoUri = context.filesDir.toString() + "/" + curItem.name + ".mp4"
//                    loadVideo()
                }
            }
            R.id.btn_next -> {
                if (exerciseList.size - 1 != pos) {
                    ++pos
                    val curItem = exerciseList[pos]
                    setMessage(curItem.desc, curItem.name)
                    loadThumbnail(curItem.name)
                    downloadVide(curItem.name)
                    videoUri = context.filesDir.toString() + "/" + curItem.name + ".mp4"
//                    loadVideo()
                }
            }
        }
    }

    fun downloadVide(name:String){
        val v = Firebase.storage
        val refrance = v.reference
        downloadAndSaveVideos(refrance, name + ".mp4")
    }

    //set description message of exercise
    fun setMessage(desc: String, name:String) {
        txt_dia.text = desc
        name_textView.text = name
    }
}

