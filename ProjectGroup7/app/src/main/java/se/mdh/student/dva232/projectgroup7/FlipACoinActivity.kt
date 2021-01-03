package se.mdh.student.dva232.projectgroup7

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import kotlin.random.Random

class FlipACoinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flip_a_coin)

        findViewById<Button>(R.id.heads).setOnClickListener{
            flip()
        }
        findViewById<Button>(R.id.tails).setOnClickListener{
            flip()
        }
    }

    private fun flip(){
        findViewById<Button>(R.id.heads).visibility = View.GONE
        findViewById<Button>(R.id.tails).visibility = View.GONE
        showResult()
        findViewById<Button>(R.id.heads).visibility = View.VISIBLE
        findViewById<Button>(R.id.tails).visibility = View.VISIBLE
    }

    private fun showResult() {
        animate()
        if(Random.nextBoolean()) {
            findViewById<TextView>(R.id.flipResult).setText("HEADS")
        } else {
            findViewById<TextView>(R.id.flipResult).setText("TAILS")
        }
    }

    private fun animate() {

    }

}