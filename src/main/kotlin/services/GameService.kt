package services

import exceptions.GameException
import model.Game

class GameService(val game: Game) {
    companion object {
        fun check(codeBreakerPegs: List<Game.CodePegs>, codeMakerPegs: List<Game.CodePegs>): List<Game.KeyPegs> {
            if (codeBreakerPegs.size != codeMakerPegs.size)
                throw GameException("Length must be ${codeMakerPegs.size} to match Code Maker")
            val keyPegs = mutableListOf<Game.KeyPegs>()
            checkColorCodePegs(codeBreakerPegs, codeMakerPegs, keyPegs)
            keyPegs.sort()
            addEmptyPegs(keyPegs, codeBreakerPegs)

            return keyPegs
        }

        private fun addEmptyPegs(
            keyPegs: MutableList<Game.KeyPegs>,
            codeBreakerPegs: List<Game.CodePegs>
        ) {
            keyPegs.addAll((0 until codeBreakerPegs.size-keyPegs.size).map { Game.KeyPegs.EMPTY })
        }

        private fun checkColorCodePegs(
            codeBreakerPegs: List<Game.CodePegs>,

            codeMakerPegs: List<Game.CodePegs>,
            keyPegs: MutableList<Game.KeyPegs>
        ) {
            val uncomparedPegs = codeMakerPegs.toMutableList()
            for (i in codeBreakerPegs.indices) {
                val indexFoundedPeg = uncomparedPegs.indexOf(codeBreakerPegs[i])
                if (codeBreakerPegs[i] == codeMakerPegs[i]) {
                    addKeyPeg(keyPegs, uncomparedPegs, indexFoundedPeg, Game.KeyPegs.RED)
                } else if (indexFoundedPeg >= 0) {
                    addKeyPeg(keyPegs, uncomparedPegs, indexFoundedPeg, Game.KeyPegs.WHITE)
                }
            }
        }

        private fun addKeyPeg(
            keyPegs: MutableList<Game.KeyPegs>,
            uncomparedPegs: MutableList<Game.CodePegs>,
            indexFoundedPeg: Int,
            keyPeg: Game.KeyPegs
        ) {
            keyPegs.add(keyPeg)
            uncomparedPegs.removeAt(indexFoundedPeg)
        }
    }

    fun play(): String {
        game.goToNextRound()
        val keyPegs = check(game.codeBreaker.codePegs, game.codeMaker.codePegs)

        checkWinner(keyPegs)

        return Game.formatKeyPegs(keyPegs)
    }

    private fun checkWinner(keyPegs: List<Game.KeyPegs>) {
        if (keyPegs.all { it == Game.KeyPegs.RED }) {
            game.winner = game.codeBreaker
            game.isPlaying = false
        } else if (!game.isPlaying) game.winner = game.codeMaker
    }
}