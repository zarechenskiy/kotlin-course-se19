package ru.hse.spb

import ru.hse.spb.parser.LangBaseVisitor
import ru.hse.spb.parser.LangParser

/**
 * Visitor for evaluating the grammar block
 * */
class BlockVisitor(): LangBaseVisitor<Void?>() {

    private var context = ExecutionContext()

    constructor(context: ExecutionContext): this() {
        this.context = context
    }

    /**
     * Visits every statement consistently until one of them returns a value
     * or until they run out
     * */
    override fun visitBlock(ctx: LangParser.BlockContext?): Void? {
        if (ctx == null) {
            context.resultValue = 0
            return null
        }
        for (s in ctx.statement()) {
            s.accept(StatementVisitor(context))
            if (context.resultValue != null) {
                return null
            }
        }
        return null
    }
}
