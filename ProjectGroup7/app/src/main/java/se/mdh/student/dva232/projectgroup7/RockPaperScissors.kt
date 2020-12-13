package se.mdh.student.dva232.projectgroup7

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class RockPaperScissors : AppCompatActivity() {
    private lateinit var myChoice: ImageView
    private lateinit var opponentChoice: ImageView
    private lateinit var context: Context
    private val clickListener = View.OnClickListener { v ->
        GlobalScope.launch(Dispatchers.IO){
            val choice: String = v.tag as String
            val rpsData = RockPaperScissorsData(choice)
            var ret:JSONObject = CommunicationLayer.addPlayerMove(rpsData)
            if(ret.getString("response") == "ok"){
                runOnUiThread {
                    val drawable = (v as ImageView).drawable
                    myChoice.setImageDrawable(drawable)
                }
                do {
                    delay(10)
                    ret = CommunicationLayer.getOpponentMove(rpsData)
                }while(ret.getString("response")=="in_queue")
                when {
                    ret.getString("response") == "rock" -> {
                        runOnUiThread {
                            opponentChoice.setImageResource(R.mipmap.rock)
                        }
                        when (choice) {
                            "rock" -> {
                                runOnUiThread {
                                    Toast.makeText(context, context.getString(R.string.draw), Toast.LENGTH_SHORT).show()
                                }
                            }
                            "paper" -> {
                                runOnUiThread {
                                    Toast.makeText(context, context.getString(R.string.win), Toast.LENGTH_SHORT).show()
                                }
                            }
                            "scissors" -> {
                                runOnUiThread {
                                    Toast.makeText(context, context.getString(R.string.lose), Toast.LENGTH_SHORT).show()
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
                                    Toast.makeText(context, context.getString(R.string.draw), Toast.LENGTH_SHORT).show()
                                }
                            }
                            "scissors" -> {
                                runOnUiThread {
                                    Toast.makeText(context, context.getString(R.string.win), Toast.LENGTH_SHORT).show()
                                }
                            }
                            "rock" -> {
                                runOnUiThread {
                                    Toast.makeText(context, context.getString(R.string.lose), Toast.LENGTH_SHORT).show()
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
                                    Toast.makeText(context, context.getString(R.string.draw), Toast.LENGTH_SHORT).show()
                                }
                            }
                            "rockr" -> {
                                runOnUiThread {
                                    Toast.makeText(context, context.getString(R.string.win), Toast.LENGTH_SHORT).show()
                                }
                            }
                            "paper" -> {
                                runOnUiThread {
                                    Toast.makeText(context, context.getString(R.string.lose), Toast.LENGTH_SHORT).show()
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

        context = this.baseContext

        myChoice = findViewById(R.id.my_choice)
        opponentChoice = findViewById(R.id.opponent_choice)

        findViewById<ImageView>(R.id.rock).setOnClickListener(clickListener)
        findViewById<ImageView>(R.id.paper).setOnClickListener(clickListener)
        findViewById<ImageView>(R.id.scissors).setOnClickListener(clickListener)
    }
}