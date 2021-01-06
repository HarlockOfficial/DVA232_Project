package se.mdh.student.dva232.projectgroup7

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.preference.PreferenceManager
import org.w3c.dom.Text

class PopUp : AppCompatActivity() {                                     //Information about pop ups from : https://www.youtube.com/watch?v=fn5OlqQuOCk

   lateinit var game: GameType

    private var mService : MusicService? = null

    fun isBackgroundEnabled(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean(("switch_preference_music"), false)
    }
        private val connection =  object : ServiceConnection {

            private var mBound: Boolean = false

            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                val binder = service as MusicService.ServiceBinder
                mService = binder.getService()
                mBound = true
            }

            override fun onServiceDisconnected(arg0: ComponentName) {
                mBound = false
            }
        }


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
            //image                 set image, look up

            startButton.setOnClickListener {
                openWaitingRoom(GameType.BLOW)
            }
        } else if (game == GameType.DICES) {                    //Set Dices amount

            header.text = "DICE GAME"
            infoText.text = "This is the dice game temporary info text"
            //image                 set image, look up
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

    override fun onResume() {
        super.onResume()
        if(isBackgroundEnabled(applicationContext)){
            //startService(Intent(this, MusicService::class.java))
            val intent =  Intent(this, MusicService::class.java)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
            startService(intent)
            //mService?.resumeMusic()

        }

    }

    override fun onPause() {
        super.onPause()
        mService?.pauseMusic()
        //stopService(Intent(this, MusicService::class.java))
    }

    private fun openWaitingRoom(game: GameType){
        val intent = Intent(this, WaitingRoom::class.java)
        intent.putExtra("GAME_CODE", game.name)
        startActivity(intent)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}