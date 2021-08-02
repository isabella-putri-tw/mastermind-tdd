package services

import assertk.assertThat
import assertk.assertions.*
import exceptions.GameException
import model.CodeBreaker
import model.CodeMaker
import model.Game
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import model.Game.CodePegs.*
import model.Game.KeyPegs.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertFailsWith

internal class GameServiceTest {

    @Nested
    inner class Check() {
        @Test
        fun `should return empty when all colors are mismatch`() {
            val keyPegs = GameService.check(listOf(Game.CodePegs.WHITE, Game.CodePegs.WHITE, BLACK, BLACK), listOf(YELLOW, GREEN, ORANGE, Game.CodePegs.RED))
            assertEquals(listOf(EMPTY, EMPTY, EMPTY, EMPTY), keyPegs)
        }

        @Test
        fun `should throw exception if the length doesn't match`() {
            val exception = assertFailsWith(GameException::class) {
                GameService.check(listOf(BLACK, BLACK), listOf(YELLOW, GREEN, ORANGE, Game.CodePegs.RED))
            }
            assertEquals("Length must be 4 to match Code Maker", exception.message)
        }

        @Test
        fun `should return red pegs when there's correct guess and other are incorrect colors`() {
            assertEquals(listOf(Game.KeyPegs.RED, EMPTY, EMPTY, EMPTY),
                GameService.check(
                    listOf(Game.CodePegs.WHITE, GREEN, BLACK, BLACK),
                    listOf(YELLOW, GREEN, ORANGE, Game.CodePegs.RED)))

            assertEquals(listOf(Game.KeyPegs.RED, Game.KeyPegs.RED, EMPTY, EMPTY),
                GameService.check(
                    listOf(YELLOW, GREEN, BLACK, BLACK),
                    listOf(YELLOW, GREEN, ORANGE, Game.CodePegs.RED)))

            assertEquals(listOf(Game.KeyPegs.RED, Game.KeyPegs.RED, Game.KeyPegs.RED, EMPTY),
                GameService.check(
                    listOf(Game.CodePegs.WHITE, Game.CodePegs.WHITE, BLACK, Game.CodePegs.RED),
                    listOf(Game.CodePegs.WHITE, Game.CodePegs.WHITE, ORANGE, Game.CodePegs.RED)))

            assertEquals(listOf(Game.KeyPegs.RED, Game.KeyPegs.RED, Game.KeyPegs.RED, Game.KeyPegs.RED),
                GameService.check(
                    listOf(Game.CodePegs.WHITE, Game.CodePegs.WHITE, ORANGE, Game.CodePegs.RED),
                    listOf(Game.CodePegs.WHITE, Game.CodePegs.WHITE, ORANGE, Game.CodePegs.RED)))
        }

        @Test
        fun `should return white pegs when there's correct guess but incorrect location`() {
            assertEquals(listOf(Game.KeyPegs.WHITE, EMPTY, EMPTY, EMPTY),
                GameService.check(
                    listOf(Game.CodePegs.WHITE, BLACK, BLACK, GREEN),
                    listOf(YELLOW, GREEN, ORANGE, Game.CodePegs.RED)))

            assertEquals(listOf(Game.KeyPegs.WHITE, Game.KeyPegs.WHITE, EMPTY, EMPTY),
                GameService.check(
                    listOf(Game.CodePegs.WHITE, ORANGE, Game.CodePegs.WHITE, GREEN),
                    listOf(YELLOW, GREEN, ORANGE, Game.CodePegs.RED)))

            assertEquals(listOf(Game.KeyPegs.WHITE, Game.KeyPegs.WHITE, Game.KeyPegs.WHITE, EMPTY),
                GameService.check(
                    listOf(ORANGE, ORANGE, Game.CodePegs.WHITE, GREEN),
                    listOf(YELLOW, GREEN, ORANGE, ORANGE)))

            assertEquals(listOf(Game.KeyPegs.WHITE, Game.KeyPegs.WHITE, Game.KeyPegs.WHITE, Game.KeyPegs.WHITE),
                GameService.check(
                    listOf(ORANGE, ORANGE, YELLOW, GREEN),
                    listOf(YELLOW, GREEN, ORANGE, ORANGE)))
        }

        @Test
        fun `should return white and red peg when there's correct guess with correct and incorrect location`() {
            assertEquals(listOf(Game.KeyPegs.RED, Game.KeyPegs.WHITE, EMPTY, EMPTY),
                GameService.check(
                    listOf(YELLOW, BLACK, BLACK, GREEN),
                    listOf(YELLOW, GREEN, ORANGE, Game.CodePegs.RED)))

            assertEquals(listOf(Game.KeyPegs.RED, Game.KeyPegs.WHITE, EMPTY, EMPTY),
                GameService.check(
                    listOf(YELLOW, BLACK, GREEN, GREEN),
                    listOf(YELLOW, GREEN, ORANGE, Game.CodePegs.RED)))

            assertEquals(listOf(Game.KeyPegs.RED, Game.KeyPegs.WHITE, EMPTY, EMPTY),
                GameService.check(
                    listOf(YELLOW, BLACK, GREEN, YELLOW),
                    listOf(YELLOW, GREEN, ORANGE, Game.CodePegs.RED)))
        }
    }

