package se.mdh.student.dva232.projectgroup7

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class TicTacToe : AppCompatActivity(), ActivityInterface {
    private val buttonsCount = 9
    lateinit var data: TicTacToeData
    var buttons = arrayOfNulls<Button>(buttonsCount)
    var isPlayersTurn = false
    var gameState: Array<String?> = arrayOfNulls<String>(buttonsCount)
    lateinit var symbol: TicTacToeData.PlayersSymbol
    lateinit var result: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tic_tac_toe)
        supportActionBar?.hide()
        result = findViewById(R.id.result_tic_tac_toe)
        result.setOnClickListener {
            startActivity(Intent(baseContext, MainActivity::class.java))
        }

        isPlayersTurn = intent.getBooleanExtra("isStarting", false)

        // prepare clean game field
        gameState = intent.getStringExtra("field")!!.split(",").toTypedArray()

        symbol = if (isPlayersTurn) {
            TicTacToeData.PlayersSymbol.CIRCLE
        } else {
            TicTacToeData.PlayersSymbol.CROSS
        }

        data = TicTacToeData(symbol)


        setupButtonsOnclicks()
        if (!isPlayersTurn) {
            GlobalScope.launch(Dispatchers.IO) { waitForOpponentsMove() }
        }
    }

    private fun setupButtonsOnclicks() {
        val idPrefix = "field_"
        for (i in 1..buttonsCount) {
            val id = resources.getIdentifier(idPrefix + i, "id", packageName)
            val button = findViewById<Button>(id)
            button.tag = i.toString()
            button.setOnClickListener { view ->
                run {
                    data.move = (view.tag as String).toInt()
                    println("Sending move " + data.move)
                    GlobalScope.launch(Dispatchers.IO) {
                        handleMove(
                            CommunicationLayer.addPlayerMove(
                                data
                            )
                        )
                    }
                }
            }
            buttons[i - 1] = button
        }

    }

    private suspend fun handleMove(response: JSONObject) {
        if (response["response"] == "ok") {
           Log.e("TTT Move Response","Received OK")
            gameState[data.move - 1] = symbol.symbol
            updateGameField()

            waitForOpponentsMove()
        } else if (response["response"] == "error") {
            Log.e("TTT Move Response","Error")
        } else {
          Log.e("TTT Move Response", response["response"].toString())
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private suspend fun waitForOpponentsMove() {
        var response = CommunicationLayer.getOpponentMove(data)
        var serverGameState: Array<String?> = parseGameStateFromJSON(response)

        // if this player wins, this should end the game
        // the other player needs move first to win, so it will go though the loop
        if (isEndOfGame(response)) {
            return
        }

        while (gameState contentEquals serverGameState) {

            // not to kill processor and battery
            println("Receiving field $serverGameState")
            delay(500)
            response = CommunicationLayer.getOpponentMove(data)
            serverGameState = parseGameStateFromJSON(response)

        }

        gameState = serverGameState
        updateGameField()
        if (isEndOfGame(response)) {
            return
        }
    }

    private fun isEndOfGame(response: JSONObject): Boolean {
        val winner = JSONObject(response["response"] as String)["winner"]
        if (winner == "") {
            return false
        } else if (winner == this.symbol.symbol) {
            // TODO visualize win
            runOnUiThread {
                result.text = getString(R.string.win)
                result.visibility = View.VISIBLE
            }
            return true
        } else if (winner == "draw") {
            // TODO visualize draw
            runOnUiThread {
                result.text = getString(R.string.draw)
                result.visibility = View.VISIBLE
            }
            return true
        } else {
            // TODO visualize loss
            runOnUiThread {
                result.text = getString(R.string.lose)
                result.visibility = View.VISIBLE
            }
            return true
        }
    }

    private fun updateGameField() {
        runOnUiThread {
            for (i in 0 until buttonsCount) {
                if (gameState[i] == TicTacToeData.PlayersSymbol.CROSS.symbol) {
                    buttons[i]?.setBackgroundResource(R.drawable.tic_tac_toe_cross_field)
                } else if (gameState[i] == TicTacToeData.PlayersSymbol.CIRCLE.symbol) {
                    buttons[i]?.setBackgroundResource(R.drawable.tic_tac_toe_circle_field)
                } else if (gameState[i] == TicTacToeData.PlayersSymbol.EMPTY_FIELD.symbol) {
                    buttons[i]?.setBackgroundResource(R.drawable.tic_tac_toe_blank_field)
                }
            }
        }
    }

    private fun parseGameStateFromJSON(state: JSONObject): Array<String?> {
        return (JSONObject(state["response"] as String)["field"] as String).split(",")
            .toTypedArray()
    }

    //necessary function that have to be in all games ↓
    override fun quit() {
        runOnUiThread {
            //blocks the player from playing more
            result.text = getString(R.string.opponent_left)
            result.visibility = View.VISIBLE
            //-------------------------------------------------
            //change this function ↑ accordingly to archieve the same result
        }
    }

    override var mService: MusicService? = null


    override fun onResume() {
        var data : Data = object:Data{
            override val game: GameType
                get() = GameType.TIC_TAC_TOE

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

    override fun onPause() {
        Pinger.stop()
        super.onPause()
        mService?.pauseMusic()
    }

    override fun onBackPressed() {
        Pinger.stop()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}