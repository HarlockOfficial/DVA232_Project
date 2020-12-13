package se.mdh.student.dva232.projectgroup7

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.UUID

object CommunicationLayer {
    private val url: String = "https://dva232-project-group-7.000webhostapp.com/?player="+UUID.randomUUID().toString()

    suspend fun addPlayerToMultiplayerQueue(data: Data): JSONObject {
        return withContext(Dispatchers.IO) {
            return@withContext JSONObject(
                URL("$url&action=add_queue&game=${data.game.name}").readText()
            )
        }
    }
    suspend fun checkMultiplayerQueue(data: Data): JSONObject{
        return withContext(Dispatchers.IO) {
            return@withContext JSONObject(
                    URL("$url&action=get_queue&game=${data.game.name}").readText()
            )
        }
    }
    suspend fun addPlayerMove(data: Data):JSONObject {
        return withContext(Dispatchers.IO) {
            return@withContext JSONObject(
                    URL("$url&action=add_move&game=${data.game.name}&move=${data.moveToCsv()}").readText()
            )
        }
    }
    suspend fun getOpponentMove(data: Data): JSONObject {
        return withContext(Dispatchers.IO) {
            return@withContext JSONObject(
                    URL("$url&action=get_move&game=${data.game.name}").readText()
            )
        }
    }
}