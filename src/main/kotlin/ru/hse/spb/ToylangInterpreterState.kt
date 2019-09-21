package ru.hse.spb

class ToylangInterpreterState<VariableType, FunctionType> : InterpreterState<VariableType, FunctionType> {
    private data class StackFrame<VariableType, FunctionType>(
        val isFunction: Boolean,
        val variables: MutableMap<String, VariableType> = mutableMapOf(),
        val functions: MutableMap<String, FunctionType> = mutableMapOf(),
        var returnValue: VariableType? = null
    )

    private val stackFrames = mutableListOf(StackFrame<VariableType, FunctionType>(false))

    override fun getReturnValue(): VariableType? =
        stackFrames.filter { it.isFunction }.let {
            when (it.isEmpty()) {
                true -> null
                else -> it[it.lastIndex].returnValue
            }
        }

    override fun setReturnValue(value: VariableType): Boolean =
        stackFrames.filter { it.isFunction }.let {
            when (it.isEmpty()) {
                true -> false
                else -> {
                    it[it.lastIndex].returnValue = value; true
                }
            }
        }


    override fun addFrame(isFunction: Boolean) =
        stackFrames.add(StackFrame(isFunction))

    override fun popFrame() =
        if (stackFrames.isEmpty()) false else stackFrames.removeAt(stackFrames.lastIndex).let { true }

    override fun lookupVariable(name: String) =
        stackFrames.map { it.variables }.asReversed().find { it.contains(name) }?.get(name)

    override fun lookupVariableInLastFrame(name: String) =
        if (stackFrames.isEmpty()) null else stackFrames[stackFrames.lastIndex].variables[name]

    override fun putVariableInLastFrame(name: String, value: VariableType) =
        if (stackFrames.isEmpty()) false else stackFrames[stackFrames.lastIndex].variables.put(name, value).let { true }

    override fun updateVariable(name: String, value: VariableType) =
        stackFrames.foldRight(false) { stackFrame, alreadyUpdated ->
            when (alreadyUpdated) {
                true -> true
                false -> when (name in stackFrame.variables) {
                    true -> stackFrame.variables.set(name, value).let { true }
                    false -> false
                }
            }
        }

    override fun lookupFunction(name: String) =
        stackFrames.map { it.functions }.asReversed().find { it.contains(name) }?.get(name)

    override fun lookupFunctionInLastFrame(name: String) =
        if (stackFrames.isEmpty()) null else stackFrames[stackFrames.lastIndex].functions[name]

    override fun putFunctionInLastFrame(name: String, value: FunctionType) =
        if (stackFrames.isEmpty()) false else stackFrames[stackFrames.lastIndex].functions.put(name, value).let { true }

}