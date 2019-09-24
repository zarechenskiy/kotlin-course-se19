package ru.hse.spb

import org.antlr.v4.runtime.tree.TerminalNode
import ru.hse.spb.parser.LangBaseVisitor
import ru.hse.spb.parser.LangParser

class CustomVisitor<T> : LangBaseVisitor<T>() {

    override fun visitFile(ctx: LangParser.FileContext?): T {
        if (ctx != null) {
            println(LangParser.ruleNames[ctx.ruleIndex])
        }
        return super.visitFile(ctx)
    }

    override fun visitStatement(ctx: LangParser.StatementContext?): T {
        if (ctx != null) {
            println(LangParser.ruleNames[ctx.ruleIndex])
        }
        return super.visitStatement(ctx)
    }

    override fun visitBlock(ctx: LangParser.BlockContext?): T {
        if (ctx != null) {
            println(LangParser.ruleNames[ctx.ruleIndex])
        }
        return super.visitBlock(ctx)
    }

    override fun visitBlock_with_braces(ctx: LangParser.Block_with_bracesContext?): T {
        if (ctx != null) {
            println(LangParser.ruleNames[ctx.ruleIndex])
        }
        return super.visitBlock_with_braces(ctx)
    }

    override fun visitFunction(ctx: LangParser.FunctionContext?): T {
        if (ctx != null) {
            println(LangParser.ruleNames[ctx.ruleIndex])
        }
        return super.visitFunction(ctx)
    }

    override fun visitVariable(ctx: LangParser.VariableContext?): T {
        if (ctx != null) {
            println(LangParser.ruleNames[ctx.ruleIndex])
        }
        return super.visitVariable(ctx)
    }

    override fun visitParameter_names(ctx: LangParser.Parameter_namesContext?): T {
        if (ctx != null) {
            println(LangParser.ruleNames[ctx.ruleIndex])
        }
        return super.visitParameter_names(ctx)
    }

    override fun visitWhile_expr(ctx: LangParser.While_exprContext?): T {
        if (ctx != null) {
            println(LangParser.ruleNames[ctx.ruleIndex])
        }
        return super.visitWhile_expr(ctx)
    }

    override fun visitIf_expr(ctx: LangParser.If_exprContext?): T {
        if (ctx != null) {
            println(LangParser.ruleNames[ctx.ruleIndex])
        }
        return super.visitIf_expr(ctx)
    }

    override fun visitReturn_expr(ctx: LangParser.Return_exprContext?): T {
        if (ctx != null) {
            println(LangParser.ruleNames[ctx.ruleIndex])
        }
        return super.visitReturn_expr(ctx)
    }

    override fun visitExpression(ctx: LangParser.ExpressionContext?): T {
        if (ctx != null) {
            println(LangParser.ruleNames[ctx.ruleIndex])
        }
        return super.visitExpression(ctx)
    }

    override fun visitFunction_call(ctx: LangParser.Function_callContext?): T {
        if (ctx != null) {
            println(LangParser.ruleNames[ctx.ruleIndex])
        }
        return super.visitFunction_call(ctx)
    }

    override fun visitArguments(ctx: LangParser.ArgumentsContext?): T {
        if (ctx != null) {
            println(LangParser.ruleNames[ctx.ruleIndex])
        }
        return super.visitArguments(ctx)
    }

    override fun visitBinary_expression(ctx: LangParser.Binary_expressionContext?): T {
        if (ctx != null) {
            println(LangParser.ruleNames[ctx.ruleIndex])
        }
        return super.visitBinary_expression(ctx)
    }

    override fun visitAssignment(ctx: LangParser.AssignmentContext?): T {
        if (ctx != null) {
            println(LangParser.ruleNames[ctx.ruleIndex])
        }
        return super.visitAssignment(ctx)
    }

    override fun visitTerminal(node: TerminalNode?): T {
        var symbol = node?.symbol
        if (symbol != null) {
            println("terminal !!! ${LangParser.tokenNames[symbol.type]} ${symbol.text} ${symbol.line}:${symbol.startIndex}")
        }
        return super.visitTerminal(node)
    }
}
