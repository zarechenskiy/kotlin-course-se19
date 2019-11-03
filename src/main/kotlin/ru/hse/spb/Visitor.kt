package ru.hse.spb

import ru.hse.spb.parser.LangBaseVisitor
import ru.hse.spb.parser.LangLexer
import ru.hse.spb.parser.LangParser

class BlockVisitor : LangBaseVisitor<Block>() {

    // block: (statement)*
    override fun visitBlock(ctx: LangParser.BlockContext): Block {
        return Block(ctx.statement().map { StatementVisitor().visitStatement(it) })
    }

    // blockWithBraces: '{' block '}'
    override fun visitBlockWithBraces(ctx: LangParser.BlockWithBracesContext): Block {
        return visitBlock(ctx.block())
    }

    // file: block
    override fun visitFile(ctx: LangParser.FileContext): Block {
        return visitBlock(ctx.block())
    }
}

class StatementVisitor : LangBaseVisitor<Statement>() {

    // function: 'fun' IDENTIFIER '(' parameterNames ')' blockWithBraces
    override fun visitFunction(ctx: LangParser.FunctionContext): Statement {
        return Function(ctx.IDENTIFIER().text, ctx.parameterNames().IDENTIFIER().map { it.text }, BlockVisitor().visitBlockWithBraces(ctx.blockWithBraces()))
    }

    // variable: 'var' IDENTIFIER ('=' expression)?
    override fun visitVariable(ctx: LangParser.VariableContext): Statement {
        return Variable(ctx.IDENTIFIER().text, ExpressionVisitor().visitExpression(ctx.expression()))
    }

    override fun visitExpression(ctx: LangParser.ExpressionContext): Statement {
        return ExpressionVisitor().visitExpression(ctx)
    }

    // while_: 'while' '(' expression ')' blockWithBraces
    override fun visitWhile_(ctx: LangParser.While_Context): Statement {
        return While(ExpressionVisitor().visitExpression(ctx.expression()), BlockVisitor().visitBlockWithBraces(ctx.blockWithBraces()))
    }

    // if_: 'if' '(' expression ')' blockWithBraces ('else' blockWithBraces)?
    override fun visitIf_(ctx: LangParser.If_Context): Statement {
        if (ctx.elseBody == null) {
            return If(ExpressionVisitor().visitExpression(ctx.expression()), BlockVisitor().visitBlockWithBraces(ctx.thenBody), Block(emptyList()))
        }
        return If(ExpressionVisitor().visitExpression(ctx.expression()), BlockVisitor().visitBlockWithBraces(ctx.thenBody), BlockVisitor().visitBlockWithBraces(ctx.elseBody))
    }

    // assignment: IDENTIFIER '=' expression
    override fun visitAssignment(ctx: LangParser.AssignmentContext): Statement {
        return Assignment(ctx.IDENTIFIER().text, ExpressionVisitor().visitExpression(ctx.expression()))
    }

    // return_: 'return' expression
    override fun visitReturn_(ctx: LangParser.Return_Context): Statement {
        return Return(ExpressionVisitor().visitExpression(ctx.expression()))
    }
}

class ExpressionVisitor : LangBaseVisitor<Expression>() {

    // functionCall: IDENTIFIER '(' arguments ')'
    override fun visitFunctionCall(ctx: LangParser.FunctionCallContext): Expression {
        return FunctionCall(ctx.IDENTIFIER().text, ctx.arguments().expression().map { ExpressionVisitor().visitExpression(it) })
    }

    override fun visitIdentifier(ctx: LangParser.IdentifierContext): Expression {
        return Identifier(ctx.IDENTIFIER().text)
    }

    override fun visitLiteral(ctx: LangParser.LiteralContext): Expression {
        return Literal(ctx.LITERAL().text.toInt())
    }

