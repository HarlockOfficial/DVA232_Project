package se.mdh.student.dva232.projectgroup7

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.*


/**
 * Order that should be used for functions call:
 * The existence of @variable data: Data is assumed
 *  (User in waiting for opponent room/page/frame/...)
 *  1. CommunicationLayer.addPlayerToMultiplayerQueue(data)
 *  2. CommunicationLayer.uuid (obtaining the valid UUID)
 *  3. if '1.' responded "in_queue" goto '4.'
 *  4. CommunicationLayer.checkMultiplayerQueue(data)
 *  5. if '4.' respond "in_queue" goto '4.' else goto '6.'
 *  (User in game page)
 *  6. CommunicationLayer.addPlayerMove(data)
 *  7. CommunicationLayer.getOpponentMove(data)
 *  8. (Valid only for "Tic Tac Toe")if the game is not over goto '6.'
 *
 *  @NOTE: UUID is rigenerated every time '1.' is done
 *  @NOTE 2: '6.' and '7.' order might depend on the starting player (if present), switch accordingly
 *  @NOTE 3: all game rooms are temporary, after the winner is decided, the game is over,
 *              impossible to play multiple times!!!
 *  @NOTE 4: server logic and bots for Flip A Coin, not implemented yet!! (Only grade 3 implemented)
 */

object CommunicationLayer {
    /**
     * UUID (Universal Unique IDentifier) public parameter containing the player UUID
     */
    var uuid = createUUID()
        private set
    private const val url: String = "https://dva232-project-group-7.000webhostapp.com/?player="

    /**
     * function used to add a player to the waiting queue should be called before the user reaches
     * the game screen
     * @param data: Data requires only the game name, not necessary to have a valid @function moveToCsv
     * @returns json object containing key "response", the value can be
     *         - "in_queue"
     *         - json object under the form of string (must be parsed before use) containing keys:
     *                  - "starting_player": UUID of starting player
     *                  - "field": the current game field if playing "Tic Tac Toe",
     *                              NULL otherwise
     * @NOTE: the json object is necessary only to the "Tic Tac Toe" game,
     *          for all the other games is only useful to understand that
     *          the game is started and the player is no more in the waiting queue
     */
    suspend fun addPlayerToMultiplayerQueue(data: Data): JSONObject {
        return withContext(Dispatchers.IO) {
            uuid = createUUID()
            return@withContext JSONObject(
                URL("$url$uuid&action=add_queue&game=${data.game.code}").readText()
            )
        }
    }
    /**
     * function used to check player queue status, should be called only if the previously called
     * @function addPlayerToMultiplayerQueue returned "in_queue".
     * Also this function has to be called before the user reaches the game screen
     * @param data: Data requires only the game name, not necessary to have a valid @function moveToCsv
     * @returns json object containing the key "response", the value can be
     *         - "in_queue"
     *         - json object under the form of string (must be parsed before use) containing keys:
     *                  - "starting_player": UUID of starting player
     *                  - "field": the current game field if playing "Tic Tac Toe",
     *                              NULL otherwise
     * @NOTE: the json object is necessary only to the "Tic Tac Toe" game,
     *          for all the other games is only useful to understand that
     *          the game is started and the player is no more in the waiting queue
     */
    suspend fun checkMultiplayerQueue(data: Data): JSONObject{
        return withContext(Dispatchers.IO) {
            return@withContext JSONObject(
                    URL("$url$uuid&action=get_queue&game=${data.game.code}").readText()
            )
        }
    }
    /**
     * function used to send to the server the move of the user
     * should be called only during the game
     * @param data: Data the complete "Data" object here the @function moveToCsv is needed!!!
     * @returns json object containing the key "response", the value can be
     *         - "ok"
     *         - error value (theoretically impossible), there are many error values,
     *                          all error values are sent as string,
     *                          you should not get errors, in case it happens, tell me
     *  Bonus: - just for dices, instead of "ok" this function returns the sum (integer value) of the dices rolled by the server
     * @NOTE: This function is only necessary to send the move,
     *          this is the reason because the return values are so "useless"
     */
    suspend fun addPlayerMove(data: Data):JSONObject {
        return withContext(Dispatchers.IO) {
            return@withContext JSONObject(
                    URL("$url$uuid&action=add_move&game=${data.game.code}&move=${data.moveToCsv()}").readText()
            )
        }
    }
    /**
     * function used to obtain the last move of the opponent (or the field for the "Tic Tac Toe" game)
     * should be called only during the game
     * @param data:Data requires only the game name, not necessary to have a valid @function moveToCsv
     * @returns json object containing the key "response", the value can be
     *         - NULL if there is no opponent move (not valid for "Tic Tac Toe")
     *         the other possible value depends on the game:
     *         - "Dices": the opponent sum (integer value) of the dices rolled by the server
     *         - "Rock Paper Scissors": the string "rock", "paper" or "scissors" based on the opponent choice
     *         - "Tic Tac Toe": a json object containing the keys:
     *                          - "field" with as a value the csv of the field
     *                                  (eg. empty field "-,-,-,-,-,-,-,-,-"
     *                                  eg. non-empty field "-,o,-,x,-,-,-,-,-")
     *                          - "winner" string empty unless a player wins, when the game is completed,
     *                                      the value could be "draw", "o" or "x"
     *                                      (eg. default -> value ""
     *                                      eg. draw match -> value "draw"
     *                                      eg. player using "x" wins -> value "x")
     * @NOTE for "Tic Tac Toe": the field is always returned, is up to the game implementation to check
     *                          (in app) if the field is changed,
     *                          this can also be used for the playing turns
     */
    suspend fun getOpponentMove(data: Data): JSONObject {
        return withContext(Dispatchers.IO) {
            return@withContext JSONObject(
                    URL("$url$uuid&action=get_move&game=${data.game.code}").readText()
            )
        }
    }

    /**
     * private function, non necessary for the games, used to generate a 20character UUID
     */
    private fun createUUID(): String{
        val alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_"
        return (1..20).map{alphabet.random()}.joinToString("")
    }
}