package se.mdh.student.dva232.projectgroup7

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class DicesActivity : AppCompatActivity(), SensorEventListener {


    private var dicesMap: HashMap<String, Int> = HashMap()
    private lateinit var sensorManager : SensorManager
    private var accelerometer: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dices2)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager



        val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)


        val button = findViewById<Button>(R.id.button_rtd)
        button.setOnClickListener{
            setDicesMap(4)
            displayResult()
        }




    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if (event.values[0] > 5 || event.values[1] > 10|| event.values[2] > 1)
            setDicesMap(4)
            displayResult()
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
            //val number = e + 1                          Not worth it..?
            dicesMap["Dice $e"] = e                     //Replace e with a function that gets a number from result
        }
    }


    //Displays the sum of all dices. This is the only visual change for the user. https://developer.android.com/guide/topics/sensors/sensors_motion for sensors. Sensor calibration? 
    private fun displayResult() {

        val text = findViewById<TextView>(R.id.resulttext)
        var sum = 0

        for ((key: String, value: Int) in dicesMap) { //Key behövs inte?
            sum += value
        }

        text.text = sum.toString()
    }

    //Get data from JSON file.
    private fun getData(): Int {
        TODO("get information from API, then return the int.")
    }
}