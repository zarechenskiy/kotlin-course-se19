package ru.hse.spb

import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import ru.hse.spb.parser.FunBaseVisitor
import ru.hse.spb.parser.FunLexer
import ru.hse.spb.parser.FunParser
import ru.hse.spb.parser.FunParser.*
import java.io.*

fun main(args: Array<String>) {
    require(args.size == 1) { "Please provide exactly one argument" }
    runInterpreter(CharStreams.fromFileName(args[0]), PrintWriter(System.out))
}

fun runInterpreter(stream: CharStream, writer: Writer) {
    val lexer = FunLexer(stream)
    val parser = FunParser(CommonTokenStream(lexer))
    parser.file().accept(StatementsEvaluationVisitor(writer))
    writer.flush()
}

class StatementsEvaluationVisitor(private val writer: Writer): FunBaseVisitor<Int?>() {
    private val state = State()

    override fun visitBlock(ctx: BlockContext): Int? {
        state.newScope()
        var returnValue: Int? = null
        for (statement in ctx.statements) {
            returnValue = statement.accept(this)
            if (returnValue != null) {
                break
            }
        }
        state.leaveScope()
        return returnValue
    }

    override fun visitStatement(ctx: StatementContext): Int? {
        if (ctx.expression() != null) {
            ctx.expression().accept(this)
            return null
        }
        return ctx.getChild(0).accept(this)
    }

    override fun visitReturnStatement(ctx: ReturnStatementContext): Int {
        return ctx.expression().accept(this)!!
    }

    override fun visitExpression(ctx: ExpressionContext): Int {
        return ctx.binaryExpression().accept(this)!!
    }

    override fun visitBlockWithBraces(ctx: BlockWithBracesContext): Int? {
        return ctx.block().accept(this)
    }

    override fun visitFunction(ctx: FunctionContext): Int? {
        val functionName = ctx.identifier().IDENTIFIER().toString()
        val arguments = ctx.parameterNames().argumentsList
                .map { it.IDENTIFIER().toString() }
        if (functionName == "println") {
            throw IllegalFunctionName("println")
        }
        state.addFunction(functionName,
                Function(state.scopes.lastIndex, arguments, ctx.blockWithBraces(), this))
        return null
    }

    override fun visitFunctionCall(ctx: FunctionCallContext): Int {
        val functionName = ctx.identifier().IDENTIFIER().toString()
        val argumentsList = ctx.arguments()
                .argumentsList.map { it.accept(this)!! }
        if (functionName == "println") {
            writer.write(argumentsList.joinToString(separator = " "))
            writer.write("\n")
            return 0
        }
        return state.invokeFunction(functionName, argumentsList)
    }

    override fun visitWhileStatement(ctx: WhileStatementContext): Int? {
        while (ctx.expression().accept(this) != 0) {
            val returnValue = ctx.blockWithBraces().accept(this)
            if (returnValue != null) {
                return returnValue
            }
        }
        return null
    }

    override fun visitAssignment(ctx: AssignmentContext): Int? {
        state.setVariable(ctx.identifier().IDENTIFIER().toString(),
                ctx.expression().accept(this)!!)
        return null
    }

    override fun visitVariable(ctx: VariableContext): Int? {
        state.addVariable(ctx.identifier().IDENTIFIER().toString(),
                ctx.expression()?.accept(this) ?: 0)
        return null
    }

    override fun visitIdentifier(ctx: IdentifierContext): Int {
        if (ctx.unaryMinus() != null) {
            return ctx.unaryMinus().accept(this)!!
        }
        val identifierName = ctx.IDENTIFIER().toString()
        return state.getVariable(identifierName)
    }

    override fun visitLiteral(ctx: LiteralContext): Int {
        return ctx.LITERAL().toString().toInt()
    }

    override fun visitUnaryMinus(ctx: UnaryMinusContext): Int {
        val identifierName = ctx.IDENTIFIER().toString()
        return -state.getVariable(identifierName)
    }

    override fun visitIfStatement(ctx: IfStatementContext): Int? {
        val result = ctx.expression().accept(this)!!
        return if (result != 0) {
            ctx.blockWithBraces().accept(this)
        } else {
            ctx.elseBlock()?.blockWithBraces()?.accept(this)
        }
    }

    private fun doOperation(ctx: ParserRuleContext): Int {
        return if (ctx.childCount == 5) {
            val leftValue: Int = ctx.getChild(0).accept(this)!!
            val rightValue: Int = ctx.getChild(4).accept(this)!!
            when (ctx.getChild(2).text) {
                "||" -> if (leftValue != 0 || rightValue != 0) 1 else 0
                "&&" -> if (leftValue != 0 && rightValue != 0) 1 else 0
                "==" -> if (leftValue == rightValue) 1 else 0
                "!=" -> if (leftValue != rightValue) 1 else 0
                ">=" -> if (leftValue >= rightValue) 1 else 0
                "<=" -> if (leftValue <= rightValue) 1 else 0
                ">" -> if (leftValue > rightValue) 1 else 0
                "<" -> if (leftValue < rightValue) 1 else 0
                "+" -> leftValue + rightValue
                "-" -> leftValue - rightValue
                "*" -> leftValue * rightValue
                "/" -> leftValue / rightValue
                "%" -> leftValue % rightValue
                else -> throw UnsupportedBinaryOperation(ctx.getChild(2).text)
            }
        } else {
            ctx.getChild(0).accept(this)!!
        }
    }

    override fun visitBinaryExpression(ctx: BinaryExpressionContext): Int = doOperation(ctx)
    override fun visitLessOrBinaryExpression(ctx: LessOrBinaryExpressionContext): Int = doOperation(ctx)
    override fun visitLessAndBinaryExpression(ctx: LessAndBinaryExpressionContext): Int = doOperation(ctx)
    override fun visitLessCompareBinaryExpression(ctx: LessCompareBinaryExpressionContext): Int = doOperation(ctx)
    override fun visitLessSubAddBinaryExpression(ctx: LessSubAddBinaryExpressionContext): Int = doOperation(ctx)
    override fun visitLessEqualityBinaryExpression(ctx: LessEqualityBinaryExpressionContext): Int = doOperation(ctx)
    override fun visitNonBinaryExpression(ctx: NonBinaryExpressionContext): Int {
        if (ctx.expression() != null) {
            return ctx.expression().accept(this)!!
        }
        return ctx.getChild(0).accept(this)!!
    }
}