    @Nested
    inner class Play() {
        @Test
        fun `should return empty when code breaker fail guessing`() {
            val codeMaker = CodeMaker()
            val codeBreaker = CodeBreaker()
            val gameService = GameService(Game(codeMaker, codeBreaker))
            codeMaker.setCodePegs("YGBR")
            codeBreaker.guess("WWWO")

            assertEquals("----", gameService.play())
        }
        @Test
        fun `should return red pegs when code breaker guessed correctly`() {
            val codeMaker = CodeMaker()
            val codeBreaker = CodeBreaker()
            val gameService = GameService(Game(codeMaker, codeBreaker))
            codeMaker.setCodePegs("YGBR")
            codeBreaker.guess("WGBB")

            assertEquals("RR--", gameService.play())
        }

        @Test
        fun `should return white pegs when code breaker guessed correctly but incorrect location`() {
            val codeMaker = CodeMaker()
            val codeBreaker = CodeBreaker()
            val gameService = GameService(Game(codeMaker, codeBreaker))
            codeMaker.setCodePegs("BYYW")
            codeBreaker.guess("WGBB")

            assertEquals("WW--", gameService.play())
        }

        @Test
        fun `should return red and white pegs when code breaker guessed correctly but some incorrect location`() {
            val codeMaker = CodeMaker()
            val codeBreaker = CodeBreaker()
            val gameService = GameService(Game(codeMaker, codeBreaker))
            codeMaker.setCodePegs("BYYW")
            codeBreaker.guess("WYBB")

            assertEquals("RWW-", gameService.play())
        }

        @Test
        fun `should go to next round after a play`() {
            val codeMaker = CodeMaker()
            val codeBreaker = CodeBreaker()
            val gameService = GameService(Game(codeMaker, codeBreaker))
            codeMaker.setCodePegs("YGBR")
            codeBreaker.guess("WWWO")

            assertThat(gameService.game.round).isEqualTo(0)
            assertThat(gameService.game.winner).isNull()

            gameService.play()

            assertThat(gameService.game.round).isEqualTo(1)
            assertThat (gameService.game.isPlaying).isEqualTo(true)
        }

        @Test
        fun `can't play after more than 10 rounds`() {
            val codeMaker = CodeMaker()
            val codeBreaker = CodeBreaker()
            val gameService = GameService(Game(codeMaker, codeBreaker))
            codeMaker.setCodePegs("YGBR")
            codeBreaker.guess("WWWO")

            for (i in 0 until 10) gameService.play()
            assertThat { gameService.play() }.isFailure().isEqualTo(GameException("Game has finished"))
            assertThat( gameService.game.isPlaying ).isEqualTo(false)
        }

        @Test
        fun `code maker wins after 10 rounds of wrong guess`() {
            val codeMaker = CodeMaker()
            val codeBreaker = CodeBreaker()
            val gameService = GameService(Game(codeMaker, codeBreaker))
            codeMaker.setCodePegs("YGBR")
            codeBreaker.guess("WWWO")

            for (i in 0 until 10) gameService.play()
            assertThat(gameService.game.winner).isEqualTo(codeMaker)
            assertThat( gameService.game.isPlaying ).isEqualTo(false)
        }

        @Test
        fun `code breaker wins when guess correctly`() {
            val codeMaker = CodeMaker()
            val codeBreaker = CodeBreaker()
            val gameService = GameService(Game(codeMaker, codeBreaker))
            codeMaker.setCodePegs("YGBR")
            codeBreaker.guess("YGBR")

            gameService.play()

            assertThat(gameService.game.winner).isEqualTo(codeBreaker)
            assertThat( gameService.game.isPlaying ).isEqualTo(false)
        }
    }
}