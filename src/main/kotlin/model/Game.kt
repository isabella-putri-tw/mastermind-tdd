package model

import exceptions.GameException

class Game(val codeMaker: CodeMaker, val codeBreaker: CodeBreaker) {
    var round = 0
    var isPlaying = true
    var winner: Player? = null

    enum class CodePegs(val code: Char) {
        YELLOW('Y'), GREEN('G'), ORANGE('O'), RED('R'), BLACK('B'), WHITE('W')
    }

    enum class KeyPegs(val code: Char) {
        EMPTY('-'), RED('R'), WHITE('W')
    }

    companion object {
        private val CODE_PEGS_CODES = CodePegs.values().map { it.code }
        const val MAX_ROUND = 10
        fun buildCodePegs(string: String): List<CodePegs> {
            if (string.isEmpty())
                throw GameException("String shouldn't be empty")
            if (string.length != 4) throw GameException("Length must be 4 characters")

            return string.map { c: Char ->
                if (CODE_PEGS_CODES.none { it == c })
                    throw GameException("Code color is unacceptable, only ([Y]ellow, [O]range, [G]reen, [R]ed, [B]lack, and [W]hite)")
                CodePegs.values().first { it.code == c }
            }
        }

        fun formatKeyPegs(keyPegs: List<KeyPegs>): String {

            return keyPegs.map { it.code }.joinToString("")
        }
    }

    fun goToNextRound() {
        if (!isPlaying) throw GameException("Game has finished")
        round++
        if (round == MAX_ROUND) isPlaying = false

    }
}