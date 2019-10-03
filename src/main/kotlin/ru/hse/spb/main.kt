package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.hse.spb.parser.FunPLBaseVisitor
import ru.hse.spb.parser.FunPLLexer
import ru.hse.spb.parser.FunPLParser
import java.util.*

fun getGreeting(): String {
    val words = mutableListOf<String>()
    words.add("Hello,")
    words.add("world!")

    return words.joinToString(separator = " ")
}

var oldProgram = "fun a(b) {if (a > 0) {a(0)}}"

var program = """
    |var a = 10
    |var b = 20
    |if (a > b) {
        |println(1)
    |} else {
        |println(0)
    |}
""".trimMargin()

fun main() {
    val lexer = FunPLLexer(CharStreams.fromString(program))
    FunPLParser(CommonTokenStream(lexer)).file().accept(StatementsEvaluationVisitor())


    println(CommonTokenStream(lexer).tokens)
    println(getGreeting())
}

object StackFrames {
    var functions = mutableListOf<MutableMap<String, FunPLParser.FunctionContext>>()
    var variables = mutableListOf<MutableMap<String, Int>>()
    var returnedFromFunction = false
}

class StatementsEvaluationVisitor : FunPLBaseVisitor<Int>() {
    override fun visitFile(ctx: FunPLParser.FileContext): Int {
        StackFrames.functions.add(mutableMapOf())
        StackFrames.variables.add(mutableMapOf())
        return ctx.block().accept(this)
    }

    override fun visitBlock(ctx: FunPLParser.BlockContext): Int {

        for (statement in ctx.statement()) {
            val statementValue = statement.accept(this)
            if (statement.returnExp() != null) {
                StackFrames.returnedFromFunction = true
                return statementValue
            }
        }
        return 0
    }

    override fun visitBlockWithBraces(ctx: FunPLParser.BlockWithBracesContext): Int {
        StackFrames.functions.add(mutableMapOf())
        StackFrames.variables.add(mutableMapOf())
        val valueInBlock = ctx.block().accept(this)
        StackFrames.functions.removeAt(StackFrames.functions.size - 1)
        StackFrames.variables.removeAt(StackFrames.variables.size - 1)
        return valueInBlock
    }

    override fun visitStatement(ctx: FunPLParser.StatementContext): Int {
        val concreteContext = with (ctx) {
            function()
                    ?: variable()
                    ?: expression()
                    ?: whileExp()
                    ?: ifExp()
                    ?: assignment()
                    ?: returnExp()
        }
        return if (concreteContext == null) {
            0
        } else {
            concreteContext.accept(this)
        }
    }

    override fun visitFunction(ctx: FunPLParser.FunctionContext): Int {
        val currentFrameNumber = StackFrames.functions.size - 1
        val functionName = ctx.IDENTIFIER().text
        if (StackFrames.functions[currentFrameNumber].containsKey(functionName)) {
            error("Function with name $functionName is already defined")
        }
        StackFrames.functions[currentFrameNumber][functionName] = ctx
        return 0
    }

    override fun visitVariable(ctx: FunPLParser.VariableContext): Int {
        val currentFrameNumber = StackFrames.variables.size - 1
        val variableName = ctx.IDENTIFIER().text
        if (StackFrames.variables[currentFrameNumber].containsKey(variableName)) {
            error ("Variable with name $variableName is already defined")
        }
        val expressionValue = ctx.expression().accept(this)
        StackFrames.variables[currentFrameNumber][variableName] = expressionValue
        return expressionValue
    }

    override fun visitWhileExp(ctx: FunPLParser.WhileExpContext): Int {
        while (ctx.expression().accept(this) > 0) {
            val valueInBlock = ctx.blockWithBraces().accept(this)
            if (StackFrames.returnedFromFunction) {
                return valueInBlock
            }
        }
        return 0
    }

    override fun visitIfExp(ctx: FunPLParser.IfExpContext): Int {
        val conditionValue = ctx.expression().accept(this)
        if (conditionValue > 0) {
            ctx.blockWithBraces()[0].accept(this)
        } else if (ctx.blockWithBraces()[1] != null) {
            ctx.blockWithBraces()[1].accept(this)
        }
        return 0
    }

    override fun visitAssignment(ctx: FunPLParser.AssignmentContext): Int {
        var variableIsDefined = false
        val variableName = ctx.IDENTIFIER().text
        var containgMap = StackFrames.variables.size - 1
        for (varMap in StackFrames.variables.asReversed()) {

            if (varMap.containsKey(variableName)) {
                variableIsDefined = true

                break
            }
            containgMap--
        }
        if (!variableIsDefined) {
            error("Variable $variableName is not defined")
        }
        StackFrames.variables[containgMap][variableName] = ctx.expression().accept(this)
        return 0
    }

    override fun visitReturnExp(ctx: FunPLParser.ReturnExpContext): Int {
        val value = ctx.expression().accept(this)
        StackFrames.returnedFromFunction = true
        return value
    }

