package se.mdh.student.dva232.projectgroup7


interface Data {
    /**
     * public variable containing the "GameType"
     */
    val game: GameType

    /**
     * function only used when add player move is called
     * @returns based on the game:
     *                  - "Rock Paper Scissors": "<selected movement>" (eg "rock")
     *                  - "Dices": "<dices quantity>" (eg "6")
     *                  - "Tic Tac Toe": "<position from 1 to 9>,<symbol> (eg "0,o")
     * @Note for "Tic Tac Toe": the position should refer to the following
     *                              1 | 2 | 3
     *                             -----------
     *                              4 | 5 | 6
     *                             -----------
     *                              7 | 8 | 9
     */
    fun moveToCsv(): String
}