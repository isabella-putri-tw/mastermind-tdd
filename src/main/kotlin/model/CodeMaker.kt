package model

class CodeMaker(): Player {
    override val name: String = "Code Maker"
    var codePegs: List<Game.CodePegs> = listOf()
    fun setCodePegs(rawCodePegs: String) {
        codePegs = Game.buildCodePegs(rawCodePegs)
    }
}