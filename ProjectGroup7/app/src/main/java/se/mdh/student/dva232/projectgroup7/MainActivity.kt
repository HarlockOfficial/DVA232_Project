package se.mdh.student.dva232.projectgroup7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

// TODO: guide how to make Hamburger Menu -> https://github.com/codepath/android_guides/wiki/Fragment-Navigation-Drawer
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openTTT()
    }

    fun openRPS(view: View) {
        val intent = Intent(this, RockPaperScissors::class.java)
        startActivity(intent)
    }

    fun openTTT() {
        val intent = Intent(this, TicTacToeActivity::class.java)
        startActivity(intent)
    }
}