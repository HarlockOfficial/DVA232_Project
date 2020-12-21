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

        findViewById<Button>(R.id.open_Dices).setOnClickListener {
            openWaitingRoom(GameType.DICES)
        }
        findViewById<Button>(R.id.open_tic_tac_toe).setOnClickListener {
            openWaitingRoom(GameType.TIC_TAC_TOE)
        }
        // TODO: after adding a button, give the button an ID and do like ↑
    }
    private fun openWaitingRoom(game: GameType){
        val intent = Intent(this, WaitingRoom::class.java)
        intent.putExtra("GAME_CODE", game.name)
        startActivity(intent)
    }

}