    override fun visitNonBinaryExpression(ctx: LangParser.NonBinaryExpressionContext): Expression {
        return when {
            ctx.functionCall() != null -> ExpressionVisitor().visitFunctionCall(ctx.functionCall())
            ctx.identifier() != null -> ExpressionVisitor().visitIdentifier(ctx.identifier())
            ctx.literal() != null -> ExpressionVisitor().visitLiteral(ctx.literal())
            else -> ExpressionVisitor().visitExpression(ctx.expression())
        }
    }

    override fun visitBinaryOrExpression(ctx: LangParser.BinaryOrExpressionContext): Expression {
        return if (ctx.op != null) {
            BinaryExpression(ExpressionVisitor().visitBinaryOrExpression(ctx.l), ExpressionVisitor().visitBinaryAndExpression(ctx.r), BinaryOperator.OR)
        } else {
            ExpressionVisitor().visitBinaryAndExpression(ctx.single)
        }
    }

    override fun visitBinaryAndExpression(ctx: LangParser.BinaryAndExpressionContext): Expression {
        return if (ctx.op != null) {
            BinaryExpression(ExpressionVisitor().visitBinaryAndExpression(ctx.l), ExpressionVisitor().visitBinaryEqualExpression(ctx.r), BinaryOperator.AND)
        } else {
            ExpressionVisitor().visitBinaryEqualExpression(ctx.single)
        }
    }

    override fun visitBinaryEqualExpression(ctx: LangParser.BinaryEqualExpressionContext): Expression {
        return if (ctx.op != null) {
            BinaryExpression(ExpressionVisitor().visitBinaryLessExpression(ctx.l), ExpressionVisitor().visitBinaryLessExpression(ctx.r),
                    when (ctx.op.text) {
                        "==" -> BinaryOperator.EQ
                        "!=" -> BinaryOperator.NEQ
                        else -> throw RuntimeException("Invalid operator $ctx.op")
                    }
            )
        } else {
            ExpressionVisitor().visitBinaryLessExpression(ctx.single)
        }
    }

    override fun visitBinaryLessExpression(ctx: LangParser.BinaryLessExpressionContext): Expression {
        return if (ctx.op != null) {
            BinaryExpression(ExpressionVisitor().visitBinaryPlusExpression(ctx.l), ExpressionVisitor().visitBinaryPlusExpression(ctx.r),
                    when (ctx.op.text) {
                        "<=" -> BinaryOperator.LE
                        ">=" -> BinaryOperator.GE
                        "<"  -> BinaryOperator.LT
                        ">"  -> BinaryOperator.GT
                        else -> throw RuntimeException("Invalid operator $ctx.op")
                    }
            )
        } else {
            ExpressionVisitor().visitBinaryPlusExpression(ctx.single)
        }
    }

    override fun visitBinaryPlusExpression(ctx: LangParser.BinaryPlusExpressionContext): Expression {
        return if (ctx.op != null) {
            BinaryExpression(ExpressionVisitor().visitBinaryPlusExpression(ctx.l), ExpressionVisitor().visitBinaryMultExpression(ctx.r),
                    when (ctx.op.text) {
                        "+" -> BinaryOperator.PLUS
                        "-" -> BinaryOperator.MINUS
                        else -> throw RuntimeException("Invalid operator $ctx.op")
                    }
            )
        } else {
            ExpressionVisitor().visitBinaryMultExpression(ctx.single)
        }
    }

    override fun visitBinaryMultExpression(ctx: LangParser.BinaryMultExpressionContext): Expression {
        return if (ctx.op != null) {
            BinaryExpression(ExpressionVisitor().visitBinaryMultExpression(ctx.l), ExpressionVisitor().visitNonBinaryExpression(ctx.r),
                    when (ctx.op.text) {
                        "*" -> BinaryOperator.MULT
                        "/" -> BinaryOperator.DIV
                        "%" -> BinaryOperator.MOD
                        else -> throw RuntimeException("Invalid operator $ctx.op")
                    }
            )
        } else {
            ExpressionVisitor().visitNonBinaryExpression(ctx.single)
        }
    }


}