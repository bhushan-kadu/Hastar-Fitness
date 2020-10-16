package com.hastarfitness.hastarfitnessapp.meditation

import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log.v
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hastarfitness.hastarfitnessapp.countDownTimerWithPause.CountDownTimerWithPause
import com.hastarfitness.hastarfitnessapp.R
import com.hastarfitness.hastarfitnessapp.appConstants.AppConstants
import com.hastarfitness.hastarfitnessapp.database.MeditationDbModel
import kotlinx.android.synthetic.main.activity_audio.*
import kotlinx.android.synthetic.main.activity_audio.play_pause_button
import java.io.IOException

class AudioActivity : AppCompatActivity() {
    var isActivityInPauseState = true
    lateinit var meditationData:MeditationDbModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)

        val GITHUB = """M 112.33,232.00
           C 109.27,216.19 108.87,196.79 112.33,181.00
             114.49,170.23 117.46,161.47 123.20,152.00
             123.20,152.00 133.52,137.00 133.52,137.00
             134.35,134.33 132.48,127.22 132.08,124.00
             132.08,124.00 132.08,104.00 132.08,104.00
             132.01,96.74 132.99,91.04 134.58,84.00
             135.43,80.23 135.55,76.88 140.05,76.15
             143.31,75.62 149.62,76.81 153.00,77.42
             163.72,79.36 175.26,83.38 185.00,88.25
             189.66,90.58 199.52,97.55 204.00,97.43
             204.00,97.43 226.00,94.17 226.00,94.17
             226.00,94.17 262.00,93.00 262.00,93.00
             262.00,93.00 289.00,95.42 289.00,95.42
             289.00,95.42 304.00,97.42 304.00,97.42
             304.00,97.42 323.00,87.31 323.00,87.31
             337.15,80.78 353.34,76.01 369.00,76.00
             373.18,94.49 375.03,99.37 374.88,119.00
             375.00,122.11 374.10,129.90 374.88,132.00
             375.51,134.39 381.16,141.98 383.00,145.00
             388.70,154.37 391.29,159.50 394.66,170.00
             398.78,182.87 399.20,189.90 400.09,203.00
             400.30,206.15 400.93,207.69 400.68,211.00
             400.68,211.00 399.88,217.00 399.88,217.00
             399.88,217.00 399.88,226.00 399.88,226.00
             399.88,226.00 396.00,246.00 396.00,246.00
             396.00,246.00 411.00,245.00 411.00,245.00
             428.38,244.97 445.75,244.73 463.00,247.00
             463.00,247.00 463.00,252.00 463.00,252.00
             444.80,249.60 426.36,249.79 408.00,250.00
             401.21,250.08 395.84,249.89 392.00,256.00
             424.77,256.00 425.60,255.90 458.00,262.00
             458.00,262.00 457.00,267.00 457.00,267.00
             457.00,267.00 442.00,264.08 442.00,264.08
             423.34,260.94 416.55,260.96 398.00,261.00
             387.16,261.02 390.41,262.38 382.52,273.00
             378.31,278.66 374.47,282.90 369.00,287.38
             353.12,300.40 324.64,312.05 304.00,313.00
             319.13,330.90 317.00,343.33 317.00,365.00
             317.00,365.00 317.00,412.00 317.00,412.00
             317.02,425.89 319.66,423.73 327.00,434.00
             316.41,434.00 301.71,434.11 294.42,424.96
             291.14,420.85 291.02,416.97 291.00,412.00
             291.00,412.00 291.00,377.00 291.00,377.00
             291.00,372.28 291.75,355.44 289.83,352.22
             288.76,350.43 287.66,350.01 286.00,349.00
             286.00,349.00 286.00,397.00 286.00,397.00
             286.00,404.53 285.12,420.60 287.56,427.00
             288.99,430.75 291.75,433.74 294.00,437.00
             283.57,437.00 272.86,437.63 265.22,428.96
             260.95,424.11 261.01,420.04 261.00,414.00
             261.00,414.00 261.00,358.00 261.00,358.00
             261.00,356.04 261.41,350.19 258.83,349.62
             255.57,348.90 256.00,356.29 256.00,358.00
             256.00,358.00 256.00,408.00 256.00,408.00
             256.00,414.94 257.24,424.28 252.44,429.79
             245.51,437.75 231.52,437.00 222.00,437.00
             224.16,433.75 226.82,430.71 228.16,427.00
             230.84,419.59 230.00,404.37 230.00,396.00
             230.00,396.00 230.00,349.00 230.00,349.00
             228.34,350.01 227.24,350.43 226.17,352.22
             224.25,355.44 225.00,372.28 225.00,377.00
             225.00,377.00 225.00,406.00 225.00,406.00
             225.00,410.72 225.45,417.70 223.87,422.00
             220.35,431.54 209.49,435.88 200.00,436.00
             200.00,436.00 191.00,436.00 191.00,436.00
             191.00,436.00 198.35,425.00 198.35,425.00
             198.35,425.00 199.00,408.00 199.00,408.00
             199.00,408.00 199.00,373.00 199.00,373.00
             184.67,373.00 164.76,374.09 152.00,367.10
             134.73,357.63 129.06,339.42 118.92,324.00
             110.67,311.45 107.99,312.37 99.00,304.00
             106.82,304.00 111.52,303.31 119.00,306.46
             133.89,312.73 143.02,327.99 157.00,335.69
             168.87,342.22 176.14,341.03 189.00,341.00
             191.21,341.00 194.81,341.21 196.69,339.98
             199.04,338.44 200.95,329.60 202.90,326.00
             206.09,320.10 209.35,316.69 214.00,312.00
             214.00,312.00 194.00,308.20 194.00,308.20
             177.44,304.72 161.93,298.26 148.00,288.65
             142.26,284.69 137.24,280.35 132.75,275.00
             122.86,263.19 125.57,261.02 114.00,261.00
             95.95,260.96 89.11,261.03 71.00,264.08
             71.00,264.08 56.00,267.00 56.00,267.00
             56.00,267.00 55.00,262.00 55.00,262.00
             86.60,256.05 87.95,256.00 120.00,256.00
             120.00,256.00 119.00,251.00 119.00,251.00
             119.00,251.00 103.00,250.00 103.00,250.00
             85.24,249.97 67.63,249.67 50.00,252.00
             50.00,252.00 50.00,247.00 50.00,247.00
             66.92,244.77 83.95,244.97 101.00,245.00
             101.00,245.00 116.00,246.00 116.00,246.00
             116.00,246.00 112.33,232.00 112.33,232.00 Z
           M 131.00,322.00
           C 131.00,322.00 130.00,325.00 130.00,325.00
             130.00,325.00 134.00,325.00 134.00,325.00
             134.00,325.00 131.00,322.00 131.00,322.00 Z
           M 136.00,332.00
           C 136.00,332.00 141.00,330.00 141.00,330.00
             137.84,329.58 137.84,329.35 136.00,332.00 Z
           M 143.00,336.00
           C 143.00,336.00 143.00,339.00 143.00,339.00
             143.00,339.00 149.00,337.00 149.00,337.00
             149.00,337.00 143.00,336.00 143.00,336.00 Z
           M 152.00,343.00
           C 152.00,343.00 152.00,345.00 152.00,345.00
             152.00,345.00 159.00,345.00 159.00,345.00
             159.00,345.00 159.00,343.00 159.00,343.00
             159.00,343.00 152.00,343.00 152.00,343.00 Z
           M 165.00,346.00
           C 166.36,349.85 167.14,349.65 171.00,349.00
             171.00,349.00 171.00,347.00 171.00,347.00
             171.00,347.00 165.00,346.00 165.00,346.00 Z
           M 178.00,347.00
           C 178.00,347.00 178.00,349.00 178.00,349.00
             178.00,349.00 184.00,349.00 184.00,349.00
             184.00,349.00 184.00,347.00 184.00,347.00
             184.00,347.00 178.00,347.00 178.00,347.00 Z
           M 190.00,349.00
           C 190.00,349.00 197.00,349.00 197.00,349.00
             197.00,349.00 197.00,347.00 197.00,347.00
             193.13,346.36 192.85,346.27 190.00,349.00 Z"""

        init()
        fillableLoader.setSvgPath(GITHUB);
        fillableLoader.start()
        startAudio()
        mediaPlayer.pause()
        length = mediaPlayer.currentPosition
        startTimer()



        play_pause_button.setOnClickListener {
            togglePlayPauseDrawableAndGif()
            if(isActivityInPauseState){
                timer.resume()
                mediaPlayer.seekTo(length)
                mediaPlayer.start()
                isActivityInPauseState = false

            }else{
                timer.pause()
                length = mediaPlayer.currentPosition
                mediaPlayer.pause()
                isActivityInPauseState = true
            }
        }



    }
    fun init(){
        meditationData = intent.getParcelableExtra(AppConstants.MEDITATION_DATA)!!

        title_text.text = meditationData.type
        desc_text.text = meditationData.desc
    }
    lateinit var timer: CountDownTimerWithPause;

    fun startTimer(){
        val duration = mediaPlayer.duration.toLong()
        timer = object : CountDownTimerWithPause(duration, 1000, false){
            override fun onFinish() {

                isActivityInPauseState = true
                mediaPlayer.release()
            }

            override fun onTick(millisUntilFinished: Long) {
                val value = (  millisUntilFinished.toDouble())/duration*90
                fillableLoader.setPercentage(90 - value.toFloat())
                v("millisUntilFinished", (90 - value).toString())
            }

        }.create()
    }


    private fun togglePlayPauseDrawableAndGif() {


        val playDrawable = R.drawable.ic_play_round
        val pauseDrawable = R.drawable.ic_pause_round

        if (isActivityInPauseState) {
            play_pause_button.setImageResource(pauseDrawable)
        } else {
            play_pause_button.setImageResource(playDrawable)

        }
    }
    val mediaPlayer = MediaPlayer();
    var length = 0
    fun startAudio(){


             val afd:AssetFileDescriptor
            try {
                afd = assets.openFd("sample.mp3");
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
//                mediaPlayer.setDataSource(afd.fileDescriptor);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (e:IOException) {
                e.printStackTrace();
            }
            Toast.makeText(this, "Playing audio from Asset directory", Toast.LENGTH_SHORT).show();
        }

    }



