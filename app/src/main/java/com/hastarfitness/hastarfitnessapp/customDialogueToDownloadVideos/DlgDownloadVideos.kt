package com.hastarfitness.hastarfitnessapp.customDialogueToDownloadVideos

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.hastarfitness.hastarfitnessapp.R
import kotlinx.android.synthetic.main.dlg_download_videos.*
import kotlinx.android.synthetic.main.dlg_download_videos.progress_horizontal
import java.io.File

/**
 * custom dialogue class for getting custom plan info to save the plan from user
 *
 * @author Bhushan Kadu
 */
class DlgDownloadVideos(val ctx: Context, var exerciseNamesList: MutableList<String>) : Dialog(ctx) {

    private lateinit var cancelBtn: Button
    private lateinit var downloadBtn: Button
    var isDownloaded = false

    /**
     * function to create the dialogue
     */
    override fun create() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_download_videos);

        cancelBtn = cancel_action
        downloadBtn = download_button

        setCanceledOnTouchOutside(false)
        setCancelable(false)

        val storage = Firebase.storage
        // Create a storage reference from our app
        val storageRef = storage.reference

        val fileNames = exerciseNamesList

        var remFiles = mutableListOf<String>()

        remFiles = checkWhichFilesExistsAndReturnWhichDoNot(fileNames)
        progress_horizontal.max = remFiles.size.toFloat()
//        downloadMultipleFiles(storageRef, remFiles)

        progress_horizontal.setOnProgressChangedListener { view, progress, isPrimaryProgress, isSecondaryProgress ->
            if(progress == remFiles.size.toFloat()){
                progress_circular.visibility = View.GONE
                start_button.visibility = View.VISIBLE
                cancelBtn.visibility = View.GONE

                isDownloaded = true
            }
        }

        cancelBtn.setOnClickListener {
            dismiss()
        }
        start_button.setOnClickListener {
            isDownloaded = true
            dismiss()
        }

        downloadBtn.setOnClickListener {
            downloadMultipleFiles(storageRef, remFiles)
            progress_circular.visibility = View.VISIBLE
            it.visibility = View.GONE
        }
    }


    private fun downloadMultipleFiles(storageRef: StorageReference, fileNames: List<String>) {
        for (fileName in fileNames) {
            downloadAndSaveVideos(storageRef, fileName)
        }
    }

    private fun downloadAndSaveVideos(storageRef: StorageReference, fileName: String) {
        val islandRef = storageRef.child(fileName)
        val filePrefix = fileName.split(".")[0]
        val fileSuffix = fileName.split(".")[1]
        val localFile = File.createTempFile(filePrefix, fileSuffix)

        val downlandRef = islandRef.getFile(localFile).addOnSuccessListener {
            // Local temp file has been created
//            Toast.makeText(ctx, "success", Toast.LENGTH_LONG).show()
            saveFile(localFile, fileName)
            localFile.delete()
        }.addOnFailureListener {
            // Handle any errors
            Toast.makeText(ctx, it.message, Toast.LENGTH_LONG).show()
        }
        downlandRef.addOnCompleteListener() {
            progress_horizontal.progress += 1f
        }
    }

    private fun checkWhichFilesExistsAndReturnWhichDoNot(fileNames: List<String>): MutableList<String> {
        val fileNotExistList = mutableListOf<String>()
        for (file in fileNames) {
            if (!fileExist(file)) {
                fileNotExistList.add(file)
            }
        }
        return fileNotExistList
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

}

