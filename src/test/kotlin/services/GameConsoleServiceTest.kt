package services

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class GameConsoleServiceTest {
    @Nested
    inner class Start() {
        private val standardOut = System.out
        private val outputStreamCaptor = ByteArrayOutputStream()

        @Mock
        lateinit var mockReader: BufferedReader

        private lateinit var gameConsoleService: GameConsoleService

        @BeforeEach
        fun setUp() {
            MockitoAnnotations.openMocks(this)
            System.setOut(PrintStream(outputStreamCaptor))
            gameConsoleService = GameConsoleService()
            gameConsoleService.reader = mockReader

        }

        @AfterEach
        fun tearDown() {
            System.setOut(standardOut)
        }

        @Test
        fun `should print welcome and rules then ask for code maker enter code pegs`() {
            Mockito.`when`(mockReader.readLine()).thenReturn("BBBB")
            gameConsoleService.start()

            assertThat(outputStreamCaptor.toString().trim()).contains(
                """
                    Welcome to Mastermind Game!
                    --------------------------------------------------
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
                    
                    Let's start the game!
                    --------------------------------------------------
                    Code maker, what is your Code Pegs sequence (Length: 4 | Colors: [Y]ellow, [O]range, [G]reen, [R]ed, [B]lack, and [W]hite)?
                    """.trimIndent().trimStart().trimEnd()
            )
        }

        @Test
        fun `should ask Code Breaker ask again if Code Breaker input wrongly`() {
            Mockito.`when`(mockReader.readLine()).thenReturn("WWWWW", "OOOO")
            gameConsoleService.start()

            assertThat(outputStreamCaptor.toString().trim()).contains("""
                Code maker, what is your Code Pegs sequence (Length: 4 | Colors: [Y]ellow, [O]range, [G]reen, [R]ed, [B]lack, and [W]hite)?
                Length must be 4 characters
                Code maker, what is your Code Pegs sequence (Length: 4 | Colors: [Y]ellow, [O]range, [G]reen, [R]ed, [B]lack, and [W]hite)?
            """.trimIndent().trimStart().trimEnd())
        }

        @Test
        fun `should continue to ask code breaker to input`() {
            Mockito.`when`(mockReader.readLine()).thenReturn("YOGR")
            gameConsoleService.start()

            assertThat(outputStreamCaptor.toString().trim()).contains("""
                Code maker, what is your Code Pegs sequence (Length: 4 | Colors: [Y]ellow, [O]range, [G]reen, [R]ed, [B]lack, and [W]hite)?
                |
                |
                |
                |
                |
                |
                |
                |
                |
                |
                |
                |
                |
                |
                |
                |
                |
                |
                |
                |
                |
                |
                |
                |
                |
                Code breaker, it time for you to guess!!
                What is your guess (Length: 4 | Colors: [Y]ellow, [O]range, [G]reen, [R]ed, [B]lack, and [W]hite)?
            """.trimIndent().trimStart().trimEnd())
        }

        @Test
        fun `should retry guess when it's incorrect format`() {
            Mockito.`when`(mockReader.readLine()).thenReturn("YOGR", "ABCD", "RGBB")
            gameConsoleService.start()

            assertThat(outputStreamCaptor.toString().trim()).contains("""
                Code breaker, it time for you to guess!!
                What is your guess (Length: 4 | Colors: [Y]ellow, [O]range, [G]reen, [R]ed, [B]lack, and [W]hite)?
                Code color is unacceptable, only ([Y]ellow, [O]range, [G]reen, [R]ed, [B]lack, and [W]hite)
                What is your guess (Length: 4 | Colors: [Y]ellow, [O]range, [G]reen, [R]ed, [B]lack, and [W]hite)?
            """.trimIndent().trimStart().trimEnd())
        }

        @Test
        fun `should display key peg result after guessing with right format`() {
            Mockito.`when`(mockReader.readLine()).thenReturn("YOGR", "RGBB")
            gameConsoleService.start()

            assertThat(outputStreamCaptor.toString().trim()).contains("""
                Code breaker, it time for you to guess!!
                What is your guess (Length: 4 | Colors: [Y]ellow, [O]range, [G]reen, [R]ed, [B]lack, and [W]hite)?
                Round 1: WW--
            """.trimIndent().trimStart().trimEnd())
        }

        @Test
        fun `should announce Code Breaker win when code breaker guess correctly`() {
            Mockito.`when`(mockReader.readLine()).thenReturn("YOGR", "RGBB", "YYGR", "YOGR", )
            gameConsoleService.start()

            assertThat(outputStreamCaptor.toString().trim()).contains("""
                What is your guess (Length: 4 | Colors: [Y]ellow, [O]range, [G]reen, [R]ed, [B]lack, and [W]hite)?
                Round 2: RRR-
                What is your guess (Length: 4 | Colors: [Y]ellow, [O]range, [G]reen, [R]ed, [B]lack, and [W]hite)?
                Round 3: RRRR
                Code Breaker WIN!!!
            """.trimIndent().trimStart().trimEnd())
        }

        @Test
        fun `should announce Code Maker win when code breaker fail to guess`() {
            Mockito.`when`(mockReader.readLine()).thenReturn("YOGR", "RGBB")
            gameConsoleService.start()

            assertThat(outputStreamCaptor.toString().trim()).contains("""
                What is your guess (Length: 4 | Colors: [Y]ellow, [O]range, [G]reen, [R]ed, [B]lack, and [W]hite)?
                Round 9: WW--
                What is your guess (Length: 4 | Colors: [Y]ellow, [O]range, [G]reen, [R]ed, [B]lack, and [W]hite)?
                Round 10: WW--
                Code Maker WIN!!!
            """.trimIndent().trimStart().trimEnd())
        }
    }
}