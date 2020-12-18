package se.mdh.student.dva232.projectgroup7

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object Pinger {
    var current_activity: ActivityInterface? = null
    var current_data: Data? = null
    private var run: Boolean= true

    fun stop(){
        run = false
        current_data=null
        current_activity=null
    }

    fun start(){
        if(current_activity != null && current_data!=null){
            run=true
            GlobalScope.launch(Dispatchers.IO) {
                while (run) {
                    val response = CommunicationLayer.ping(current_data!!).getString("response")
                    if(response!="ok"){
                        run = false
                        current_activity!!.quit()
                    }
                }
            }
        }
        //if parameters are not correctly set before calling start, the function will terminate without errors, be careful
        stop()
    }
}