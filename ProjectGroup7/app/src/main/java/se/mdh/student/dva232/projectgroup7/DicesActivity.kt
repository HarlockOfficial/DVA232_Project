package se.mdh.student.dva232.projectgroup7

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class DicesActivity : AppCompatActivity(), SensorEventListener, ActivityInterface {

    override var mService: MusicService? = null

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var thrown = false
    private lateinit var quantity: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dices2)

        findViewById<TextView>(R.id.resulttext).text = getString(R.string.result_pending)
        //Accelerometer
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        quantity = intent.getStringExtra("DICE_COUNT")!!

        val button = findViewById<Button>(R.id.button_rtd)
        button.setOnClickListener {
            displayResult()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if (event.values[0] > 30 || event.values[1] > 30 || event.values[2] > 30)
                displayResult()
        }
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // we need to have this method empty
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        val data: Data = object : Data {
            override val game: GameType
                get() = GameType.DICES

            override fun moveToCsv(): String {
                return quantity
            }
        }
        Pinger.changeContext(this, data)
        if (isBackgroundEnabled(applicationContext)) {
            val intent = Intent(this, MusicService::class.java)
            bindService(intent, getConnection(), Context.BIND_AUTO_CREATE)
            startService(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        Pinger.stop()
        mService?.pauseMusic()
    }

    //Displays the sum of all dices. This is the only visual change for the user.
    // https://developer.android.com/guide/topics/sensors/sensors_motion for sensors. Sensor calibration?
    private fun displayResult() {
        if (!thrown) {
            val ownsumView = findViewById<TextView>(R.id.resulttext)

            var mySum: Int
            var opponentSum: Int
            //sendmove -> get the sum back
            //get opponents move -> get opponents sum back
            //compare sums, declare winner

            GlobalScope.launch {
                val diceData = DicesData(quantity.toInt())
                var ret: JSONObject = CommunicationLayer.addPlayerMove(diceData)

                mySum = ret.getString("response").toInt()

                ret = CommunicationLayer.getOpponentMove(diceData)
                delay(10)
                opponentSum = ret.getString("response").toInt()
                runOnUiThread {
                    ownsumView.text = mySum.toString()

                    if (opponentSum > mySum) {
                        showGameResult(
                            this@DicesActivity,
                            GameType.DICES,
                            MatchResult.LOSE,
                            diceCount = diceData.amountOfDices
                        )
                    }
                    if (opponentSum < mySum) {
                        showGameResult(
                            this@DicesActivity,
                            GameType.DICES,
                            MatchResult.WIN,
                            diceCount = diceData.amountOfDices
                        )
                    }
                    if (opponentSum == mySum) {
                        showGameResult(
                            this@DicesActivity,
                            GameType.DICES,
                            MatchResult.DRAW,
                            diceCount = diceData.amountOfDices
                        )
                    }
                }
            }
            thrown = true
        }
    }

    override fun quit() {
        showGameResult(this@DicesActivity, GameType.DICES, MatchResult.DRAW, quantity.toInt())
    }

    override fun onBackPressed() {
        Pinger.stop()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}