package se.mdh.student.dva232.projectgroup7

class DicesData(val amountOfDices: Int) : Data {
    override val game: GameType
        get() = GameType.DICES


    override fun moveToCsv(): String {
        return amountOfDices.toString()
    }

}