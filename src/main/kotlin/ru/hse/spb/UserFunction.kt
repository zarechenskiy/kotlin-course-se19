package ru.hse.spb

import ru.hse.spb.parser.FunCallParser

interface Function {
    val name: String
    fun apply(args: List<Int>): Int

}

class UserFunction(
        private val scope: Scope,
        override val name: String,
        private val argNames: List<String>,
        private val block: FunCallParser.BlockContext
) : Function {

    override fun apply(args: List<Int>): Int {
        if (args.size != argNames.size)
            error("$name expected ${argNames.size} arguments, got ${args.size}")

        val scopeWithArgs = scope.copy()
        argNames.zip(args).forEach { (name, value) -> scopeWithArgs.defVar(name, value) }
        val visitor = Visitor(scopeWithArgs)
        block.accept(visitor)
        return visitor.returnValue
    }
}

object Std {
    object PrintLn : Function {
        override val name = "println"
        override fun apply(args: List<Int>): Int {
            println(args.joinToString(" "))
            return 0
        }

    }
}