package ru.hse.spb

import java.util.*

fun getGoodSubstrNumber(input : String) : Int {
    var previousZeros = 0
    var goodSubstrNumber = 0
    for ((index, elem) in input.withIndex()) {
        var maxLength = previousZeros + 1
        if (elem == '1') {
            var currentNumber = 0
            for (forwardIndex in index until input.length) {
                currentNumber *= 2
                if (input[forwardIndex] == '1') {
                    ++currentNumber
                }

                if (currentNumber <= maxLength) {
                    ++goodSubstrNumber
                } else {
                    break
                }
                ++maxLength
            }
            previousZeros = 0
        } else {
            ++previousZeros
        }
    }
    return goodSubstrNumber
}

fun main() {
    val reader = Scanner(System.`in`)
    val queriesNumber:Int = reader.nextInt()
    reader.nextLine()
    for (query in 0 until queriesNumber) {
        val input = reader.nextLine()
        println(getGoodSubstrNumber(input))
    }
}