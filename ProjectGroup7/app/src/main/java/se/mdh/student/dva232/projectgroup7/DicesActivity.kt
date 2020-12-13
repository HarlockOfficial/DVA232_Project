package se.mdh.student.dva232.projectgroup7

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class DicesActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dices2)

        roll(4)
    }

    //Get data from JSON file.
   private fun getData(array: Array<Int>, noOfDice: Int): Array<Int> {

        //Temporary fill array functionality. Replace!
        for (e in 0..noOfDice) {
            array[e] = e+10
        }
        return array
    }

    //Call on button. Calls getData. Array is created in function
    fun roll (noOfDice: Int) {
        var diceArray = arrayOf(Int)
        diceArray = getData(diceArray,noOfDice) //Fills array

        for (e in diceArray) {
            println(diceArray[e])
        }

    }
}