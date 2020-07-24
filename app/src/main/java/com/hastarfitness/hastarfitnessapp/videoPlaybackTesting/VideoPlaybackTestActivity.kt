package com.hastarfitness.hastarfitnessapp.videoPlaybackTesting

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.hastarfitness.hastarfitnessapp.R
import kotlinx.android.synthetic.main.activity_video_playback_test.*
import java.io.File


class VideoPlaybackTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_playback_test)

        val storage = Firebase.storage
        // Create a storage reference from our app
        val storageRef = storage.reference

        val fileNames = mutableListOf("push ups.mp4", "pushups.mp4")
        var remFiles = mutableListOf<String>()

            remFiles = checkWhichFilesExistsAndReturnWhichDoNot(fileNames)
            progress_horizontal.max = remFiles.size.toFloat()
            downloadMultipleFiles(storageRef, remFiles)

        progress_horizontal.setOnProgressChangedListener { view, progress, isPrimaryProgress, isSecondaryProgress ->
            if(progress == remFiles.size.toFloat()){
                playVideo(fileNames.last())
            }
        }

        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
        }
    }

    private fun downloadMultipleFiles(storageRef: StorageReference, fileNames: List<String>){
        for(fileName in fileNames){
            downloadAndSaveVideos(storageRef, fileName)
        }
    }

    private fun downloadAndSaveVideos(storageRef:StorageReference, fileName:String){
        val islandRef = storageRef.child(fileName)
        val filePrefix =fileName.split(".")[0]
        val fileSuffix =fileName.split(".")[1]
        val localFile = File.createTempFile(filePrefix, fileSuffix)

        val downlandRef = islandRef.getFile(localFile).addOnSuccessListener {
            // Local temp file has been created
            Toast.makeText(this, "success", Toast.LENGTH_LONG).show()
            saveFile(localFile, fileName)
            localFile.delete()
        }.addOnFailureListener {
            // Handle any errors
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
        }
        downlandRef.addOnCompleteListener() {
            progress_horizontal.progress+=1f
        }
    }

    private fun checkWhichFilesExistsAndReturnWhichDoNot(fileNames:List<String>):MutableList<String>{
        val fileNotExistList = mutableListOf<String>()
        for(file in fileNames){
            if(!fileExist(file)){
                fileNotExistList.add(file)
            }
        }
        return fileNotExistList
    }

    private fun saveFile(localFile: File, fileName: String) {
        val fOut = openFileOutput(fileName, Context.MODE_PRIVATE); // save
        fOut.write(localFile.readBytes());
        fOut.close();
    }

    private fun fileExist(fName: String?): Boolean {
        val file = baseContext.getFileStreamPath(fName)
        return file.exists()
    }

    private fun playVideo(fileName: String){
        val videoUri = applicationContext.filesDir.toString() + "/" + fileName
        val uri: Uri = Uri.parse(videoUri)

        videoView.setVideoURI(uri)

        videoView.start()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
