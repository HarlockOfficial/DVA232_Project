package se.mdh.student.dva232.projectgroup7

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class TicTacToe : AppCompatActivity() {
    private val BUTTONS_COUNT = 9
    lateinit var data: TicTacToeData
    var buttons = arrayOfNulls<Button>(BUTTONS_COUNT)
    var isPlayersTurn = false
    var gameState: Array<String?> = arrayOfNulls<String>(BUTTONS_COUNT)
    lateinit var symbol: TicTacToeData.PlayersSymbol

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tic_tac_toe)
        isPlayersTurn = intent.getBooleanExtra("isStarting", false)
        symbol = if (isPlayersTurn) {
            TicTacToeData.PlayersSymbol.CIRCLE
        } else {
            TicTacToeData.PlayersSymbol.CROSS
        }

        // TODO do a catch clause
        data = TicTacToeData(symbol)

        // prepare clean game field
        gameState.fill("-")

        setupButtonsOnclicks()
        if (!isPlayersTurn) {
            GlobalScope.launch { waitForOpponentsMove() }
        }
    }

    private fun setupButtonsOnclicks() {
        val idPrefix = "field_"
        for (i in 1..BUTTONS_COUNT) {
            val id = resources.getIdentifier(idPrefix + i, "id", packageName)
            val button = findViewById<Button>(id)
            button.tag = i.toString()
            button.setOnClickListener { view ->
                run {
                    data.move = (view.tag as String).toInt()
                    println("Sending move " + data.move)
                    GlobalScope.launch { handleMove(CommunicationLayer.addPlayerMove(data)) }
                }
            }
            buttons[i - 1] = button
        }

    }

    private suspend fun handleMove(response: JSONObject) {
        if (response["response"] == "ok") {
            println("Received OK")
            gameState[data.move - 1] = symbol.symbol
            updateGameField()

            waitForOpponentsMove()
        } else if (response["response"] == "error") {
            // TODO show move is invalid
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
        return if (winner == "") {
            false
        } else if (winner == this.symbol) {
            // TODO visualize win
            true
        } else if (winner == "draw") {
            // TODO visualize draw
            true
        } else {
            // TODO visualize loss
            true
        }
    }

    private fun updateGameField() {
        runOnUiThread {
            for (i in 0 until BUTTONS_COUNT) {
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
}