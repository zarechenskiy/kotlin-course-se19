package ru.hse.spb

import ru.hse.spb.parser.LangBaseVisitor
import ru.hse.spb.parser.LangParser

class BlockVisitor(): LangBaseVisitor<Int?>() {

    private var context = ExecutionContext()

    constructor(context: ExecutionContext): this() {
        this.context = context
    }

    override fun visitBlock_with_braces(ctx: LangParser.Block_with_bracesContext?): Int? {
        return super.visitBlock_with_braces(ctx)
    }
}
