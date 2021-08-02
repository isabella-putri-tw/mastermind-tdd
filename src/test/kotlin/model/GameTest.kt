package model

import exceptions.GameException
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import kotlin.test.assertFailsWith

internal class GameTest {

    @Nested
    inner class BuildCodePegs() {
        @Test
        fun `should throw exception if string is empty`() {
            val exception = assertFailsWith(GameException::class) {
                Game.buildCodePegs("")
            }
            assertEquals("String shouldn't be empty", exception.message)
        }



        @Test
        fun `should throw exception if length is not 4`() {
            val exception = assertFailsWith(GameException::class) {
                Game.buildCodePegs("C")
            }
            assertEquals(
                "Length must be 4 characters",
                exception.message
            )
        }
        @Test
        fun `should throw exception if string has invalid colors`() {
            val exception = assertFailsWith(GameException::class) {
                Game.buildCodePegs("CCCC")
            }
            assertEquals(
                "Code color is unacceptable, only ([Y]ellow, [O]range, [G]reen, [R]ed, [B]lack, and [W]hite)",
                exception.message
            )
        }

        @Test
        fun `should return list of code pegs when color codes are correct`() {
            assertEquals(
                listOf(
                    Game.CodePegs.YELLOW,
                    Game.CodePegs.ORANGE,
                    Game.CodePegs.GREEN,
                    Game.CodePegs.RED,
                ), Game.buildCodePegs("YOGR")
            )

            assertEquals(
                listOf(
                    Game.CodePegs.BLACK,
                    Game.CodePegs.WHITE,
                    Game.CodePegs.BLACK,
                    Game.CodePegs.WHITE
                ), Game.buildCodePegs("BWBW")
            )
        }

    }

    @Nested
    inner class FormatKeyPegs() {
        @Test
        fun `should return dash for empty pegs`() {
            assertEquals("-", Game.formatKeyPegs(listOf(Game.KeyPegs.EMPTY)))
            assertEquals("----", Game.formatKeyPegs(listOf(Game.KeyPegs.EMPTY, Game.KeyPegs.EMPTY, Game.KeyPegs.EMPTY, Game.KeyPegs.EMPTY)))
        }

        @Test
        fun `should return 'R' for red pegs`() {
            assertEquals("R", Game.formatKeyPegs(listOf(Game.KeyPegs.RED)))
            assertEquals("RR--", Game.formatKeyPegs(listOf(Game.KeyPegs.RED, Game.KeyPegs.RED, Game.KeyPegs.EMPTY, Game.KeyPegs.EMPTY)))
        }

        @Test
        fun `should return 'W' for white pegs`() {
            assertEquals("W", Game.formatKeyPegs(listOf(Game.KeyPegs.WHITE)))
            assertEquals("WWW-", Game.formatKeyPegs(listOf(Game.KeyPegs.WHITE, Game.KeyPegs.WHITE, Game.KeyPegs.WHITE, Game.KeyPegs.EMPTY)))

        }

        @Test
        fun `should return combined pegs for multicolored pegs`() {
            assertEquals("RWW-", Game.formatKeyPegs(listOf(Game.KeyPegs.RED, Game.KeyPegs.WHITE, Game.KeyPegs.WHITE, Game.KeyPegs.EMPTY)))
            assertEquals("RRRW", Game.formatKeyPegs(listOf(Game.KeyPegs.RED, Game.KeyPegs.RED, Game.KeyPegs.RED, Game.KeyPegs.WHITE)))
        }
    }
}