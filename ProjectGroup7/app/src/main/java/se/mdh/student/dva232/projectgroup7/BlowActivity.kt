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
import java.lang.NumberFormatException
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
private var standing: Int = 100
private var started: Boolean = false
private var isPlayerTurn: Boolean = false


class BlowActivity : AppCompatActivity(), ActivityInterface {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blow)

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        val timer = Timer()

        isPlayerTurn = intent.getBooleanExtra("isStarting", false)

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
            if (!started) {
                startGame(mediaRecorder, timer)
                //temporary()
            }

        }

        findViewById<Button>(R.id.record_stop).setOnClickListener{
            if (started) {
                endGame(mediaRecorder, timer, false)
            }
        }



    }

    //https://stackoverflow.com/questions/3928202/get-microphone-volume
    //https://stackoverflow.com/questions/7197798/get-the-microphone-sound-level-decibel-level-in-android/51815138
    private fun startGame (mediaRecorder: MediaRecorder, timer: Timer) {
        started = true
        mediaRecorder.start()
        //Start timer here, includes updating amplitude, updating value (temp), running the globalscope
        val view =  findViewById<TextView>(R.id.textView)
        timer.purge()               //Should not be needed, just in case

        var retValue : Int
        var oppValue : Int
        val task = timerTask {
            val amplitude : Int = mediaRecorder.maxAmplitude%100
            this@BlowActivity.runOnUiThread(java.lang.Runnable {
                view.text = amplitude.toString()
            })


            var blowData = BlowData (amplitude)
            GlobalScope.launch {
                var ret: JSONObject = CommunicationLayer.addPlayerMove(blowData) //Our data is sent
                    Log.e("Our new data:", ret.getString("response"))

                try {
                    retValue = ret.getString("response").toInt()        //We get our new data
                    if (isPlayerTurn) {
                        if (retValue >= 200) {
                            endGame(mediaRecorder,timer,true)
                            //endgame on all of these
                        }
                        else if (retValue <= 0) {
                            endGame(mediaRecorder,timer,false)
                        }
                    } else {
                        if (retValue <= 0) {
                            endGame(mediaRecorder,timer,true)
                        }
                        else if (retValue >= 200) {
                            endGame(mediaRecorder,timer,false)
                        }
                    }
                }
                catch(e: NumberFormatException ) {
                    ret = CommunicationLayer.getOpponentMove(blowData)
                    Log.e("Opponents new data:", ret.getString("response"))
                    oppValue = ret.getString("response").toInt()        //We get the opponents data
                    if (isPlayerTurn) {
                        if (oppValue >= 200) {
                            endGame(mediaRecorder,timer,true)
                        }
                        else if (oppValue <= 0) {
                            endGame(mediaRecorder,timer,false)
                        }
                    } else {
                        if (oppValue <= 0) {
                            endGame(mediaRecorder,timer,true)
                        }
                        else if (oppValue >= 200) {
                            endGame(mediaRecorder,timer,false)
                        }
                    }

                }

            }

        }



        timer.schedule(task,1,500) //Bleh
                    //recording to value
    }

    private fun endGame (mediaRecorder: MediaRecorder, timer: Timer, standing : Boolean) {
        timer.cancel()
        mediaRecorder.stop()
        val view =  findViewById<TextView>(R.id.textView)
        if (standing) {
            view.text = "You won!"
        }else {
            view.text = "You lost..."
        }
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

    override fun quit() {
        TODO("Tell user it's over")
    }

    //this has to be the same
    override fun onPause() {
        Pinger.stop()
        super.onPause()
    }
    //here ↓ you have to change the data class to the correct one
    override fun onResume() {
        Pinger.changeContext(this, GameType.BLOW)
        super.onResume()
    }


}