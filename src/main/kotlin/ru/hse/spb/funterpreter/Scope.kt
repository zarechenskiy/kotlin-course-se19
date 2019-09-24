package ru.hse.spb.funterpreter

import java.util.*

typealias ValueScope = Scope<Value>
class Scope<T>(private val parent: Scope<T>? = null) {
    private val currentScope = HashMap<String, T>()

    fun create(identifier : String, value : T) {
        currentScope[identifier] = value
    }

    fun get(identifier: String) : T? {
        return currentScope[identifier] ?: parent?.get(identifier)
    }

    fun assign(identifier: String, value : T) : Boolean {
        if (currentScope.containsKey(identifier)) {
            currentScope[identifier] = value
            return true
        }
        return parent?.assign(identifier, value) ?: false
    }

    fun childScope(): Scope<T> = Scope(this)
}