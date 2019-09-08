package tanvd.itmo.kotlin

import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object Solver {
    fun solve(input: InputStream): String {

        val newString = Array(200001) { ' ' }
        val lettersToIndices = HashMap<Char, ArrayList<Int>>()

        return Scanner(input.bufferedReader()).use { scanner ->
            val k = scanner.nextInt()
            val string = scanner.next()
            val n = scanner.nextInt()

            var p = 0
            for (i in 0 until k) {
                for (char in string) {
                    newString[p] = char
                    lettersToIndices.getOrPut(char, { ArrayList() }).add(p)
                    p++
                }
            }

            for (i in 0 until n) {
                val aInd = scanner.nextInt() - 1
                val b = scanner.nextChar()

                newString[lettersToIndices[b]!![aInd]] = '!'
                lettersToIndices[b]!!.removeAt(aInd)
            }

            buildString {
                newString.asSequence()
                        .take(p)
                        .filter { it != '!' }
                        .forEach {
                            append(it)
                        }
            }
        }
    }
}

fun main() = println(Solver.solve(System.`in`))

