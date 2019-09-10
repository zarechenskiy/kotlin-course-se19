package ru.hse.spb

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.math.PI
import kotlin.math.atan

fun getSymbolsFromString(string : String) : HashSet<Char> {
    val stringSymbols = HashSet<Char>()
    for (symbol in string) {
        if (!stringSymbols.contains(symbol)) {
            stringSymbols.add(symbol)
        }
    }

    return stringSymbols
}

fun getStringMissingSymbols(string: String) : ArrayList<Char> {
    val stringSymbols = getSymbolsFromString(string)
    val missingSymbols = ArrayList<Char>()

    for (symbol in 'a'..'z') {
        if (!stringSymbols.contains(symbol)) {
            missingSymbols.add(symbol)
        }
    }

    return missingSymbols
}

fun canChooseSymbolNumber(string: String) : Int {
    var canChooseSymbolNumber = 0
    for (i in 0 until (string.length + 1)/ 2) {
        canChooseSymbolNumber +=
                if (string[i] == '?' && string[string.length - 1 - i] == '?') {
                    1
                } else {
                    0
                }
    }

    return canChooseSymbolNumber
}

fun mustChooseSymbolNumber(missingSymbols : ArrayList<Char>, mustHaveCharsNumber: Int) : Int {
    var mustChooseCharsNumber = 0
    for (symbol in missingSymbols) {
        if (symbol - 'a' < mustHaveCharsNumber) {
            mustChooseCharsNumber++
        }
    }

    return mustChooseCharsNumber
}

fun hasAnswer(string: String) : Boolean {
    for (i in 0 until (string.length + 1) / 2) {
        if (string[i] != '?' && string[string.length - 1 - i] != '?') {
            if (string[i] != string[string.length - 1 - i]) {
                return false
            }
        }
    }

    return true
}

fun solveProblem(string: String, mustHaveCharsNumber : Int) : ArrayList<Char>? {
    val chars = ArrayList<Char>()
    val missingSymbols = getStringMissingSymbols(string)
    val mustChooseCharsNumber = mustChooseSymbolNumber(missingSymbols, mustHaveCharsNumber)
    var canChooseCharsNumber = canChooseSymbolNumber(string)
    var index = 0

    if (mustChooseCharsNumber > canChooseCharsNumber || !hasAnswer(string)) {
        return null
    }

    for (char in string) {
        chars.add(char)
    }

    for (i in 0 until (string.length + 1) / 2) {
        if (string[i] == '?' && string[string.length - 1 - i] == '?') {
            if (mustChooseCharsNumber < canChooseCharsNumber) {
                chars[i] = 'a'
                chars[string.length - 1 - i] = 'a'
                canChooseCharsNumber--
            } else {
                chars[i] = missingSymbols[index]
                chars[string.length - 1 - i] = missingSymbols[index]
                index++
            }
        } else if (string[i] == '?') {
            chars[i] = string[string.length - 1 - i]
            chars[string.length - 1 - i] = string[string.length - 1 - i]
        } else {
            chars[i] = string[i]
            chars[string.length - 1 - i] = string[i]
        }
    }

    return chars
}

fun main() {
    val reader = Scanner(System.`in`)
    val mustHaveCharsNumber = reader.nextInt()
    val string = reader.next()
    val resultString = solveProblem(string, mustHaveCharsNumber)

    if (resultString == null) {
        print("IMPOSSIBLE")
    } else {
        for (char in resultString) {
            print(char)
        }
    }
}