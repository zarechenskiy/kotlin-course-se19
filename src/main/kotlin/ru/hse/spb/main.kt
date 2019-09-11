package ru.hse.spb

import java.lang.AssertionError
class Main {
    private val answerArray: ArrayList<Int> = ArrayList()
    private var currentPosition: Int = 0

    private fun processAnswerFromTd(input: String): Int {
        var ans = 0
        val td = "<td>"
        val tdClosed = "</td>"

        while (input.drop(currentPosition).startsWith(td)) {
            currentPosition += td.length
            processAnswerFromTable(input)
            ans++
            if (!input.drop(currentPosition).startsWith(tdClosed)) {
                throw AssertionError("couldn't pair $td")
            }
            currentPosition += tdClosed.length
        }
        return ans
    }

    private fun processAnswerFromTr(input: String): Int {
        var ans = 0
        val tr = "<tr>"
        val trClosed = "</tr>"
        while (input.drop(currentPosition).startsWith(tr)) {
            currentPosition += tr.length
            ans += processAnswerFromTd(input)
            if (!input.drop(currentPosition).startsWith(trClosed)) {
                throw AssertionError("couldn't pair $tr")
            }
            currentPosition += trClosed.length
        }
        return ans
    }

    private fun processAnswerFromTable(input: String): Int {
        var ans = 0
        val table = "<table>"
        val tableClosed = "</table>"
        if (input.drop(currentPosition).startsWith(table)) {
            currentPosition += table.length
            ans = processAnswerFromTr(input)
        } else return ans
        if (!input.drop(currentPosition).startsWith(tableClosed)) {
            throw AssertionError("couldn't pair $table")
        }
        currentPosition += tableClosed.length
        answerArray.add(ans)
        return 0
    }

    // for tests
    fun getAnswer(input: String): String {
        answerArray.clear()
        currentPosition = 0
        processAnswerFromTable(input.filter { it != '\n' })
        var answer = String()
        answerArray.sorted().forEach { ans -> answer += ans; answer += " " }
        return answer
    }
}

fun main() {
    var input = String()
    var nextLine = readLine()
    while (nextLine != null) {
        input += nextLine
        nextLine = readLine()
    }
    val mainObject = Main()
    print(mainObject.getAnswer(input))
}