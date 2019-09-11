package ru.hse.spb

import java.util.*

const val CHAR_DIGIT_ONE = '1'

enum class FieldStatus {
    EMPTY, BLOCKED, CASTLE
}

val dx = intArrayOf(1, 0, 0, -1)
val dy = intArrayOf(0, 1, -1, 0)

data class Field(
        var x: Int,
        var y: Int
) {
    var status: FieldStatus? = null
    var distance: Int = 0

    fun setStatus(statusSymbol: Char) {
        when (statusSymbol) {
            '#' -> this.status = FieldStatus.BLOCKED
            '.' -> this.status = FieldStatus.EMPTY
            else -> {
                this.status = FieldStatus.CASTLE
            }
        }
    }

    val isFree: Boolean
        get() = status == FieldStatus.EMPTY
}

data class Player(
        val id: Int,
        val maxDistance: Int
) {
    var castles: Queue<Field> = LinkedList()
    var score: Int = 0

    fun addCastle(field: Field, distance: Int = 0) {
        field.status = FieldStatus.CASTLE
        field.distance = distance
        castles.add(field)
        this.score++
    }
}

data class Board(
        var rows: Int,
        var cols: Int
) {
    private val board: Array<Array<Field>> = Array(rows) { row -> Array(cols) { col -> Field(row, col) } }

    fun containsField(i: Int, j: Int): Boolean {
        return i in 0 until rows && j in 0 until cols
    }

    fun getField(i: Int, j: Int): Field {
        return board[i][j]
    }
}

fun playGame(board: Board, players: Array<Player>): List<Int> {
    while (players.any { it.castles.isNotEmpty() }) {
        for (player in players) {

            for (castle in player.castles) {
                castle.distance = 0
            }

            val newCastles = LinkedList<Field>()
            while (!player.castles.isEmpty()) {
                val castle = player.castles.poll()
                if (castle.distance == player.maxDistance) {
                    newCastles.add(castle)
                    continue
                }
                for (j in 0..3) {
                    val x = castle.x + dx[j]
                    val y = castle.y + dy[j]
                    if (board.containsField(x, y) && board.getField(x, y).isFree) {
                        player.addCastle(board.getField(x, y), castle.distance + 1)
                    }
                }
            }
            player.castles = newCastles
        }
    }
    return players.map { player -> player.score }
}

fun initGame(scanner: Scanner): Pair<Board, Array<Player>> {
    val rows = scanner.nextInt()
    val cols = scanner.nextInt()
    val playersNumber = scanner.nextInt()

    val players: Array<Player> = Array(playersNumber) { playerId -> Player(playerId, scanner.nextInt()) }
    val board = Board(rows, cols)

    val fieldSymbols: Array<CharArray> = Array(rows) { scanner.next().toCharArray() }

    for ((i, fieldSymbolRow) in fieldSymbols.withIndex()) {
        for ((j, fieldSymbol) in fieldSymbolRow.withIndex()) {
            val field = board.getField(i, j)
            field.setStatus(fieldSymbol)
            if (field.status == FieldStatus.CASTLE) {
                players[fieldSymbol - CHAR_DIGIT_ONE].addCastle(board.getField(i, j))
            }
        }
    }
    return Pair(board, players)
}

fun runGameCycle(scanner: Scanner) {
    val (board, players) = initGame(scanner)
    val scores = playGame(board, players)
    print(scores.joinToString(" "))
}

fun main(args: Array<String>) {
    runGameCycle(Scanner(System.`in`))
}
