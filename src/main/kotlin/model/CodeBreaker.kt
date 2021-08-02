package model

class CodeBreaker: Player {
    override val name: String = "Code Breaker"
    var codePegs: List<Game.CodePegs> = listOf()
    fun guess(rawCodePegs: String) {
        codePegs = Game.buildCodePegs(rawCodePegs)
    }
}