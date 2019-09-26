package ru.hse.spb.anstkras

class FrameContext {
    private val frames = mutableListOf<Frame>()

    fun setShouldReturn() {
        frames.last().shouldReturn = true
    }

    fun addNewFrame() {
        frames.add(Frame())
    }

    fun removeFrame() {
        frames.removeAt(frames.size - 1)
    }

    fun addVar(varName: String, value: Int) {
        if (frames.last().vars.containsKey(varName)) {
            throw IllegalStateException("$varName already defined in this block")
        }
        frames.last().vars[varName] = value
    }

    fun findVar(varName: String): Int {
        for (i in frames.size - 1 downTo 0) {
            if (frames[i].vars.containsKey(varName)) {
                return frames[i].vars[varName]!!
            }
        }
        throw IllegalStateException("$varName was not defined")
    }

    fun addFunction(funName: String, value: () -> Int) {
        if (frames.last().functions.containsKey(funName)) {
            throw IllegalStateException("$funName already defined in this block")
        }
        frames.last().functions[funName] = value
    }

    fun findFunction(funName: String): () -> Int {
        for (i in frames.size - 1 downTo 0) {
            if (frames[i].functions.containsKey(funName)) {
                return frames[i].functions[funName]!!
            }
        }
        throw IllegalStateException("$funName was not defined")
    }

    fun addArgs(funName: String, args: List<String>) {
        if (frames.last().functionsArgs.containsKey(funName)) {
            throw IllegalStateException("$funName already defined in this block")
        }
        frames.last().functionsArgs[funName] = args
    }

    fun findArgs(funName: String): List<String> {
        for (i in frames.size - 1 downTo 0) {
            if (frames[i].functionsArgs.containsKey(funName)) {
                return frames[i].functionsArgs[funName]!!
            }
        }
        throw IllegalStateException("$funName was not defined")
    }

    fun shouldReturn(): Boolean = frames.last().shouldReturn

    fun changeVar(varName: String, res: Int) {
        for (i in frames.size - 1 downTo 0) {
            if (frames[i].vars.containsKey(varName)) {
                frames[i].vars[varName] = res
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