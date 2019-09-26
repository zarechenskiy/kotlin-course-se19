package ru.hse.spb

class State(val parentState: State?) {
    constructor() : this(null) {
    }

    private val variables: MutableMap<String, Int> = mutableMapOf()
    private val functions: MutableMap<String, Function<Int>> = mutableMapOf()

    var isReturned: Boolean = false
        get() = field
        set(value) {
            field = value
        }

    var returnValue: Int? = null
        get() = field
        set(value) {
            field = value
        }

    fun getFunc(name: String): Function<Int>? {
        return functions[name] ?: parentState?.getFunc(name)
    }

    fun addFunc(name: String, function: Function<Int>) {
        functions[name] = function
    }

    fun getVariable(name: String): Int? {
        return variables[name] ?: parentState?.getVariable(name)
    }

    fun newVariable(name: String, value: Int = 0) {
        variables[name] = value
    }

    fun setVariable(name: String, value: Int) {
        if (variables.contains(name)) {
            variables[name] = value
            return
        }
        if (parentState?.getVariable(name) != null) {
            parentState.setVariable(name, value)
        }
        variables[name] = value
    }
}