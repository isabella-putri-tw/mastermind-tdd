package services

import exceptions.GameException
import model.CodeBreaker
import model.CodeMaker
import model.Game
import java.io.BufferedReader
import java.io.InputStreamReader

class GameConsoleService {
    var reader = BufferedReader(InputStreamReader(System.`in`))
    private lateinit var gameService: GameService
    private lateinit var game: Game

    fun start() {
        game = Game(CodeMaker(), CodeBreaker())
        gameService = GameService(game)
        printWelcome()
        printSeparator()
        printRules()
        printStart()
        printSeparator()
        askCodeMaker()
        printPageChange()
        askCodeBreaker()
    }

    private fun askCodeMaker() {
        var answered = false
        do {
            try {
                println("Code maker, what is your Code Pegs sequence (Length: 4 | Colors: [Y]ellow, [O]range, [G]reen, [R]ed, [B]lack, and [W]hite)?")
                game.codeMaker.setCodePegs(reader.readLine())
                answered = true
            } catch (ex: GameException) {
                println(ex.message)
            }
        } while (!answered)
    }

    private fun askCodeBreaker() {
        println("Code breaker, it time for you to guess!!")
        do {
            try {
                println("What is your guess (Length: 4 | Colors: [Y]ellow, [O]range, [G]reen, [R]ed, [B]lack, and [W]hite)?")
                val raw = reader.readLine()
                game.codeBreaker.guess(raw)
                val keyPegs = gameService.play()
                println("Round ${game.round}: $keyPegs")
                if (game.winner != null) {
                    println("${game.winner?.name} WIN!!!")
                }
            } catch (ex: GameException) {
                println(ex.message)
            }
        } while (game.isPlaying)

    }

    private fun printRules() {
        println(
            """
                There are 2 players in this game, which is Code Maker and Code Breaker.
                Code Maker must create a sequence of 4 code pegs.
                Afterwards, Code Breaker must guess correctly to win within 10 rounds.
                The available Code Peg colors: [Y]ellow, [O]range, [G]reen, [R]ed, [B]lack, and [W]hite
                
                If the guess of a code peg is correct in the correct position, the displayed key peg will be [R]ed.
                If the guess of a code peg is correct but the position is wrong, the displayed key peg will be [W]hite.
                All incorrect code peg will be leave empty.
                Key pegs will be sorted from [R]ed, [W]hite, and lastly [-] empty
                
                Here are an example:
                
                Code Maker: YOGB
                Code Breaker: OWGR
                Result Key Peg: RW--
                
                Explanation: [O]range is correct but wrong position, [W] is incorrect, [G] is correct, [R]ed is incorrect
                
            """.trimIndent()
        )
    }

    private fun printStart() {
        println("Let's start the game!")

    }

    private fun printWelcome() {
        println("Welcome to Mastermind Game!")
    }

    private fun printSeparator() {
        println("".padStart(50, '-'))
    }
    private fun printPageChange() {
        for (i in 0 until 25) println("|")
    }
}