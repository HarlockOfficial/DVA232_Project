package se.mdh.student.dva232.projectgroup7

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.preference.PreferenceManager

interface ActivityInterface {

    var mService : MusicService?

    fun quit()

    fun isBackgroundEnabled(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean(("switch_preference_music"), false)
    }
    fun getConnection () : ServiceConnection {
         return object : ServiceConnection {

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
    }

    fun showGameResult(context: Context, gameType: GameType, matchResult: MatchResult, diceCount:Int = 0) {
        Pinger.stop()

        val intent = Intent(context, ResultScreen::class.java)
        intent.putExtra("GAME", gameType.code)
        intent.putExtra("RESULT", matchResult.value)
        intent.putExtra("DICES_COUNT", diceCount.toString())
        context.startActivity(intent)
    }
}