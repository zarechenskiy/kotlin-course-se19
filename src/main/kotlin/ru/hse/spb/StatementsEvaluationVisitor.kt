package ru.hse.spb

import ru.hse.spb.parser.FunPLBaseVisitor
import ru.hse.spb.parser.FunPLParser

class StatementsEvaluationVisitor : FunPLBaseVisitor<Int>() {
    override fun visitFile(ctx: FunPLParser.FileContext): Int {
        StackFrames.functions.add(mutableMapOf())
        StackFrames.variables.add(mutableMapOf())
        return ctx.block().accept(this)
    }

    override fun visitBlock(ctx: FunPLParser.BlockContext): Int {

        for (statement in ctx.statement()) {
            val statementValue = statement.accept(this)
            if (StackFrames.returnedFromFunction) {
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
            val lineNumber = ctx.start.line
            error("Function with name $functionName is already defined (line $lineNumber)")
        }
        StackFrames.functions[currentFrameNumber][functionName] = ctx
        return 0
    }

    override fun visitVariable(ctx: FunPLParser.VariableContext): Int {
        val currentFrameNumber = StackFrames.variables.size - 1
        val variableName = ctx.IDENTIFIER().text
        if (StackFrames.variables[currentFrameNumber].containsKey(variableName)) {
            val lineNumber = ctx.start.line
            error ("Variable with name $variableName is already defined (line $lineNumber)")
        }
        val expressionValue = ctx.expression().accept(this)
        StackFrames.variables[currentFrameNumber][variableName] = expressionValue
        return expressionValue
    }

    override fun visitWhileExp(ctx: FunPLParser.WhileExpContext): Int {
        while (ctx.expression().accept(this) != 0) {
            val valueInBlock = ctx.blockWithBraces().accept(this)
            if (StackFrames.returnedFromFunction) {
                return valueInBlock
            }
        }
        return 0
    }

    override fun visitIfExp(ctx: FunPLParser.IfExpContext): Int {
        val conditionValue = ctx.expression().accept(this)
        var valueInIf = 0
        if (conditionValue != 0) {
            valueInIf = ctx.branch1.accept(this)
        } else if (ctx.branch2 != null) {
            valueInIf = ctx.branch2.accept(this)
        }
        if (StackFrames.returnedFromFunction) {
            return valueInIf
        }
        return 0
    }

    override fun visitAssignment(ctx: FunPLParser.AssignmentContext): Int {
        val variableName = ctx.IDENTIFIER().text
        val variableValue = ctx.expression().accept(this)
        val lineNumber = ctx.start.line
        StackFrames.getVariable(variableName)?.setValue(variableValue)
                ?: error("Variable $variableName is not defined (line $lineNumber)")
        if (StackFrames.returnedFromFunction) {
            return variableValue
        }
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
            val lineNumber = ctx.start.line
            error("Variable $identifierName is not defined (line $lineNumber)")
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
        val functionContext = StackFrames.getFunction(functionName)?.value
        if (functionContext != null) {
            val parameterNamesContext = functionContext.parameterNames()
            StackFrames.functions.add(mutableMapOf())
            val parameters = mutableMapOf<String, Int>()
            StackFrames.variables.add(parameters)
            for ((counter, identifier) in parameterNamesContext.IDENTIFIER().withIndex()) {
                parameters[identifier.text] = ctx.arguments().expression()[counter].accept(this)
            }
            val valueInFunction = functionContext.blockWithBraces().accept(this)
            StackFrames.returnedFromFunction = false
            StackFrames.functions.removeAt(StackFrames.functions.size - 1)
            StackFrames.variables.removeAt(StackFrames.variables.size - 1)
            return valueInFunction
        }
        if (functionName != "println") {
            val lineNumber = ctx.start.line
            error("Function $functionName is not defined (line $lineNumber)")
        }
        val argumentValues = ctx.arguments().expression().map{it.accept(this)}
        val joined = argumentValues.joinToString(separator = " ")
        println(joined)
        return 0
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

        if (ctx.lm_exp0 != null) {
            return ctx.lm_exp0.accept(this)
        }
        if (ctx.lm_exp1 != null) {
            return if (ctx.lm_exp1.accept(this) > 0 == ctx.eq_exp1.accept(this) > 0) {
                1
            } else {
                0
            }
        }
        return if (ctx.lm_exp2.accept(this) > 0 != ctx.eq_exp2.accept(this) > 0) {
            1
        } else {
            0
        }
    }

    override fun visitLessMoreExpression(ctx: FunPLParser.LessMoreExpressionContext): Int {
        if (ctx.add_exp0 != null) {
            return ctx.add_exp0.accept(this)
        }
        if (ctx.add_exp1 != null) {
            return if (ctx.add_exp1.accept(this) < ctx.lm_exp1.accept(this)) {
                1
            } else {
                0
            }
        }
        if (ctx.add_exp2 != null) {
            return if (ctx.add_exp2.accept(this) <= ctx.lm_exp2.accept(this)) {
                1
            } else {
                0
            }
        }
        if (ctx.add_exp3 != null) {
            return if (ctx.add_exp3.accept(this) > ctx.lm_exp3.accept(this)) {
                1
            } else {
                0
            }
        }
        return if (ctx.add_exp4.accept(this) >= ctx.lm_exp4.accept(this)) {
            1
        } else {
            0
        }
    }

    override fun visitAddExpression(ctx: FunPLParser.AddExpressionContext): Int {
        if (ctx.mult_exp0 != null) {
            return ctx.mult_exp0.accept(this)
        }
        if (ctx.mult_exp1 != null) {
            return ctx.mult_exp1.accept(this) + ctx.add_exp1.accept(this)
        }
        return ctx.mult_exp2.accept(this) - ctx.add_exp2.accept(this)
    }

    override fun visitMultExpression(ctx: FunPLParser.MultExpressionContext): Int {
        if (ctx.nb_exp0 != null) {
            return ctx.nb_exp0.accept(this)
        }
        if (ctx.nb_exp1 != null) {
            return ctx.nb_exp1.accept(this) * ctx.mult_exp1.accept(this)
        }
        if (ctx.nb_exp2 != null) {
            return ctx.nb_exp2.accept(this) / ctx.mult_exp2.accept(this)
        }
        return ctx.nb_exp3.accept(this) % ctx.mult_exp3.accept(this)
    }
}