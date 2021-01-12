package se.mdh.student.dva232.projectgroup7

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.util.*
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
private var started: Boolean = false
private var isPlayerTurn: Boolean = false

class BlowActivity : AppCompatActivity(), ActivityInterface {

    override var mService: MusicService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blow)
        supportActionBar?.hide()
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        val timer = Timer()

        isPlayerTurn = intent.getBooleanExtra("isStarting", false)

        val mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        val temp = File.createTempFile("audio", "tmp", cacheDir)
        //Read only file system, permissions to write to "disk"?, requires min API lvl 26, current min is 19
        mediaRecorder.setOutputFile(temp)
        mediaRecorder.setAudioSamplingRate(48000)
        mediaRecorder.setAudioEncodingBitRate(48000)
        mediaRecorder.prepare()

        startGame(mediaRecorder, timer)
    }

    //https://stackoverflow.com/questions/3928202/get-microphone-volume
    //https://stackoverflow.com/questions/7197798/get-the-microphone-sound-level-decibel-level-in-android/51815138
    private fun startGame(mediaRecorder: MediaRecorder, timer: Timer) {
        started = true
        mediaRecorder.start()
        //Start timer here, includes updating amplitude, updating value (temp), running the globalscope
        val view = findViewById<TextView>(R.id.ball)

        //Should not be needed, just in case
        timer.purge()

        var retValue: Int
        val task = timerTask {
            val amplitude: Int = (mediaRecorder.maxAmplitude / 32762f * 100f).toInt()
            this@BlowActivity.runOnUiThread {
                view.text = amplitude.toString()
            }

            val blowData = BlowData(amplitude)
            GlobalScope.launch {
                var ret: JSONObject = CommunicationLayer.addPlayerMove(blowData) //Our data is sent
                while (true) {
                    try {
                        retValue = ret.getString("response").toInt()        //We get our new data
                        if (isPlayerTurn) {
                            if (retValue >= 200) {
                                endGame(mediaRecorder, timer, true)
                            } else if (retValue <= 0) {
                                endGame(mediaRecorder, timer, false)
                            } else {
                                runOnUiThread {
                                    val constraintLayout =
                                        this@BlowActivity.findViewById<TextView>(R.id.ball).parent as ConstraintLayout
                                    val set = ConstraintSet()
                                    set.clone(constraintLayout)
                                    set.setVerticalBias(R.id.ball, 1 - (retValue / 200f))
                                    set.applyTo(constraintLayout)
                                }
                            }
                        } else {
                            if (retValue <= 0) {
                                endGame(mediaRecorder, timer, true)
                            } else if (retValue >= 200) {
                                endGame(mediaRecorder, timer, false)
                            } else {
                                runOnUiThread {
                                    val constraintLayout =
                                        this@BlowActivity.findViewById<TextView>(R.id.ball).parent as ConstraintLayout
                                    val set = ConstraintSet()
                                    set.clone(constraintLayout)
                                    set.setVerticalBias(R.id.ball, retValue / 200f)
                                    set.applyTo(constraintLayout)
                                }
                            }
                        }
                        break
                    } catch (e: NumberFormatException) {
                        ret = CommunicationLayer.getOpponentMove(blowData)
                        continue
                    }
                }
            }
        }
        timer.schedule(task, 1, 50)
        //recording to value
    }

    //True if a user has won
    private fun endGame(mediaRecorder: MediaRecorder, timer: Timer, standing: Boolean) {
        timer.cancel()
        mediaRecorder.stop()
        Pinger.stop()
        if (standing) {
            showGameResult(this, GameType.BLOW, MatchResult.WIN)
        } else {
            showGameResult(this, GameType.BLOW, MatchResult.LOSE)
        }
    }

    override fun onBackPressed() {
        Pinger.stop()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
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
        showGameResult(this, GameType.BLOW, MatchResult.DISCONNECT)
    }

    //this has to be the same
    override fun onPause() {
        Pinger.stop()
        super.onPause()
        mService?.pauseMusic()
    }

    //here ↓ you have to change the data class to the correct one
    override fun onResume() {
        val blowData: Data = object : Data {
            override val game: GameType
                get() = GameType.BLOW

            override fun moveToCsv(): String {
                return ""
            }

        }
        Pinger.changeContext(this, blowData)
        super.onResume()
        if (isBackgroundEnabled(applicationContext)) {
            val intent = Intent(this, MusicService::class.java)
            bindService(intent, getConnection(), Context.BIND_AUTO_CREATE)
            startService(intent)
        }
    }


}