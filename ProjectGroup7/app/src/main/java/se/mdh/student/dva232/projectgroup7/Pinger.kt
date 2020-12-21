package se.mdh.student.dva232.projectgroup7

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore

object Pinger {
    var current_activity: ActivityInterface? = null
    var current_data: Data? = null
    private var run: Boolean = true
    var isPlayerAdded = false

    fun stop() {
        run = false
        current_activity = null
    }

    fun start() {
        Log.e("Pinger", "Pinger started " + current_activity.toString())
        if (current_activity != null && current_data != null) {
            Log.e("Pinger", "Pinger entered THE IF")
            GlobalScope.launch(Dispatchers.IO) {
                run = true
                while (!isPlayerAdded) {
                    delay(100)
                }

                while (run) {
                    try {
                        Log.e("Pinger", "trying to get response ${(current_data as Data).game.code}")
                        val response = CommunicationLayer.ping(current_data!!).getString("response")
                        Log.e("Pinger", "response: $response")
                        if (response != "ok") {
                            run = false
                            current_activity!!.quit()
                        }
                        delay(1000)
                    } catch (e: java.lang.NullPointerException) {
                        delay(100)
                        continue
                    }
                }
            }
        }
        //if parameters are not correctly set before calling start, the function will terminate without errors, be careful
        stop()
    }

    fun changeContext(newContext: ActivityInterface, gameType: GameType) {
        this.stop()
        this.current_activity = newContext
        this.current_data = object : Data {
            override val game: GameType
                get() = gameType

            override fun moveToCsv(): String {
                return ""
            }
        }
        Log.e("Pinger", "Asigned data: " + (this.current_data as Data).game.code)
        Log.e("Pinger", "Asigned game type: " + (this.current_data as Data).game.code)
        this.start()
    }
}