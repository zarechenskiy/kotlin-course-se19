package ru.hse.spb

import ru.hse.spb.parser.LangBaseVisitor
import ru.hse.spb.parser.LangParser

class BlockVisitor(): LangBaseVisitor<Int>() {

    private var context = ExecutionContext()

    constructor(context: ExecutionContext): this() {
        this.context = context
    }

    override fun visitBlock(ctx: LangParser.BlockContext?): Int {
        if (ctx == null) {
            return 0
        }
        for (s in ctx.statement()) {
            val result = s.accept(StatementVisitor(context))
            if (result != null) {
                return result
            }
        }
        return 0
    }
}
