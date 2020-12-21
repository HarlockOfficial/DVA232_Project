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

                        // Must implement interface ↓
class RockPaperScissors : AppCompatActivity(), ActivityInterface {
    private lateinit var myChoice: ImageView
    private lateinit var opponentChoice: ImageView
    private lateinit var context: Context
    private lateinit var result: TextView
    private val clickListener = View.OnClickListener { v ->
        GlobalScope.launch(Dispatchers.IO){
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
                    Log.e("eee", ret.toString())
                }while(ret.get("response")==null)   //TODO check, not sure if NULL is accepted as string like this
                when {
                    ret.getString("response") == "rock" -> {
                        runOnUiThread {
                            opponentChoice.setImageResource(R.mipmap.rock)
                        }
                        when (choice) {
                            "rock" -> {
                                runOnUiThread {
                                    result.text=getString(R.string.draw)
                                    result.visibility = View.VISIBLE
                                }
                            }
                            "paper" -> {
                                runOnUiThread {
                                    result.text=getString(R.string.win)
                                    result.visibility = View.VISIBLE
                                }
                            }
                            "scissors" -> {
                                runOnUiThread {
                                    result.text=getString(R.string.lose)
                                    result.visibility = View.VISIBLE
                                }
                            }
                            else -> {
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
                                runOnUiThread {
                                    result.text=getString(R.string.draw)
                                    result.visibility = View.VISIBLE
                                }
                            }
                            "scissors" -> {
                                runOnUiThread {
                                    result.text=getString(R.string.win)
                                    result.visibility = View.VISIBLE
                                }
                            }
                            "rock" -> {
                                runOnUiThread {
                                    result.text=getString(R.string.lose)
                                    result.visibility = View.VISIBLE
                                }
                            }
                            else -> {
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
                                runOnUiThread {
                                    result.text=getString(R.string.draw)
                                    result.visibility = View.VISIBLE
                                }
                            }
                            "rockr" -> {
                                runOnUiThread {
                                    result.text=getString(R.string.win)
                                    result.visibility = View.VISIBLE
                                }
                            }
                            "paper" -> {
                                runOnUiThread {
                                    result.text=getString(R.string.lose)
                                    result.visibility = View.VISIBLE
                                }
                            }
                            else -> {
                                runOnUiThread {
                                    Toast.makeText(context, context.getString(R.string.error_while_getting_move), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    else -> {
                        runOnUiThread {
                            Toast.makeText(context, context.getString(R.string.error_while_getting_move), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }else{
                runOnUiThread {
                    Toast.makeText(context, context.getString(R.string.error_while_sending_move), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rock_paper_scissors)

        // ---------------------------------------------------
        context = baseContext

        myChoice = findViewById(R.id.my_choice)
        opponentChoice = findViewById(R.id.opponent_choice)

        result = findViewById(R.id.result)
        result.setOnClickListener{
            startActivity(Intent(context, MainActivity::class.java))
        }

        findViewById<ImageView>(R.id.rock).setOnClickListener(clickListener)
        findViewById<ImageView>(R.id.paper).setOnClickListener(clickListener)
        findViewById<ImageView>(R.id.scissors).setOnClickListener(clickListener)
    }

    //necessary function that have to be in all games ↓
    override fun quit() {
        runOnUiThread {
            //blocks the player from playing more
            result.text=getString(R.string.opponent_left)
            result.visibility = View.VISIBLE
            //-------------------------------------------------
            //change this function ↑ accordingly to archieve the same result
        }
    }
    //this has to be the same
    override fun onPause() {
        Pinger.stop()
        super.onPause()
    }
    //here ↓ you have to change the data class to the correct one
    override fun onResume() {
        Pinger.changeContext(this, GameType.ROCK_PAPER_SCISSORS)
        super.onResume()
    }
}