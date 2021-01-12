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

class ResultScreen :
    AppCompatActivity() {                                     //Information about pop ups from : https://www.youtube.com/watch?v=fn5OlqQuOCk

    private lateinit var game: GameType
    private lateinit var result: MatchResult
    lateinit var quantity: String

    private var mService: MusicService? = null

    fun isBackgroundEnabled(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean(("switch_preference_music"), false)
    }

    private val connection = object : ServiceConnection {

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
        result = MatchResult.valueOf(intent.getStringExtra("RESULT")!!)
        if (game == GameType.DICES) {
            quantity = intent.getStringExtra("DICES_COUNT")!!
        }

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        window.setLayout((dm.widthPixels * 0.9).toInt(), (dm.heightPixels * 0.75).toInt())

        val startButton = findViewById<Button>(R.id.button_start)
        val backButton = findViewById<Button>(R.id.button_back)
        val header = findViewById<TextView>(R.id.name_header)
        val infoText = findViewById<TextView>(R.id.text_info)
        val image = findViewById<ImageView>(R.id.image_game)
        val field = findViewById<EditText>(R.id.field_numDices)
        val amntDicesText = findViewById<TextView>(R.id.text_amnt)

        field.visibility = View.GONE
        amntDicesText.visibility = View.GONE
        image.visibility = View.GONE
        infoText.visibility = View.GONE
        header.text = if (result == MatchResult.DRAW) {
            getString(R.string.draw)
        } else if (result == MatchResult.WIN) {
            getString(R.string.win)
        } else if (result == MatchResult.LOSE) {
            getString(R.string.lose)
        } else {
            getString(R.string.opponent_left)
        }

        startButton.text = getString(R.string.button_replay)
        backButton.text = getString(R.string.button_back_to_main)
        backButton.setOnClickListener {
            onBackPressed()
        }
        startButton.setOnClickListener {
            openWaitingRoom(game)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isBackgroundEnabled(applicationContext)) {
            val intent = Intent(this, MusicService::class.java)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
            startService(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        mService?.pauseMusic()
    }

    private fun openWaitingRoom(game: GameType) {
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