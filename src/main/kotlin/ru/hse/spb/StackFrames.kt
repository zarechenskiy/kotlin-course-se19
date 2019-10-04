package ru.hse.spb

import ru.hse.spb.parser.FunPLParser

object StackFrames {
    var functions = mutableListOf<MutableMap<String, FunPLParser.FunctionContext>>()
    var variables = mutableListOf<MutableMap<String, Int>>()
    var returnedFromFunction = false

    fun getVariable(name: String): MutableMap.MutableEntry<String, Int>? {
        for (map in variables.reversed()) {
            val found = map.entries.find { it.key == name }
            if (found != null) {
                return found
            }
        }
        return null
    }

    fun getFunction(name: String): MutableMap.MutableEntry<String, FunPLParser.FunctionContext>? {
        for (map in functions.reversed()) {
            val found = map.entries.find { it.key == name }
            if (found != null) {
                return found
            }
        }
        return null
        }

}