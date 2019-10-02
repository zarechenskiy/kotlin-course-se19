package ru.hse.spb.tex.util

fun parametersOrNothing(vararg parameter: String?): String {
    return "[${parameter.filter { it != null && it != "" }.joinToString(", ")}]".takeIf { it.length > 2 } ?: ""
}

fun pairsToParameter(vararg params: Pair<String, String>): String {
    return parametersOrNothing(params.joinToString(", ") { "${it.first} = ${it.second}" })
}

fun convertToParameters(openBracket: String, closeBracket: String, argumentsLists: List<List<String>>): String =
        if (argumentsLists.isNotEmpty()) {
            argumentsLists.joinToString(closeBracket + openBracket, openBracket, closeBracket) {
                it.joinToString(", ")
            }
        } else {
            ""
        }