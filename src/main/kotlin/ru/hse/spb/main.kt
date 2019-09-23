package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import ru.hse.spb.parser.FunGrammarBaseVisitor
import ru.hse.spb.parser.FunGrammarLexer
import ru.hse.spb.parser.FunGrammarParser
import java.util.*

private val testProgram = """
    2 + 3 * 4
""".trimIndent()

fun main(args: Array<String>) {
    val lexer = FunGrammarLexer(CharStreams.fromString(testProgram))
    val parser = FunGrammarParser(CommonTokenStream(lexer))

    parser.file().accept(EvaluationVisitor())

}


class EvaluationVisitor : FunGrammarBaseVisitor<Unit>() {
    val scope = Scope<ParseTree>()

    private inline fun withNewScopeDo(f : () -> Unit) {
        scope.enterScope()
        f()
        scope.exitScope()
    }

    override fun visitFile(ctx: FunGrammarParser.FileContext) {
        ctx.block().accept(this)
    }

    override fun visitBlock_with_braces(ctx: FunGrammarParser.Block_with_bracesContext) {
        ctx.block().accept(this)
    }

    override fun visitBlock(ctx: FunGrammarParser.BlockContext) {
        withNewScopeDo { ctx.statement().forEach { it.accept(this) } }
    }

    override fun visitStatement(ctx: FunGrammarParser.StatementContext) {
        ctx.getChild(0).accept(this)  // statement has only one child
    }

    override fun visitFunction(ctx: FunGrammarParser.FunctionContext) {
        val functionName = ctx.identifier().IDENTIFIER().symbol.text
        
    }
}
