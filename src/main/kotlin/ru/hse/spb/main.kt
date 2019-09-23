package ru.hse.spb

import java.util.Scanner

private enum class Gender {
    M, F
}

private enum class Token {
    ADJECTIVE, NOUN, VERB, END
}

private enum class State {
    START, ADJECTIVE, VERB, NOUN, YES, NO
}

private val states = mapOf(
        State.START to mapOf(
                Token.ADJECTIVE to State.ADJECTIVE,
                Token.NOUN to State.NOUN,
                Token.VERB to State.NO,
                Token.END to State.NO),
        State.ADJECTIVE to mapOf(
                Token.ADJECTIVE to State.ADJECTIVE,
                Token.NOUN to State.NOUN,
                Token.VERB to State.NO,
                Token.END to State.NO
        ),
        State.NOUN to mapOf(
                Token.ADJECTIVE to State.NO,
                Token.NOUN to State.NO,
                Token.VERB to State.VERB,
                Token.END to State.YES
        ),
        State.VERB to mapOf(
                Token.ADJECTIVE to State.NO,
                Token.NOUN to State.NO,
                Token.VERB to State.VERB,
                Token.END to State.YES
        )
)

fun String.endsWithAnyOf(vararg strs: String): Boolean {
    return strs.firstOrNull { s -> endsWith(s) } != null
}

private fun getGender(word: String): Gender? {
    return when {
        word.endsWithAnyOf("lios", "etr", "initis") -> Gender.M
        word.endsWithAnyOf("liala", "etra", "inites") -> Gender.F
        else -> null
    }
}

private fun getToken(word: String): Token? {
    return when {
        word.endsWithAnyOf("lios", "liala") -> Token.ADJECTIVE
        word.endsWithAnyOf("etr", "etra") -> Token.NOUN
        word.endsWithAnyOf("initis", "inites") -> Token.VERB
        else -> null
    }
}

fun main() {
    val reader = Scanner(System.`in`)
    if (belongsToLanguage(reader)) println("YES") else println("NO")
}

fun belongsToLanguage(reader: Scanner): Boolean {
    if (!reader.hasNext()) {
        return false
    }
    var word = reader.next()
    val gender = getGender(word) ?: return false
    if (!reader.hasNext()) {
        return true
    }
    var curState = State.START
    while (true) {
        val token = getToken(word)
        if (gender != getGender(word) || token == null) {
            return false
        }
        curState = states[curState]?.get(getToken(word)) ?: State.NO
        if (curState == State.NO) {
            return false
        }
        if (!reader.hasNext()) {
            break
        }
        word = reader.next()
    }

    curState = states[curState]?.get(Token.END) ?: State.NO
    return curState == State.YES
}
