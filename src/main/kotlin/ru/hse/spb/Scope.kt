package ru.hse.spb

import java.util.*

class Scope<T>(private val parent: Scope<T>?) {
    private val currentScope = HashMap<String, T>()

    fun assign(identifier : String, value : T) {
        currentScope[identifier] = value
    }

    fun get(identifier: String) : T? {
        return currentScope[identifier] ?: parent?.get(identifier)
    }

    fun childScope(): Scope<T> {
        return Scope(this)
    }
}