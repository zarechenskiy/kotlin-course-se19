package ru.hse.spb

import ru.hse.spb.parser.FunParser.*
import java.lang.RuntimeException


class BlockEnv(private var functionsMap: HashMap<String, FunctionContext> = HashMap(),
                    private val varsMap: HashMap<String, Any?> = HashMap()) {

    fun deepCopyVarMap(): HashMap<String, Any?> {
        val copyVarsMap = HashMap<String, Any?>()
        varsMap.entries.forEach { copyVarsMap[it.key] = it.value }

        return copyVarsMap
    }

    fun deepCopyFunctionsMap(): HashMap<String, FunctionContext> {
        val copyFunctionsMap = HashMap<String, FunctionContext>()
        functionsMap.entries.forEach { copyFunctionsMap[it.key] = it.value }

        return copyFunctionsMap
    }

    fun assignVar(name: String, value: Any?) {
        varsMap[name] = value
    }

    fun getVar(name: String): Any {
        return varsMap[name] ?: error("No variable with name $name")
    }

    fun deleteVar(name: String) {
        varsMap.remove(name)
    }

    fun addFunction(name: String, ctx: FunctionContext) {
        if (name in functionsMap) {
            throw RuntimeException("Already exists function with name $name")
        }

        functionsMap[name] = ctx
    }

    fun deleteFunction(name: String) {
        functionsMap.remove(name)
    }

    fun getFunction(name: String): FunctionContext {
        return functionsMap[name] ?: error("No function with name $name")
    }
}