package ru.hse.spb.anstkras

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.hse.spb.parser.FunInterpreterBaseVisitor
import ru.hse.spb.parser.FunInterpreterLexer
import ru.hse.spb.parser.FunInterpreterParser
import java.io.File

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Enter file name with program.")
        return
    }
    val fileName = args[0]
    val program = File(fileName).readText(Charsets.UTF_8)

    try {
        runProgram(program)
    } catch (e: IllegalStateException) {
        println(e.message)
    }
}

private val frameContext = FrameContext()

fun runProgram(program: String) {
    val lexer = FunInterpreterLexer(CharStreams.fromString(program))
    val parser = FunInterpreterParser(CommonTokenStream(lexer))
    parser.file().accept(StatementsEvaluationVisitor())
}

class StatementsEvaluationVisitor : FunInterpreterBaseVisitor<Int>() {

    override fun visitFile(ctx: FunInterpreterParser.FileContext): Int {
        frameContext.addNewFrame()
        val result = ctx.block().accept(this)
        frameContext.removeFrame()
        return result
    }

    override fun visitBlock(ctx: FunInterpreterParser.BlockContext): Int {
        for (statement in ctx.statement()) {
            val result = statement.accept(this)
            if (frameContext.shouldReturn()) {
                return result
            }
        }
        return 0
    }

    override fun visitBlockWithBraces(ctx: FunInterpreterParser.BlockWithBracesContext): Int {
        return ctx.block().accept(this)
    }

    override fun visitStatement(ctx: FunInterpreterParser.StatementContext): Int {
        return (ctx.variable()?.accept(this) ?: ctx.ifStatement()?.accept(this) ?: ctx.expression()?.accept(this)
        ?: ctx.function()?.accept(this) ?: ctx.whileStatement()?.accept(this)
        ?: ctx.assignment()?.accept(this) ?: ctx.returnStatement()?.accept(this))!!
    }

    override fun visitLogicExpr(ctx: FunInterpreterParser.LogicExprContext): Int {
        var result = ctx.compareExpr().accept(this)
        val ops = ctx.LOGIC_OPS()
        val exps = ctx.logicExpr()
        for (i in 0 until ops.size) {
            when (ops[i].text) {
                "||" -> result = (result.toBoolean() || exps[i].accept(this).toBoolean()).toInt()
                "&&" -> result = (result.toBoolean() && exps[i].accept(this).toBoolean()).toInt()
            }
        }
        return result
    }

    override fun visitCompareExpr(ctx: FunInterpreterParser.CompareExprContext): Int {
        val left = ctx.expr(0).accept(this)
        if (ctx.COMP_OPS() == null) {
            return left
        }
        val right = ctx.expr(1).accept(this)
        val op = ctx.COMP_OPS()
        when (op.text) {
            ">" -> return (left > right).toInt()
            "<" -> return (left < right).toInt()
            ">=" -> return (left >= right).toInt()
            "<=" -> return (left <= right).toInt()
            "==" -> return (left == right).toInt()
            "!=" -> return (left != right).toInt()

        }
        throw IllegalStateException("Unexpected operation")
    }

    override fun visitExpr(ctx: FunInterpreterParser.ExprContext): Int {
        var result = ctx.term().accept(this)
        val ops = ctx.PLUS_MINUS()
        val exps = ctx.expr()
        for (i in 0 until ops.size) {
            when (ops[i].text) {
                "+" -> result += exps[i].accept(this)
                "-" -> result -= exps[i].accept(this)
            }
        }
        return result
    }

    override fun visitTerm(ctx: FunInterpreterParser.TermContext): Int {
        var result = ctx.factor().accept(this)
        val ops = ctx.MULT_DIVIDE()
        val exps = ctx.term()
        for (i in 0 until ops.size) {
            when (ops[i].text) {
                "*" -> result += exps[i].accept(this)
                "/" -> result -= exps[i].accept(this)
                "%" -> result -= exps[i].accept(this)
            }
        }
        return result
    }

    override fun visitFactor(ctx: FunInterpreterParser.FactorContext): Int {
        return ctx.variableName()?.let { frameContext.findVar(it) } ?: ctx.literalValue()
        ?: ctx.expression()?.accept(this)
        ?: ctx.functionCall()?.accept(this)!!
    }

    override fun visitFunction(ctx: FunInterpreterParser.FunctionContext): Int {
        val funName = ctx.functionName()
        val parameters = mutableListOf<String>()
        for (param in ctx.parameterNames().parameters) {
            parameters.add(param.text)
        }
        frameContext.addArgs(funName, parameters)
        frameContext.addFunction(funName) { ctx.blockWithBraces().accept(this) ?: 0 }
        return 0
    }

    override fun visitVariable(ctx: FunInterpreterParser.VariableContext): Int {
        val result = ctx.expression().accept(this)
        frameContext.addVar(ctx.variableName() ?: error("error"), result)
        return result
    }

    override fun visitWhileStatement(ctx: FunInterpreterParser.WhileStatementContext): Int {
        while (ctx.whileExpr.accept(this) == 1) {
            val result = ctx.blockWithBraces().accept(this)
            if (frameContext.shouldReturn()) {
                return result
            }
        }
        return 0
    }

    override fun visitIfStatement(ctx: FunInterpreterParser.IfStatementContext): Int {
        return if (ctx.ifExpr.accept(this) == 1) {
            ctx.trueBlock.accept(this)
        } else {
            ctx.falseBlock?.accept(this) ?: 0
        }
    }

    override fun visitAssignment(ctx: FunInterpreterParser.AssignmentContext): Int {
        val varName = ctx.variableName()
        val result = ctx.expression().accept(this)
        frameContext.changeVar(varName, result)
        return result
    }

    override fun visitReturnStatement(ctx: FunInterpreterParser.ReturnStatementContext): Int {
        frameContext.setShouldReturn()
        return ctx.expression().accept(this)
    }

    override fun visitExpression(ctx: FunInterpreterParser.ExpressionContext): Int {
        return ctx.logicExpr().accept(this)
    }

    override fun visitFunctionCall(ctx: FunInterpreterParser.FunctionCallContext): Int {
        val functionName = ctx.functionName()
        val arguments = ctx.arguments().args.map { it.accept(this) }
        if (functionName == "println") {
            println(arguments.joinToString(" "))
            return 0
        }
        frameContext.addNewFrame()
        for (i in 0 until arguments.size) {
            frameContext.addVar(frameContext.findArgs(functionName)[i], arguments[i])
        }
        val function = frameContext.findFunction(functionName)
        val res = function()
        frameContext.removeFrame()
        return res
    }
}

private fun FunInterpreterParser.VariableContext.variableName() = IDENTIFIER()?.text
private fun FunInterpreterParser.FunctionCallContext.functionName() = IDENTIFIER().text
private fun FunInterpreterParser.FunctionContext.functionName() = IDENTIFIER().text
private fun FunInterpreterParser.AssignmentContext.variableName() = IDENTIFIER().text
private fun FunInterpreterParser.FactorContext.variableName() = IDENTIFIER()?.text
private fun FunInterpreterParser.FactorContext.literalValue() = literal()?.text?.toIntOrNull()
private fun Boolean.toInt() = if (this) 1 else 0
private fun Int.toBoolean() = this != 0
