package ru.hse.spb.visitors

import ru.hse.spb.declarations.StatementDeclaration
import ru.hse.spb.parser.FunBaseVisitor
import ru.hse.spb.parser.FunParser

class BlockVisitor : FunBaseVisitor<List<StatementDeclaration>>() {
    override fun visitBlock(ctx: FunParser.BlockContext): List<StatementDeclaration> {
        return ctx.statement().map { StatementVisitor().visit(it) }
    }

    override fun visitBlockWithBraces(ctx: FunParser.BlockWithBracesContext): List<StatementDeclaration> {
        return visit(ctx.block())
    }

    override fun visitFile(ctx: FunParser.FileContext): List<StatementDeclaration> {
        return visit(ctx.block())
    }
}
