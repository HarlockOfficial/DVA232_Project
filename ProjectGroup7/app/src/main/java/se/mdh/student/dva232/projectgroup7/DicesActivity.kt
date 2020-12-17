package se.mdh.student.dva232.projectgroup7

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DicesActivity : AppCompatActivity(), SensorEventListener {


    private var dicesMap: HashMap<String, Int> = HashMap()
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dices2)

        //Accelerometer
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)


        val button = findViewById<Button>(R.id.button_rtd)
        button.setOnClickListener {
            //setDicesMap(4)
            displayResult(4)
        }


    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if (event.values[0] > 5 || event.values[1] > 10 || event.values[2] > 1)
                //setDicesMap(4)
            displayResult(4)
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onResume() { //No idea if this is working properly
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    //sets dice map and applies results from
    private fun setDicesMap(noOfDice: Int) {
        for (e in 0 until noOfDice) {
            dicesMap["Dice $e"] = (0..6).random()
        }

        //Might be redundant, we just get a sum value
    }


    //Displays the sum of all dices. This is the only visual change for the user. https://developer.android.com/guide/topics/sensors/sensors_motion for sensors. Sensor calibration? 
    private fun displayResult(amount: Int) {

        val ownsumView = findViewById<TextView>(R.id.resulttext)
        val winnerView = findViewById<TextView>(R.id.winnertext)

        //sendmove -> get the sum back
        //get opponents move -> get opponents sum back
        //compare sums, declare winner

        var mySum = 0
        var opponentSum = 0

        GlobalScope.launch {
            val diceData = DicesData(amount)
            var ret: JSONObject = CommunicationLayer.addPlayerMove(diceData)

                Log.e("Log00", ret.getString("response"));
            if (ret.getString("response") != null) {                                                             //Any way to just check the errors?
                mySum = ret.getString("response").toInt()                                                        //Is this really right? Redundant toInt?

                ret  = CommunicationLayer.getOpponentMove(diceData)
                delay(10)
                opponentSum = ret.getString("response").toInt()
                runOnUiThread {


                ownsumView.text = mySum.toString()

                if (opponentSum > mySum) {
                    winnerView.text = "Opponent wins..."
                }
                if (opponentSum < mySum) {
                    winnerView.text = "You win!"
                }
                if (opponentSum == mySum) {
                    winnerView.text = "Draw!"
                }
                }
            }



        }

    }

    //Get data from JSON file.
    private fun getData(): Int {
        TODO("get information from API, then return the int.")
    }
}