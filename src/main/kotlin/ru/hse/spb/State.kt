package ru.hse.spb

class State(private val parentState: State? = null) {

    private val variables = mutableMapOf<String, Int>()
    private val functions = mutableMapOf<String, Function<Int>>()

    var isReturned: Boolean = false

    var returnValue: Int? = null

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
        if (name in variables) {
            variables[name] = value
            return
        }
        if (parentState?.getVariable(name) != null) {
            parentState.setVariable(name, value)
        }
        variables[name] = value
    }
}