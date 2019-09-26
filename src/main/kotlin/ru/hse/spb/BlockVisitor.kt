package ru.hse.spb

import ru.hse.spb.parser.LangBaseVisitor
import ru.hse.spb.parser.LangParser

class BlockVisitor(): LangBaseVisitor<Void?>() {

    private var context = ExecutionContext()

    constructor(context: ExecutionContext): this() {
        this.context = context
    }

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
