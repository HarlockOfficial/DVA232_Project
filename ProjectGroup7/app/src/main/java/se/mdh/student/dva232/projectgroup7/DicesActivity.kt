package se.mdh.student.dva232.projectgroup7

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class DicesActivity : AppCompatActivity() {


    private var dicesMap: HashMap<String, Int> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dices2)
        val button = findViewById<Button>(R.id.button_rtd)
        button.setOnClickListener{
            setDicesMap(4)
            displayResult()
        }



    }

    //sets dice map and applies results from
    private fun setDicesMap(noOfDice: Int) {
        for (e in 0 until noOfDice) {
            //val number = e + 1                          Not worth it..?
            dicesMap["Dice $e"] = e                     //Replace e with a function that gets a number from result
        }
    }


    //Displays the sum of all dices.
    private fun displayResult() {

        val text = findViewById<TextView>(R.id.resulttext)
        var sum = 0

        for ((key: String, value: Int) in dicesMap) {
            sum += value
        }

        text.text = sum.toString()
    }

    //Get data from JSON file.
    private fun getData(): Int {
        TODO("get information from API, then return the int.")
    }
}