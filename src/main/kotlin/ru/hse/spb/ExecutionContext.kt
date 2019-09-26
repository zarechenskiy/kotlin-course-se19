package ru.hse.spb

import ru.hse.spb.parser.LangParser

data class ExecutionContext(
    val addressScope: MutableMap<Int, Int> = HashMap(),
    val varAddresses: MutableMap<String, Int> = HashMap(),
    val funAddresses: MutableMap<String, Int> = HashMap(),
    val varValues: MutableMap<Int, Int> = HashMap(),
    val funDefinition: MutableMap<Int, LangParser.FunctionContext> = HashMap(),
    var resultValue: Int? = null,
    var scopeId: Int = 0
) {
    init {
        scopeId = currentScopeId++
    }

    fun copy(): ExecutionContext = ExecutionContext(
        addressScope.copy(),
        varAddresses.copy(),
        funAddresses.copy(),
        varValues,
        funDefinition,
        resultValue
    )

    fun addVariable(name: String, value: Int) {
        check(!funAddresses.containsKey(name)) {
            "Can no define variable $name: function with such a name has been already defined"
        }
        if (varAddresses.containsKey(name)) {
            check(addressScope[varAddresses[name]] != scopeId) { "Multiply definition of variable $name" }
        }
        val address = currentAddress++
        addressScope[address] = scopeId
        varAddresses[name] = address
        varValues[address] = value
    }

    fun addFunction(name: String, ctx: LangParser.FunctionContext) {
        check(!varAddresses.containsKey(name)) {
            "Can no define function $name: variable with such a name has been already defined"
        }
        if (funAddresses.containsKey(name)) {
            check(addressScope[funAddresses[name]] != scopeId) { "Multiply definition of function $name" }
        }
        val address = currentAddress++
        addressScope[address] = scopeId
        funAddresses[name] = address
        funDefinition[address] = ctx
    }

    companion object {
        var currentAddress: Int = 0
        var currentScopeId: Int = 0
    }
}

fun <U, V> MutableMap<U, V>.copy(): MutableMap<U, V> {
    val to: MutableMap<U, V> = HashMap()
    for (k in this.keys) {
        to[k] = this[k]!!
    }
    return to
}


