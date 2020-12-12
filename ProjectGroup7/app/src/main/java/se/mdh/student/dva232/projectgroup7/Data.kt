package se.mdh.student.dva232.projectgroup7

import org.json.JSONObject

interface Data {
    val game: GameType

    /*
     * function only used when add player move is called
     * for rock paper scissors "<selected movement>" eg "rock"
     * for dices "<dices quantity>"} eg "6"
     * for tic tac toe "<0-based x coordinate>,<0-based y coordinate>,<symbol> eg "0,2,o"
     */
    fun moveToCsv(): JSONObject
}