    override fun visitExpression(ctx: FunPLParser.ExpressionContext): Int {
        val concreteExpression = with (ctx) {
            binaryExpression()
                    ?: nonBinaryExpression()
        }
        return concreteExpression.accept(this)
    }

    override fun visitNonBinaryExpression(ctx: FunPLParser.NonBinaryExpressionContext): Int {
        if (ctx.functionCall() != null) {
            return ctx.functionCall().accept(this)
        }
        if (ctx.IDENTIFIER() != null) {
            val identifierName = ctx.IDENTIFIER().text
            for (varMap in StackFrames.variables.asReversed()) {
                if (varMap.containsKey(identifierName)) {
                    return varMap[identifierName] ?: 0
                }
            }
            error("Variable $identifierName is not defined")
        }
        if (ctx.LITERAL() != null) {
            return ctx.LITERAL().text.toInt()
        }
        if (ctx.expression() != null) {
            return ctx.expression().accept(this)
        }
        return 0
    }

    override fun visitFunctionCall(ctx: FunPLParser.FunctionCallContext): Int {
        val functionName = ctx.IDENTIFIER().text
        for (funMap in StackFrames.functions.asReversed()) {
            if (funMap.containsKey(functionName)) {
                val functionContext = funMap[functionName]
                val parameterNamesContext = functionContext!!.parameterNames()
                StackFrames.functions.add(mutableMapOf())
                val parameters = mutableMapOf<String, Int>()
                StackFrames.variables.add(parameters)
                for ((counter, identifier) in parameterNamesContext.IDENTIFIER().withIndex()) {
                    parameters[identifier.text] = ctx.arguments().expression()[counter].accept(this)
                }
                val valueInFunction = functionContext.blockWithBraces().accept(this)
                StackFrames.returnedFromFunction = false
                return valueInFunction
            }
        }
        error("Function $functionName is not defined")
    }

    override fun visitBinaryExpression(ctx: FunPLParser.BinaryExpressionContext): Int {
        return ctx.orExpression().accept(this)
    }

    override fun visitOrExpression(ctx: FunPLParser.OrExpressionContext): Int {
        if (ctx.orExpression() != null) {
            return if (ctx.andExpression().accept(this) > 0 || ctx.orExpression().accept(this) > 0) {
                1
            } else {
                0
            }
        }
        return ctx.andExpression().accept(this)
    }

    override fun visitAndExpression(ctx: FunPLParser.AndExpressionContext): Int {
        if (ctx.andExpression() != null) {
            return if (ctx.eqExpression().accept(this) > 0 && ctx.andExpression().accept(this) > 0) {
                1
            } else {
                0
            }
        }
        return ctx.eqExpression().accept(this)
    }

    override fun visitEqExpression(ctx: FunPLParser.EqExpressionContext): Int {
        if (ctx.ruleIndex == 0) {
            return ctx.lessMoreExpression().accept(this)
        }
        return if ((ctx.lessMoreExpression().accept(this) > 0 == ctx.eqExpression().accept(this) > 0) ==
                (ctx.ruleIndex == 1)) {
                1
            } else {
                0
            }
    }

    override fun visitLessMoreExpression(ctx: FunPLParser.LessMoreExpressionContext): Int {
        return when (ctx.ruleIndex) {
            0 -> ctx.addExpression().accept(this)
            1 -> if (ctx.addExpression().accept(this) < ctx.lessMoreExpression().accept(this)) {
                1
            } else {
                0
            }
            2 -> if (ctx.addExpression().accept(this) <= ctx.lessMoreExpression().accept(this)) {
                1
            } else {
                0
            }
            3 -> if (ctx.addExpression().accept(this) > ctx.lessMoreExpression().accept(this)) {
                1
            } else {
                0
            }
            else -> if (ctx.addExpression().accept(this) >= ctx.lessMoreExpression().accept(this)) {
                1
            } else {
                0
            }
        }
    }

    override fun visitAddExpression(ctx: FunPLParser.AddExpressionContext): Int {
        return when (ctx.ruleIndex) {
            0 -> ctx.multExpression().accept(this)
            1 -> ctx.multExpression().accept(this) + ctx.addExpression().accept(this)
            else -> ctx.multExpression().accept(this) - ctx.addExpression().accept(this)
        }
    }

    override fun visitMultExpression(ctx: FunPLParser.MultExpressionContext): Int {
        return when (ctx.ruleIndex) {
            0 -> ctx.nonBinaryExpression().accept(this)
            1 -> ctx.nonBinaryExpression().accept(this) * ctx.multExpression().accept(this)
            2 -> ctx.nonBinaryExpression().accept(this) / ctx.multExpression().accept(this)
            else -> ctx.nonBinaryExpression().accept(this) % ctx.multExpression().accept(this)
        }
    }
}
