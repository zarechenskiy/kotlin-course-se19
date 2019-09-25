package ru.hse.spb

import ru.hse.spb.parser.LangBaseVisitor
import ru.hse.spb.parser.LangParser

class ArgumentsVisitor(): LangBaseVisitor<List<Int>>() {

    private var context = ExecutionContext()

    constructor(context: ExecutionContext): this() {
        this.context = context
    }

    override fun visitArguments(ctx: LangParser.ArgumentsContext?): List<Int> {
        return ctx?.expression()!!.map { arg -> arg.accept(ExpressionVisitor(context.copy())) }
    }
}
