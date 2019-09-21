package ru.hse.spb

interface InterpreterState<VariableType, FunctionType> {
    /**
     * Adds new stack frame. [isFunction] must be set to `true` if a frame is created for function call.
     * Returns `true` if frame was created
     */
    fun addFrame(isFunction: Boolean = false): Boolean

    /**
     * Removes last stack frame.
     * Returns `true` if frame was deleted
     */
    fun popFrame(): Boolean

    /**
     * Gets return value from last function stack frame.
     */
    fun getReturnValue(): VariableType?

    /**
     * Saves value into last function stack frame.
     */
    fun setReturnValue(value: VariableType): Boolean

    /**
     * Returns first occurrence of variable with a [name] from all stack frames
     */
    fun lookupVariable(name: String): VariableType?

    /**
     * Returns variable with a [name] from last stack frame
     */
    fun lookupVariableInLastFrame(name: String): VariableType?

    /**
     * Saves variable into last stack frame
     */
    fun putVariableInLastFrame(name: String, value: VariableType): Boolean

    /**
     * Updates first occurrence of variable if it exists and returns `true`
     */
    fun updateVariable(name: String, value: VariableType): Boolean

    /**
     * Returns first occurrence of function with a [name] from all stack frames
     */
    fun lookupFunction(name: String): FunctionType?

    /**
     * Returns function with a [name] from last stack frame
     */
    fun lookupFunctionInLastFrame(name: String): FunctionType?

    /**
     * Saves function into last stack frame
     */
    fun putFunctionInLastFrame(name: String, value: FunctionType): Boolean
}