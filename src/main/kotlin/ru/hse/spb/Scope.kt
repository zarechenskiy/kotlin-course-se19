package ru.hse.spb

import ru.hse.spb.parser.MyLangParser
import java.util.*
import kotlin.collections.HashMap


data class Scope(val stack: Stack<Frame> = Stack<Frame>().apply { push(Frame()) }) {
    data class Frame(val functions: HashMap<String, MyLangParser.LFunContext> = HashMap(), val variables: HashMap<String, Context> = HashMap())

    private val frame: Frame
        get() = stack.peek()

    fun declareVariable(name: String) {
        require(name !in frame.variables.keys) { "Variable $name is already declared" }
        frame.variables[name] = Context.NONE
    }

    fun setVariable(name: String, value: Context) {
        var isExist = false
        for (frame in stack.reversed()) {
            if (name in frame.variables.keys) {
                frame.variables[name] = value
                isExist = true
                break
            }
        }
        require(isExist) { "Variable $name is not declared" }
    }

    fun withFunction(name: String, value: MyLangParser.LFunContext) {
        require(name !in frame.functions.keys) { "Function $name is already declared" }
        frame.functions[name] = value
    }

    fun findVariable(name: String): Context? {
        for (frame in stack.reversed()) {
            if (name in frame.variables.keys) return frame.variables[name]!!
        }
        return null
    }

    fun findFunction(name: String): MyLangParser.LFunContext? {
        for (frame in stack.reversed()) {
            if (name in frame.functions.keys) return frame.functions[name]!!
        }
        return null
    }

    inline fun <T> withFrame(body: () -> T): T {
        stack.push(Frame())
        return try {
            body()
        } finally {
            stack.pop()
        }
    }
}
