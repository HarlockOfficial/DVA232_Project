package se.mdh.student.dva232.projectgroup7

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object Pinger {
    var currentActivity: ActivityInterface? = null
    var currentData: Data? = null
    private var run: Boolean= true
    var isPlayerAdded = false

    fun stop() {
        run = false
        currentActivity=null
    }

    fun start(){
        if(currentActivity != null && currentData!=null){
            GlobalScope.launch(Dispatchers.IO) {
                run = true
                while (!isPlayerAdded) {
                    delay(100)
                }

                while (run) {
                    try {
                        val response = CommunicationLayer.ping(currentData!!).getString("response")
                        if(response != "ok") {
                            run = false
                            currentActivity!!.quit()
                        }
                        delay(4000)
                    } catch (e: java.lang.NullPointerException) {
                        delay(1000)
                        continue
                    } catch (e: org.json.JSONException) {
                        delay(1000)
                    }
                }
            }
        }
        //if parameters are not correctly set before calling start, the function will terminate without errors, be careful
        stop()
    }

    fun changeContext(newContext: ActivityInterface, data: Data) {
        this.stop()
        this.currentActivity = newContext
        currentData = data
        this.start()
    }
}