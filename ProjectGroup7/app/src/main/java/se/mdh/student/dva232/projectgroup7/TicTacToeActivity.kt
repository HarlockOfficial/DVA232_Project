package se.mdh.student.dva232.projectgroup7

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import se.mdh.student.dva232.projectgroup7.CommunicationLayer.addPlayerMove

class TicTacToeActivity : AppCompatActivity() {
    lateinit var data: TicTacToeData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tic_tac_toe)

        data = TicTacToeData()

        GlobalScope.launch {
            val response = CommunicationLayer.addPlayerToMultiplayerQueue(data)
            setSymbolFromResponse(response)
        }

        setupButtonsOnclicks()
    }

    // TODO question if it is going to be string or JSON
    private fun setSymbolFromResponse(response: String) {
        // just to test
        this.data.symbol = TicTacToeData.PlayersSymbol.CIRCLE
    }

    private fun setupButtonsOnclicks() {
        // Terible solution, do not merge.. just to test if that works
        findViewById<Button>(R.id.field_0).setOnClickListener {
            data.moveXCoordinate = 0
            data.moveYCoordinate = 0

            GlobalScope.launch { handleMove(addPlayerMove(data)) }
        }
        findViewById<Button>(R.id.field_1).setOnClickListener {
            data.moveXCoordinate = 1
            data.moveYCoordinate = 0

            GlobalScope.launch { handleMove(addPlayerMove(data)) }
        }
        findViewById<Button>(R.id.field_2).setOnClickListener {
            data.moveXCoordinate = 2
            data.moveYCoordinate = 0

            GlobalScope.launch { handleMove(addPlayerMove(data)) }
        }
        findViewById<Button>(R.id.field_3).setOnClickListener {
            data.moveXCoordinate = 0
            data.moveYCoordinate = 1

            GlobalScope.launch { handleMove(addPlayerMove(data)) }
        }
        findViewById<Button>(R.id.field_4).setOnClickListener {
            data.moveXCoordinate = 1
            data.moveYCoordinate = 1
            GlobalScope.launch { handleMove(addPlayerMove(data)) }
        }
        findViewById<Button>(R.id.field_5).setOnClickListener {
            data.moveXCoordinate = 2
            data.moveYCoordinate = 1
            GlobalScope.launch { handleMove(addPlayerMove(data)) }
        }
        findViewById<Button>(R.id.field_6).setOnClickListener {
            data.moveXCoordinate = 0
            data.moveYCoordinate = 2
            GlobalScope.launch { handleMove(addPlayerMove(data)) }
        }
        findViewById<Button>(R.id.field_7).setOnClickListener {
            data.moveXCoordinate = 1
            data.moveYCoordinate = 2
            GlobalScope.launch { handleMove(addPlayerMove(data)) }
        }
        findViewById<Button>(R.id.field_8).setOnClickListener {
            data.moveXCoordinate = 2
            data.moveYCoordinate = 2

            GlobalScope.launch { handleMove(addPlayerMove(data)) }
        }

    }

    private fun handleMove(move: JSONObject) {
        // TODO parse JSON
        val a = 0
        // TODO do according action
        // TODO show move is invalid

        // TODO do the move
    }
}