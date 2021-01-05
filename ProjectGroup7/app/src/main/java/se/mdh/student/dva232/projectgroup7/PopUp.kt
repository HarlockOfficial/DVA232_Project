package se.mdh.student.dva232.projectgroup7

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import org.w3c.dom.Text

class PopUp : AppCompatActivity() {                                     //Information about pop ups from : https://www.youtube.com/watch?v=fn5OlqQuOCk

   lateinit var game: GameType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pop_up)
        game = GameType.valueOf(intent.getStringExtra("GAME")!!)
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        window.setLayout((dm.widthPixels*0.9).toInt(), (dm.heightPixels*0.75).toInt())

        var startButton = findViewById<Button>(R.id.button_start)
        findViewById<Button>(R.id.button_back).setOnClickListener {
            finish()
        }
        var header = findViewById<TextView>(R.id.name_header)
        var infoText = findViewById<TextView>(R.id.text_info)
        var image = findViewById<ImageView>(R.id.image_game)
        var field = findViewById<EditText>(R.id.field_numDices)
        var amntDicesText = findViewById<TextView>(R.id.text_amnt)

        field.isVisible = false
        amntDicesText.isVisible = false

        if (game == GameType.BLOW) {

            header.text = "BLOW GAME"
            infoText.text = "This is the blow game temporary info text"
            image.setImageResource(R.drawable.ic_tumbleweed)

            startButton.setOnClickListener {
                openWaitingRoom(GameType.BLOW)
            }
        } else if (game == GameType.DICES) {                    //Set Dices amount

            header.text = "DICE GAME"
            infoText.text = "This is the dice game temporary info text"
            image.setImageResource(R.drawable.ic_dice)         //set image, look up
            field.isVisible = true
            amntDicesText.isVisible = true

            startButton.setOnClickListener {
                openWaitingRoom(GameType.DICES)
            }
        }  else if (game == GameType.TIC_TAC_TOE) {

            header.text = "TIC TAC TOE"
            infoText.text = "This is the Tic Tac Toe game temporary info text"
            //image                 set image, look up

            startButton.setOnClickListener {
                openWaitingRoom(GameType.TIC_TAC_TOE)
            }
        } else if (game == GameType.ROCK_PAPER_SCISSORS) {

            header.text = "Rock Paper Scissors"
            infoText.text = "This is the Rock Paper Scissors game temporary info text"
            //image                 set image, look up

            startButton.setOnClickListener {
                openWaitingRoom(GameType.ROCK_PAPER_SCISSORS)
            }
        } else if (game == GameType.FLIP_A_COIN) {

            header.text = "Flip a Coin"
            infoText.text = "This is the Flip a coin game temporary info text"
            //image                 set image, look up

            startButton.setOnClickListener {
                openWaitingRoom(GameType.FLIP_A_COIN)
            }
        } else {

            header.text = "Something went wrong"
            infoText.text = "You should not be seeing this"
            //image                 set image, look up

            startButton.setOnClickListener {
                openWaitingRoom(GameType.FLIP_A_COIN)
            }
        }

    }

    private fun openWaitingRoom(game: GameType){
        val intent = Intent(this, WaitingRoom::class.java)
        intent.putExtra("GAME_CODE", game.name)
        startActivity(intent)
    }
}