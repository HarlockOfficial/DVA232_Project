package se.mdh.student.dva232.projectgroup7

class BlowData (private val temp: Int) : Data{ //Also temporary. Will contain the audio output transformed into a value. This will be compared against the opponents value.
    override val game: GameType
        get() = GameType.BLOW


    override fun moveToCsv(): String {
        return temp.toString()
    }
}