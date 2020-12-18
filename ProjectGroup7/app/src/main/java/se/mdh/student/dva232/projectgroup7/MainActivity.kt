package se.mdh.student.dva232.projectgroup7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

// TODO: guide how to make Hamburger Menu -> https://github.com/codepath/android_guides/wiki/Fragment-Navigation-Drawer
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.open_RPS).setOnClickListener {
            openWaitingRoom(GameType.ROCK_PAPER_SCISSORS)
        }

        openTTT()
        findViewById<Button>(R.id.open_Dices).setOnClickListener {
            openWaitingRoom(GameType.DICES)
        }
        // TODO: after adding a button, give the button an ID and do like ↑
    }
    private fun openWaitingRoom(game: GameType){
        val intent = Intent(this, WaitingRoom::class.java)
        intent.putExtra("GAME_CODE", game.code)
        startActivity(intent)
    }

    fun openTTT() {
        val intent = Intent(this, TicTacToeActivity::class.java)
        startActivity(intent)
    }
}