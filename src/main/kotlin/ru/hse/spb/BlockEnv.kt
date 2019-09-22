package ru.hse.spb

import ru.hse.spb.parser.FunParser.*


data class BlockEnv(var functionsMap: HashMap<String, FunctionContext> = HashMap(),
                    val varsMap: HashMap<String, Any?> = HashMap(),
                    var returnRegister : Any? = null) {
    fun deepCopy() : BlockEnv {
        val newFunctionsMap = HashMap<String, FunctionContext>()
        functionsMap.entries.forEach { newFunctionsMap[it.key] = it.value }

        val newVarsMap = HashMap<String, Any?>()
        varsMap.entries.forEach { newVarsMap[it.key] = it.value }

        return BlockEnv(newFunctionsMap, newVarsMap)
    }
}