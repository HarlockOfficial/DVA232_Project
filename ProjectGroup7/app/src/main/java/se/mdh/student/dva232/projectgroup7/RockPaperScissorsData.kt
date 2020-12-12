package se.mdh.student.dva232.projectgroup7

class RockPaperScissorsData(private val move: String): Data {
    override val game: GameType
        get() = GameType.ROCK_PAPER_SCISSORS

    override fun moveToCsv(): String {
        return move
    }

}