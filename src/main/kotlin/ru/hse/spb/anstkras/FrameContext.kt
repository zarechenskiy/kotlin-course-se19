package ru.hse.spb.anstkras

class FrameContext {
    private val frames = mutableListOf<Frame>()

    private val lastFrame get() = frames.last()

    fun setShouldReturn() {
        lastFrame.shouldReturn = true
    }

    fun addNewFrame() {
        frames.add(Frame())
    }

    fun removeFrame() {
        frames.removeAt(frames.lastIndex)
    }

    fun addVar(varName: String, value: Int) {
        if (varName in lastFrame.vars) {
            throw IllegalStateException("$varName already defined in this block")
        }
        lastFrame.vars[varName] = value
    }

    fun findVar(varName: String): Int {
        for (frame in frames.asReversed()) {
            if (varName in frame.vars) {
                return frame.vars[varName]!!
            }
        }
        throw IllegalStateException("$varName was not defined")
    }

    fun addFunction(funName: String, value: () -> Int) {
        if (funName in lastFrame.functions) {
            throw IllegalStateException("$funName already defined in this block")
        }
        lastFrame.functions[funName] = value
    }

    fun findFunction(funName: String): () -> Int {
        for (frame in frames.asReversed()) {
            if (funName in frame.functions) {
                return frame.functions[funName]!!
            }
        }
        throw IllegalStateException("$funName was not defined")
    }

    fun addArgs(funName: String, args: List<String>) {
        if (funName in lastFrame.functionsArgs) {
            throw IllegalStateException("$funName already defined in this block")
        }
        lastFrame.functionsArgs[funName] = args
    }

    fun findArgs(funName: String): List<String> {
        for (frame in frames.asReversed()) {
            if (funName in frame.functionsArgs) {
                return frame.functionsArgs[funName]!!
            }
        }
        throw IllegalStateException("$funName was not defined")
    }

    fun shouldReturn(): Boolean = lastFrame.shouldReturn

    fun changeVar(varName: String, res: Int) {
        for (frame in frames.asReversed()) {
            if (varName in frame.vars) {
                frame.vars[varName] = res
            }
        }
    }

    private class Frame {
        val vars = mutableMapOf<String, Int>()
        val functions = mutableMapOf<String, () -> Int>()
        val functionsArgs = mutableMapOf<String, List<String>>()
        var shouldReturn = false
    }
}