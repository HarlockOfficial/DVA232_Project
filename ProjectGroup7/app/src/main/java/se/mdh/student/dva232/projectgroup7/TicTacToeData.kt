package se.mdh.student.dva232.projectgroup7

class TicTacToeData(symbol: PlayersSymbol) : Data {
    override val game: GameType
        get() = GameType.TIC_TAC_TOE

    val FIELD_SIZE = 9
    var symbol: PlayersSymbol? = null

    var move = -1

    init {
        this.symbol = symbol
    }

    override fun moveToCsv(): String {
        return if ((symbol != null) && (move in 0..FIELD_SIZE)) {
            String.format("%d,%s", move, symbol!!.symbol)
        } else {
            ""
        }

    }

    enum class PlayersSymbol(val symbol: String) {
        CIRCLE("o"),
        CROSS("x"),
        EMPTY_FIELD("-")
    }
}