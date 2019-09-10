package ru.hse.spb

import java.lang.AssertionError

val answerArray: ArrayList<Int> = ArrayList()
var currentPosition: Int = 0

fun getAnswerFromTd(input: String): Int {
    var ans = 0
    while (input.drop(currentPosition).startsWith("<td>")) {
        currentPosition += 4
        getAnswerFromTable(input)
        ans++
        if (!input.drop(currentPosition).startsWith("</td>")) {
            throw AssertionError()
        }
        currentPosition += 5
    }
    return ans
}

fun getAnswerFromTr(input: String): Int {
    var ans = 0
    while (input.drop(currentPosition).startsWith("<tr>")) {
        currentPosition += 4
        ans += getAnswerFromTd(input)
        if (!input.drop(currentPosition).startsWith("</tr>")) {
            throw AssertionError()
        }
        currentPosition += 5
    }
    return ans
}

fun getAnswerFromTable(input: String): Int {
    var ans = 0
    if (input.drop(currentPosition).startsWith("<table>")) {
        currentPosition += 7
        ans = getAnswerFromTr(input)
    } else return ans
    if (!input.drop(currentPosition).startsWith("</table>")) {
        throw AssertionError()
    }
    currentPosition += 8
    answerArray.add(ans)
    return 0
}

fun main() {
    var input = String()
    var nextLine = readLine()
    while (nextLine != null) {
        input += nextLine
        nextLine = readLine()
    }
    getAnswerFromTable(input)
    answerArray.sorted().forEach { ans -> print(ans); print(" ") }
}

// for tests
fun getAnswer(input: String): String {
    answerArray.clear()
    currentPosition = 0
    getAnswerFromTable(input.filter { it != '\n' })
    var answer = String()
    answerArray.sorted().forEach { ans -> answer += ans; answer += " " }
    return answer
}