package com.hastarfitness.hastarfitnessapp

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileOutputStream


class DownloadVideos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_videos)

        val storage = Firebase.storage
        // Create a storage reference from our app
        val storageRef = storage.reference

        val islandRef = storageRef.child("PUSHUP02.mp4")

        val localFile = File.createTempFile("pushups", "mp4")

        val downlaodRef = islandRef.getFile(localFile).addOnSuccessListener {
            // Local temp file has been created
            Toast.makeText(this, "success", Toast.LENGTH_LONG).show()
            saveFile(localFile, "pushups.mp4")
        }.addOnFailureListener {
            // Handle any errors
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()

        }


        val body = localFile.readBytes()
        body.toString()
        downlaodRef.removeOnProgressListener {
            it.totalByteCount
        }
    }

    private fun saveFile(localFile: File, fileName: String) {
        val contextWrapper = ContextWrapper(applicationContext)
        val directory: File = contextWrapper.getDir(filesDir.name, Context.MODE_PRIVATE)
        val file =  File(directory,fileName);
        val fos = FileOutputStream(fileName, true); // save
        fos.write(localFile.readBytes());
        fos.close();
    }

    fun getFile(){
        val yourFilePath: String = applicationContext.filesDir.toString() + "/" + "images.mp4"
        val yourFile = File(yourFilePath)
    }
}