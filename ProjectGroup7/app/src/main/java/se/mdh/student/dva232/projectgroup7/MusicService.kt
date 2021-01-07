package se.mdh.student.dva232.projectgroup7
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder


class MusicService : Service() {

    private lateinit var player: MediaPlayer

    private var length = 0
    private val mBinder: IBinder = ServiceBinder()


    inner class ServiceBinder : Binder() {
        fun getService() : MusicService = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder? {

        return mBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        player = MediaPlayer.create(this, R.raw.bkgrdmusic)
        player.isLooping = true
        player.setVolume(1f,1f)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        player.start()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
        player.release()
    }


   fun pauseMusic(){
        if(player.isPlaying)
        {
            player.pause()
            length= player.currentPosition
        }
    }

    fun resumeMusic() {
        if(!player.isPlaying)
        {
            player.seekTo(length);
            player.start();
        }
    }

    fun stopMusic() {
        player.stop()
        player.release();
    }
}