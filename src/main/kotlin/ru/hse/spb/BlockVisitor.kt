package ru.hse.spb

import ru.hse.spb.parser.LangBaseVisitor
import ru.hse.spb.parser.LangParser

/**
 * Visitor for evaluating the grammar block
 * */
class BlockVisitor(private var context: ExecutionContext = ExecutionContext()): LangBaseVisitor<Void?>() {

    /**
     * Visits every statement consistently until one of them returns a value
     * or until they run out
     * */
    override fun visitBlock(ctx: LangParser.BlockContext): Void? {
        for (s in ctx.statement()) {
            s.accept(StatementVisitor(context))
            if (context.resultValue != null) {
                return null
            }
        }
        return null
    }
}
