package ru.hse.spb

import ru.hse.spb.parser.FunInterpreterBaseVisitor
import ru.hse.spb.parser.FunInterpreterParser

class CodeFunction<R>(val block: FunInterpreterParser.BlockContext,
                      val argNames: List<String>,
                      val visitor: FunInterpreterBaseVisitor<Any>) : Function<R> {
    override fun compute(state: State): R {
        return block.accept(visitor) as R
    }

    override fun argsList(): List<String> {
        return argNames
    }
}