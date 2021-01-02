package se.mdh.student.dva232.projectgroup7

import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.media.MediaRecorder.AudioSource.MIC
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.util.*
import java.util.jar.Manifest
import kotlin.concurrent.timerTask

//Two users. Start with a value of 100, can go up to 200 in which case the user wins
//User blows into microphone, which will then be transformed into a numeric value
//Numeric value will be sent to server, which will compare it to the other users number, then return it
//
// Record audio -> save to file -> read file and get value -> send value -> get value, update internal value and start over
//
//Borrowed request permissions from https://developer.android.com/guide/topics/media/mediarecorder


//
//Get peak value from mic -> save peak value -> send peak value -> retrieve calculated score
//Normalize the score to a value between 0 and 100? Percentages?


//Request permission for mic

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200



class BlowActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blow)

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        val timer = Timer()

        val mediaRecorder : MediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC) //This mic has some sort of processing on the input.
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) //Not sure which one to use to be honest
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        val temp = File.createTempFile("audio", "tmp", cacheDir)
        mediaRecorder.setOutputFile(temp) //Read only file system, permissions to write to "disk"?, requires min API lvl 26, current min is 19
        mediaRecorder.setAudioSamplingRate(48000)
        mediaRecorder.setAudioEncodingBitRate(48000)
        mediaRecorder.prepare()


        findViewById<Button>(R.id.record_start).setOnClickListener{
            startGame(mediaRecorder, timer)
        }

        findViewById<Button>(R.id.record_stop).setOnClickListener{
            endGame(mediaRecorder, timer)
        }



    }

    //https://stackoverflow.com/questions/3928202/get-microphone-volume
    //https://stackoverflow.com/questions/7197798/get-the-microphone-sound-level-decibel-level-in-android/51815138
    private fun startGame (mediaRecorder: MediaRecorder, timer: Timer) {
        mediaRecorder.start()
        //Start timer here, includes updating amplitude, updating value (temp), running the globalscope
        val view =  findViewById<TextView>(R.id.textView)
        timer.purge()               //Should not be needed, just in case

        val task = timerTask {
            val amplitude : Int = mediaRecorder.maxAmplitude
            this@BlowActivity.runOnUiThread(java.lang.Runnable {
                view.text = amplitude.toString()
            })

            GlobalScope.launch {
                // var ret: JSONObject = CommunicationLayer.addPlayerMove(blowData)
                TODO("Send values back and forth. Why is mic not picking up any noise? Normalize the input. Implement checks to avoid crashes.")
            }
        }

        timer.schedule(task,10,20)


        //recording to value
    }

    private fun endGame (mediaRecorder: MediaRecorder, timer: Timer) {
        timer.cancel()
        mediaRecorder.stop()
        val view =  findViewById<TextView>(R.id.textView)           //DEBUG
        view.text = "STOPPED"                                       //DEBUG

    }

    private var permissionAccepted = false
    private var permissions: Array<String> = arrayOf(android.Manifest.permission.RECORD_AUDIO)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionAccepted) finish()
    }


}


//Normalize number (percent)