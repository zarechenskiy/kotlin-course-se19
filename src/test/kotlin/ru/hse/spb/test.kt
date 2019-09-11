package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test

class TestSource {

    @Test
    fun firstGameTest() {
        val rows = 3
        val cols = 3
        val playersNumber = 2

        val players: Array<Player> = Array(playersNumber) { playerId -> Player(playerId, 1) }
        val board = Board(rows, cols)

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val field = board.getField(i, j)
                field.setStatus('.')
            }
        }

        players[0].addCastle(board.getField(0, 0))
        players[1].addCastle(board.getField(2, 2))

        assertEquals("First test failed", listOf(6, 3), playGame(board, players))
    }

    @Test
    fun secondGameTest() {
        val rows = 3
        val cols = 4
        val playersNumber = 4

        val players: Array<Player> = Array(playersNumber) { playerId -> Player(playerId, 1) }
        val board = Board(rows, cols)

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val field = board.getField(i, j)
                field.setStatus('.')
            }
        }
        board.getField(1, 0).setStatus('#')

        for ((j, player) in players.withIndex()) {
            player.addCastle(board.getField(2, j))
        }

        assertEquals("Second test failed", listOf(1, 4, 3, 3), playGame(board, players))
    }
}
