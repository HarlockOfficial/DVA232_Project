package se.mdh.student.dva232.projectgroup7

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class RockPaperScissors : AppCompatActivity(), ActivityInterface {
    override var mService: MusicService? = null

    private lateinit var myChoice: ImageView
    private lateinit var opponentChoice: ImageView
    private lateinit var context: Context
    private var isClicked: Boolean = false
    private val clickListener = View.OnClickListener { v ->
        if (isClicked) {
            return@OnClickListener
        }
        isClicked = true
        GlobalScope.launch(Dispatchers.IO) {
            val choice: String = v.tag as String
            val rpsData = RockPaperScissorsData(choice)
            if(CommunicationLayer.addPlayerMove(rpsData).getString("response") == "ok"){
                runOnUiThread {
                    val drawable = (v as ImageView).drawable
                    myChoice.setImageDrawable(drawable)
                }
                lateinit var ret: JSONObject
                do {
                    delay(10)
                    ret = CommunicationLayer.getOpponentMove(rpsData)
                }while(ret.get("response")==null)
                when {
                    ret.getString("response") == "rock" -> {
                        runOnUiThread {
                            opponentChoice.setImageResource(R.mipmap.rock)
                        }
                        when (choice) {
                            "rock" -> {
                                showGameResult(this@RockPaperScissors, GameType.ROCK_PAPER_SCISSORS, MatchResult.DRAW, own = R.mipmap.rock, opp = R.mipmap.rock)
                            }
                            "paper" -> {
                                showGameResult(this@RockPaperScissors, GameType.ROCK_PAPER_SCISSORS, MatchResult.WIN, own = R.mipmap.rock, opp = R.mipmap.paper)
                            }
                            "scissors" -> {
                                showGameResult(this@RockPaperScissors, GameType.ROCK_PAPER_SCISSORS, MatchResult.LOSE, own = R.mipmap.rock, opp = R.mipmap.scissors)
                            }
                            else -> {
                                // this should't happen
                                runOnUiThread {
                                    Toast.makeText(context, context.getString(R.string.error_while_getting_move), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    ret.getString("response") == "paper" -> {
                        runOnUiThread {
                            opponentChoice.setImageResource(R.mipmap.paper)
                        }
                        when (choice) {
                            "paper" -> {
                                showGameResult(this@RockPaperScissors, GameType.ROCK_PAPER_SCISSORS, MatchResult.DRAW, own = R.mipmap.paper, opp = R.mipmap.paper)
                            }
                            "scissors" -> {
                                showGameResult(this@RockPaperScissors, GameType.ROCK_PAPER_SCISSORS, MatchResult.WIN, own = R.mipmap.paper, opp = R.mipmap.scissors)
                            }
                            "rock" -> {
                                showGameResult(this@RockPaperScissors, GameType.ROCK_PAPER_SCISSORS, MatchResult.LOSE, own = R.mipmap.paper, opp = R.mipmap.rock)
                            }
                            else -> {
                                // this should't happen
                                runOnUiThread {
                                    Toast.makeText(context, context.getString(R.string.error_while_getting_move), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    ret.getString("response") == "scissors" -> {
                        runOnUiThread {
                            opponentChoice.setImageResource(R.mipmap.scissors)
                        }
                        when (choice) {
                            "scissors" -> {
                                showGameResult(this@RockPaperScissors, GameType.ROCK_PAPER_SCISSORS, MatchResult.DRAW,own = R.mipmap.scissors, opp = R.mipmap.scissors)
                            }
                            "rock" -> {
                                showGameResult(this@RockPaperScissors, GameType.ROCK_PAPER_SCISSORS, MatchResult.WIN, own = R.mipmap.scissors, opp = R.mipmap.rock)
                            }
                            "paper" -> {
                                showGameResult(this@RockPaperScissors, GameType.ROCK_PAPER_SCISSORS, MatchResult.LOSE, own = R.mipmap.scissors, opp = R.mipmap.paper)
                            }
                            else -> {
                                // this shouldn't happen
                                runOnUiThread {
                                    Toast.makeText(context, context.getString(R.string.error_while_getting_move), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    else -> {
                        // this shouldn't happen
                        runOnUiThread {
                            Toast.makeText(context, context.getString(R.string.error_while_getting_move), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }else{
                // this shouldn't happen
                runOnUiThread {
                    Toast.makeText(context, context.getString(R.string.error_while_sending_move), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rock_paper_scissors)
        supportActionBar?.hide()
        context = baseContext

        myChoice = findViewById(R.id.my_choice)
        opponentChoice = findViewById(R.id.opponent_choice)

        findViewById<ImageView>(R.id.rock).setOnClickListener(clickListener)
        findViewById<ImageView>(R.id.paper).setOnClickListener(clickListener)
        findViewById<ImageView>(R.id.scissors).setOnClickListener(clickListener)
    }

    //necessary function that have to be in all games ↓
    override fun quit() {
        showGameResult(this, GameType.ROCK_PAPER_SCISSORS, MatchResult.DISCONNECT)
    }

    //this has to be the same
    override fun onPause() {
        Pinger.stop()
        super.onPause()
        mService?.pauseMusic()
    }

    //here ↓ you have to change the data class to the correct one
    override fun onResume() {
        val data: Data = object : Data {
            override val game: GameType
                get() = GameType.ROCK_PAPER_SCISSORS

            override fun moveToCsv(): String {
                return ""
            }
        }
        Pinger.changeContext(this, data)
        super.onResume()
        if (isBackgroundEnabled(applicationContext)) {
            val intent = Intent(this, MusicService::class.java)
            bindService(intent, getConnection(), Context.BIND_AUTO_CREATE)
            startService(intent)
        }
    }

    override fun onBackPressed() {
        Pinger.stop()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}