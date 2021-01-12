package se.mdh.student.dva232.projectgroup7

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class DicesActivity : AppCompatActivity(), SensorEventListener, ActivityInterface {


    private var dicesMap: HashMap<String, Int> = HashMap()
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var thrown = false
    private lateinit var quantity: String
    private lateinit var result: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dices2)

        result = findViewById(R.id.result)
        result.setOnClickListener {
            startActivity(Intent(baseContext, MainActivity::class.java))
        }
        findViewById<TextView>(R.id.resulttext).text = "Result pending"
        //Accelerometer
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer= sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        quantity = intent.getStringExtra("DICE_COUNT")!!

        val button = findViewById<Button>(R.id.button_rtd)
        button.setOnClickListener {
            Log.e("Log00", "aaaaa")
            displayResult()

        }


    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if (event.values[0] > 30 || event.values[1] > 30 || event.values[2] > 30)
                displayResult()
                    Log.e("Sensor", "Accelerometer")
        }


    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        var data: Data = object : Data {
            override val game: GameType
                get() = GameType.DICES

            override fun moveToCsv(): String {
                return quantity
            }

        }
        Pinger.changeContext(this, data)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        Pinger.stop()
        mService?.pauseMusic()
    }

    //sets dice map and applies results from
    private fun setDicesMap(noOfDice: Int) {
        for (e in 0 until noOfDice) {
            dicesMap["Dice $e"] = (0..6).random()
        }
    }


    //Displays the sum of all dices. This is the only visual change for the user. https://developer.android.com/guide/topics/sensors/sensors_motion for sensors. Sensor calibration? 
    private fun displayResult() {
        if (!thrown) {
            val ownsumView = findViewById<TextView>(R.id.resulttext)
            val winnerView = findViewById<TextView>(R.id.winnertext)

            var mySum = 0
            var opponentSum = 0
            //sendmove -> get the sum back
            //get opponents move -> get opponents sum back
            //compare sums, declare winner


            GlobalScope.launch {
                val diceData = DicesData(quantity.toInt())
                var ret: JSONObject = CommunicationLayer.addPlayerMove(diceData)

                Log.e("Log00", ret.getString("response"));
                if (ret.getString("response") != null) {
                    mySum = ret.getString("response")
                        .toInt()
                    Log.e("Log00", ret.getString("response"))
                    if (ret.getString("response") != null) {
                        mySum = ret.getString("response")
                            .toInt()

                        ret = CommunicationLayer.getOpponentMove(diceData)
                        delay(10)
                        opponentSum = ret.getString("response").toInt()
                        runOnUiThread {


                            ownsumView.text = mySum.toString()

                            if (opponentSum > mySum) {
                                winnerView.text = getString(R.string.lose)
                            }
                            if (opponentSum < mySum) {
                                winnerView.text = getString(R.string.win)
                            }
                            if (opponentSum == mySum) {
                                winnerView.text = getString(R.string.draw)
                            }
                        }
                    }

                }
            }


        }
        thrown = true
        runOnUiThread {
            result.visibility = View.VISIBLE
        }
    }

    override fun quit() {
        runOnUiThread{
            result.visibility = View.VISIBLE
            result.text = getString(R.string.opponent_left)
        }
    }

    override var mService: MusicService? = null
    override fun onBackPressed() {
        Pinger.stop()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}