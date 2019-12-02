package ru.hse.spb

import ru.hse.spb.parser.LangBaseVisitor
import ru.hse.spb.parser.LangParser

/**
 * Visitor for evaluating values of function arguments
 * */
class ArgumentsVisitor(private var context: ExecutionContext = ExecutionContext()): LangBaseVisitor<List<Int>>() {

    /**
     * Executes evaluating expression of arguments consistently
     *
     * @return evaluated arguments values
     * */
    override fun visitArguments(ctx: LangParser.ArgumentsContext): List<Int> {
        return ctx.expression()!!.map { arg -> arg.accept(ExpressionVisitor(context.copy())) }
    }
}
