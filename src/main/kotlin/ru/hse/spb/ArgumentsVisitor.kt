package ru.hse.spb

import ru.hse.spb.parser.LangBaseVisitor
import ru.hse.spb.parser.LangParser

/**
 * Visitor for evaluating values of function arguments
 * */
class ArgumentsVisitor(): LangBaseVisitor<List<Int>>() {

    private var context = ExecutionContext()

    constructor(context: ExecutionContext): this() {
        this.context = context
    }

    /**
     * Executes evaluating expression of arguments consistently
     *
     * @return evaluated arguments values
     * */
    override fun visitArguments(ctx: LangParser.ArgumentsContext?): List<Int> {
        return ctx!!.expression()!!.map { arg -> arg.accept(ExpressionVisitor(context.copy())) }
    }
}
