package se.mdh.student.dva232.projectgroup7

class FlipACoinFakeData: Data {
    override val game: GameType
        get() = GameType.FLIP_A_COIN

    override fun moveToCsv(): String {
        return ""
    }
}