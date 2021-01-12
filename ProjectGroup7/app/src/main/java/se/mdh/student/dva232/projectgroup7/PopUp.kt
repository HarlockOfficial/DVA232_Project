package se.mdh.student.dva232.projectgroup7

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class PopUp : AppCompatActivity() {                                     //Information about pop ups from : https://www.youtube.com/watch?v=fn5OlqQuOCk

   lateinit var game: GameType
    lateinit var quantity: String

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
        window.setLayout((dm.widthPixels*0.9).toInt(), (dm.heightPixels*0.6).toInt())

        val startButton = findViewById<Button>(R.id.button_start)
        findViewById<Button>(R.id.button_back).setOnClickListener {
            onBackPressed()
        }
        val header = findViewById<TextView>(R.id.name_header)
        val infoText = findViewById<TextView>(R.id.text_info)
        val image = findViewById<ImageView>(R.id.image_game)
        val field = findViewById<EditText>(R.id.field_numDices)
        val amntDicesText = findViewById<TextView>(R.id.text_amnt)

        field.visibility = View.GONE
        amntDicesText.visibility = View.GONE

        if (game == GameType.BLOW) {
            header.text = getString(R.string.blowing_game_name)
            infoText.text = getString(R.string.blowing_game_desc)
            image.setImageResource(R.drawable.ic_tumbleweed)

            startButton.setOnClickListener {
                openWaitingRoom(GameType.BLOW)
            }
        } else if (game == GameType.DICES) {                    //Set Dices amount
            header.text = getString(R.string.dices_name)
            infoText.text = getString(R.string.dices_desc)
            image.setImageResource(R.drawable.ic_dice)         //set image, look up
            field.visibility = View.VISIBLE
            amntDicesText.visibility = View.VISIBLE

            startButton.setOnClickListener {
                quantity = field.text.toString()
                openWaitingRoom(GameType.DICES)
            }
        }  else if (game == GameType.TIC_TAC_TOE) {
            header.text = getString(R.string.tic_tac_toe_name)
            infoText.text = getString(R.string.tic_tac_toe_desc)
            image.setImageResource(R.mipmap.ttt_round)         //set image, look up

            startButton.setOnClickListener {
                openWaitingRoom(GameType.TIC_TAC_TOE)
            }
        } else if (game == GameType.ROCK_PAPER_SCISSORS) {
            header.text = getString(R.string.rock_pape_scissors_name)
            infoText.text = getString(R.string.rock_paper_scissors_desc)
            image.setImageResource(R.mipmap.rps_round)

            startButton.setOnClickListener {
                openWaitingRoom(GameType.ROCK_PAPER_SCISSORS)
            }
        } else if (game == GameType.FLIP_A_COIN) {
            header.text = getString(R.string.flip_a_coin_name)
            infoText.text = getString(R.string.flip_a_coin_desc)
            image.setImageResource(R.drawable.ic_shiny_coin3)

            startButton.setOnClickListener {
                val intent3 = Intent(this, FlipACoinActivity::class.java)
                startActivity(intent3)
            }
        } else {
            header.text = getString(R.string.unknown_game_error_name)
            infoText.text = getString(R.string.unknown_game_error_desc)

            startButton.setOnClickListener {
                openWaitingRoom(GameType.FLIP_A_COIN)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(isBackgroundEnabled(applicationContext)){
            val intent =  Intent(this, MusicService::class.java)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
            startService(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        mService?.pauseMusic()
    }

    private fun openWaitingRoom(game: GameType){
        val intent = Intent(this, WaitingRoom::class.java)
        intent.putExtra("GAME_CODE", game.name)
        if (game == GameType.DICES) {
            intent.putExtra("DICE_QUANTITY", quantity)
        }
        startActivity(intent)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}