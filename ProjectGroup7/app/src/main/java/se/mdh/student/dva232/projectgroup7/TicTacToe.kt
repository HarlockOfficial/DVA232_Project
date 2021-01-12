package se.mdh.student.dva232.projectgroup7

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    var gameState: Array<String?> = arrayOfNulls(buttonsCount)
    lateinit var symbol: TicTacToeData.PlayersSymbol

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tic_tac_toe)
        supportActionBar?.hide()

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
            gameState[data.move - 1] = symbol.symbol
            updateGameField()

            waitForOpponentsMove()
        } else {
            runOnUiThread{
                Toast.makeText(baseContext, R.string.error_while_sending_move, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun waitForOpponentsMove() {
        var response = CommunicationLayer.getOpponentMove(data)
        var serverGameState: Array<String?> = parseGameStateFromJSON(response)

        // if this player wins, this should end the game
        // the other player needs move first to win, so it will go though the loop
        if (isEndOfGame(response)) {
            return
        }

        while (gameState.contentEquals(serverGameState)) {
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
            showGameResult(this, GameType.TIC_TAC_TOE, MatchResult.WIN)
            return true
        } else if (winner == "draw") {
            showGameResult(this, GameType.TIC_TAC_TOE, MatchResult.DRAW)
            return true
        } else {
            showGameResult(this, GameType.TIC_TAC_TOE, MatchResult.LOSE)
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
        showGameResult(this, GameType.TIC_TAC_TOE, MatchResult.DISCONNECT)
    }

    override var mService: MusicService? = null

    override fun onResume() {
        val data : Data = object:Data{
            override val game: GameType
                get() = GameType.TIC_TAC_TOE

            override fun moveToCsv(): String {
                return ""
            }
        }
        Pinger.changeContext(this, data)
        super.onResume()
        if(isBackgroundEnabled(applicationContext)){
            val intent =  Intent(this, MusicService::class.java)
            bindService(intent, getConnection(), Context.BIND_AUTO_CREATE)
            startService(intent)
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