package se.mdh.student.dva232.projectgroup7

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class WaitingRoom : AppCompatActivity(), ActivityInterface {
    private lateinit var gameCode: GameType
    var isBackPressed: Boolean = false
    lateinit var data: Data
    lateinit var quantity : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("WaitingRoom", "onCreate started\n")
        setContentView(R.layout.waiting_room)
        gameCode = GameType.valueOf(intent.getStringExtra("GAME_CODE")!!)
        if (gameCode == GameType.DICES) {
            quantity = intent.getStringExtra("DICE_QUANTITY")!!
        }
        lateinit var out: String
        val gameClass = if (gameCode == GameType.ROCK_PAPER_SCISSORS) {
            out = "Rock Paper Scissors"
            RockPaperScissors::class.java
        } else if (gameCode == GameType.TIC_TAC_TOE) {
            out = "Tic Tac Toe"
            TicTacToe::class.java
        } else if (gameCode == GameType.DICES) {
            out = "Dices"
            DicesActivity::class.java
        } else if (gameCode == GameType.BLOW) {
            out = "Blow"
            BlowActivity::class.java
        } else {  //TODO: if game not present add "else if" here
            out = "Unrecognized Game"
            null
        }
        if (out == "Unrecognized Game") {
            Toast.makeText(
                baseContext,
                getString(R.string.waiting_room_game_error),
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        val label = findViewById<TextView>(R.id.waiting_room_label)
        label.text = getString(R.string.waiting_room, out)

        data = if (gameCode == GameType.ROCK_PAPER_SCISSORS) {
            RockPaperScissorsData("")
        } else if (gameCode == GameType.TIC_TAC_TOE) {
            object : Data {
                override val game: GameType
                    get() = GameType.TIC_TAC_TOE

                override fun moveToCsv(): String {
                    return ""
                }
            }
        } else if (gameCode == GameType.DICES) {

            Log.e("quantity", quantity)
            object : Data {
                override val game: GameType
                    get() = GameType.DICES

                override fun moveToCsv(): String {
                    Log.e("quantity", quantity)
                    return quantity
                }
            }    // TODO: when created add here the correct data Implementation
        }  else if (gameCode == GameType.BLOW) {
            object : Data {
                override val game: GameType
                    get() = GameType.BLOW

                override fun moveToCsv(): String {
                    return ""
                }
            }     // TODO: when created add here the correct data Implementation

        } else if (gameCode == GameType.FLIP_A_COIN) {
            object : Data {
                override val game: GameType
                    get() = GameType.FLIP_A_COIN

                override fun moveToCsv(): String {
                    return ""
                }
            }
        } else {
            onBackPressed()
             object: Data {
                 override val game: GameType
                     get() = TODO("Impossible Game")

                 override fun moveToCsv(): String {
                     TODO("Impossible Game")
                 }

             }
        }
        GlobalScope.launch(Dispatchers.IO) {
            Log.e("data from foo", data.moveToCsv())
            var ret: JSONObject = CommunicationLayer.addPlayerToMultiplayerQueue(data)
            Pinger.isPlayerAdded = true
            Log.e("WaitingRoom Coroutine", "Player added to queue")
            val uuid: String = CommunicationLayer.uuid

            if (ret["response"] == "in_queue") {
                do {
                    delay(10)
                    ret = CommunicationLayer.checkMultiplayerQueue(data)
                    if (isBackPressed) {
                        return@launch
                    }
                } while (ret["response"] == "in_queue")
            }
            Log.e("THIS ONE SIR", ret.toString())
            ret = JSONObject(ret["response"] as String)

            val intent = Intent(this@WaitingRoom, gameClass)
            intent.putExtra("isStarting", ret["starting_player"] == uuid)
            intent.putExtra("field", ret["field"].toString())
            if (gameCode == GameType.DICES) {
                intent.putExtra("DICE_COUNT", quantity)
            }
            startActivity(intent)
        }
        findViewById<Button>(R.id.leave_queue_btn).setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                onBackPressed()
            }
        }
    }

    //function needed to instantiate pinger, not really necessary, the only "response" possible is "ok" (in the waiting room)
    override fun quit() {
        throw NotImplementedError("Impossible that quit gets called in waiting room")
    }

    override var mService: MusicService? = null

    override fun onPause() {
        Log.e("WatingRoom", "onPause started\n")
        isBackPressed = true
        GlobalScope.launch {
            CommunicationLayer.delPlayerFromMultiplayerQueue(data)
        }
        Pinger.stop()
        super.onPause()
    }

    override fun onResume() {
        Log.e("WaitingRoom", "onResume started\n")
        var data : Data = object:Data{
            override val game: GameType
                get() = gameCode

            override fun moveToCsv(): String {
                return ""
            }

        }
        Pinger.changeContext(this, data)
        super.onResume()
        if(isBackgroundEnabled(applicationContext)){
            //startService(Intent(this, MusicService::class.java))
            val intent =  Intent(this, MusicService::class.java)
            bindService(intent, getConnection(), Context.BIND_AUTO_CREATE)
            startService(intent)
            //mService?.resumeMusic()

        }
    }

    override fun onBackPressed() {
        isBackPressed = true
        GlobalScope.launch {
            CommunicationLayer.delPlayerFromMultiplayerQueue(data)
        }
        Pinger.stop()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}