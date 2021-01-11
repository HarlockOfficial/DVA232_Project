package se.mdh.student.dva232.projectgroup7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.preference.PreferenceManager


// TODO: guide how to make Hamburger Menu -> https://github.com/codepath/android_guides/wiki/Fragment-Navigation-Drawer
class MainActivity : AppCompatActivity() {

    //---------------------------------------------------------------------------------------//
    private var mService: MusicService? = null
    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicService.ServiceBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }
    //---------------------------------------------------------------------------------------//

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(isBackgroundEnabled(applicationContext)){
            //startService(Intent(this, MusicService::class.java))
            val intent =  Intent(this, MusicService::class.java)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
            startService(intent)
            //mService?.resumeMusic()

        }

        findViewById<Button>(R.id.open_RPS).setOnClickListener {
            //openWaitingRoom(GameType.ROCK_PAPER_SCISSORS)
            openPop(GameType.ROCK_PAPER_SCISSORS)
        }

        findViewById<Button>(R.id.open_Dices).setOnClickListener {
            //openWaitingRoom(GameType.DICES)
            openPop(GameType.DICES)

        }
        findViewById<Button>(R.id.open_tic_tac_toe).setOnClickListener {
            //openWaitingRoom(GameType.TIC_TAC_TOE)
            openPop(GameType.TIC_TAC_TOE)
        }

        findViewById<Button>(R.id.open_blow).setOnClickListener {
            //val intent2 = Intent(this, BlowActivity::class.java) //Only for now
            //startActivity(intent2)
            openPop(GameType.BLOW)
        }
        findViewById<Button>(R.id.open_flip_a_coin).setOnClickListener {
            val intent3 = Intent(this, FlipACoinActivity::class.java) //Only for now
            startActivity(intent3)
            //openWaitingRoom(GameType.BLOW)
        }
        // TODO: after adding a open_flip_a_coin, give the open_flip_a_coin an ID and do like ↑
    }

    private fun openPop(game: GameType){
        val intent = Intent(this, PopUp::class.java)
        intent.putExtra("GAME", game.name)
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        if(mBound) {
            unbindService(connection)
            mBound = false
        }

    }
    //---------------------------------------------------------------------------------------//

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

    fun isBackgroundEnabled(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean(("switch_preference_music"), false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menuitem, menu)
        return true
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                this.startActivity(Intent(this, Settings::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}