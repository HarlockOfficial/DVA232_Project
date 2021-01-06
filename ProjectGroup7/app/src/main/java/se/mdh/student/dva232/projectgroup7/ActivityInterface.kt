package se.mdh.student.dva232.projectgroup7

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import androidx.preference.PreferenceManager

interface ActivityInterface {
    fun quit()

    var mService : MusicService?

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

}