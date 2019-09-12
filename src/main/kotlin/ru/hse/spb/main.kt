package ru.hse.spb

import java.util.Scanner

private enum class Gender {
    M, F, ERROR
}

private enum class Token {
    ADJECTIVE, NOUN, VERB, END, ERROR
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

private fun getGender(word: String): Gender {
    if (word.endsWith("lios")
            || word.endsWith("etr")
            || word.endsWith("initis"))
        return Gender.M;
    if (word.endsWith("liala")
            || word.endsWith("etra")
            || word.endsWith("inites"))
        return Gender.F
    return Gender.ERROR
}

private fun getToken(word: String): Token {
    if (word.endsWith("lios") || word.endsWith("liala")) {
        return Token.ADJECTIVE
    }
    if (word.endsWith("etr") || word.endsWith("etra")) {
        return Token.NOUN
    }
    if (word.endsWith("initis") || word.endsWith("inites")) {
        return Token.VERB
    }
    return Token.ERROR;
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
    val gender = getGender(word = word)
    if (gender == Gender.ERROR) {
        return false
    }
    if (!reader.hasNext()) {
        return true
    }
    var curState = State.START
    while (true) {
        val token = getToken(word)
        if (gender != getGender(word) || token == Token.ERROR) {
            return false
        }
        curState = states.getOrDefault(curState, mapOf()).getOrDefault(getToken(word = word), State.NO)
        if (curState == State.NO) {
            return false
        }
        if (!reader.hasNext()) {
            break
        }
        word = reader.next()
    }

    curState = states.getOrDefault(curState, mapOf()).getOrDefault(Token.END, State.NO)
    return curState == State.YES
}
