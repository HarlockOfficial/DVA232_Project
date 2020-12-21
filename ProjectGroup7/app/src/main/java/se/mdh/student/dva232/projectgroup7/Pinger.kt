package se.mdh.student.dva232.projectgroup7

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object Pinger {
    var currentActivity: ActivityInterface? = null
    var currentData: Data? = null
    private var run: Boolean= true

    fun stop(){
        run = false
        currentData=null
        currentActivity=null
    }

    fun start(){
        if(currentActivity != null && currentData!=null){
            run=true
            GlobalScope.launch(Dispatchers.IO) {
                while (run) {
                    val response = CommunicationLayer.ping(currentData!!).getString("response")
                    if(response!="ok"){
                        run = false
                        currentActivity!!.quit()
                    }
                }
            }
        }
        //if parameters are not correctly set before calling start, the function will terminate without errors, be careful
        stop()
    }
}