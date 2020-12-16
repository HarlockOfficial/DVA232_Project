package se.mdh.student.dva232.projectgroup7

class TicTacToeData : Data {
    override val game: GameType
        get() = GameType.TIC_TAC_TOE

    val FIELD_SIZE = 3
    var symbol: PlayersSymbol? = null
    var moveXCoordinate: Int = -1
    var moveYCoordinate: Int = -1

    override fun moveToCsv(): String {
        return if ((symbol != null) && (moveXCoordinate in 0..FIELD_SIZE) && (moveYCoordinate in 0..FIELD_SIZE)) {
            String.format("%d,%d,%s", moveXCoordinate, moveYCoordinate, symbol!!.symbol)
        } else {
            // TODO make check here or in the function above
            ""
        }

    }

    enum class PlayersSymbol(val symbol: String) {
        CIRCLE("o"),
        CROSS("x")
    }
}