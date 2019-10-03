package ru.hse.spb

import org.antlr.v4.runtime.ParserRuleContext
import ru.hse.spb.parser.FunCallBaseVisitor
import ru.hse.spb.parser.FunCallParser

class Visitor(private var currentScope: Scope = Scope.createRootScope()) : FunCallBaseVisitor<Unit>() {

    var returnValue = 0
        private set

    var itsTimeToStop = false
    override fun visitFile(ctx: FunCallParser.FileContext) {
        ctx.block().accept(this)
    }

    override fun visitBlock(ctx: FunCallParser.BlockContext) {
        for (statement in ctx.statement()) {
            try {
                statement.accept(this)
            } catch (e: Exception) {
                val line = statement.start.line
                throw InterpreterException(line, "Error on line $line:", e)
            }
        }
    }

    override fun visitBlockWithBraces(ctx: FunCallParser.BlockWithBracesContext) {
        currentScope = Scope(currentScope)
        ctx.block().accept(this)
        currentScope = currentScope.parent ?: error("Unexpected throw out scope")
    }

    override fun visitStatement(ctx: FunCallParser.StatementContext) {
        if (itsTimeToStop)
            return

        val type = with(ctx) {
            assignment()
                    ?: expression()
                    ?: function()
                    ?: ifBlock()
                    ?: returnBlock()
                    ?: whileBlock()
                    ?: variable()
                    ?: error("Unknown statement")
        }
        type.accept(this)
    }

    override fun visitFunction(ctx: FunCallParser.FunctionContext) {
        val name = ctx.IDENTIFIER().text
        val args = ctx.parameterNames()?.IDENTIFIER()?.map { it.text } ?: emptyList()
        val block = ctx.blockWithBraces().block()
        currentScope.defFun(name, UserFunction(currentScope.copy(), name, args, block))
    }

    override fun visitVariable(ctx: FunCallParser.VariableContext) {
        val name = ctx.IDENTIFIER().text
        val value = ctx.expression()?.let { calc(it) } ?: 0

        currentScope.defVar(name, value)
    }

    override fun visitWhileBlock(ctx: FunCallParser.WhileBlockContext) {
        while (calc(ctx.expression()) != 0) {
            ctx.blockWithBraces().accept(this)
        }
    }

    override fun visitIfBlock(ctx: FunCallParser.IfBlockContext) {
        if (calc(ctx.expression()) != 0) {
            ctx.blockWithBraces(0).accept(this)
        } else {
            ctx.blockWithBraces(1)?.accept(this)
        }
    }

    override fun visitAssignment(ctx: FunCallParser.AssignmentContext) {
        val name = ctx.IDENTIFIER().text
        val value = calc(ctx.expression())
        if (!currentScope.assignVar(name, value)) {
            error("Variable $name is undefined")
        }

    }

    override fun visitExpression(ctx: FunCallParser.ExpressionContext) {
        val op = ctx.op?.text
        val expr = ctx.expression().filterNotNull()
        returnValue = with(ctx) {
            NUMBER_LITERAL()?.text?.toInt()
                    ?: NUMBER_LITERAL()?.text?.toInt()
                    ?: IDENTIFIER()?.text?.let { currentScope.getVar(it) }
                    ?: op?.let { applyOperator(it, { this@Visitor.calc(expr[0]) }, { this@Visitor.calc(expr[1]) }) }
                    ?: expr.getOrNull(0)?.let { this@Visitor.calc(it) }
                    ?: functionCall()?.let { this@Visitor.calc(it) }
                    ?: error("Unknown expression")
        }
    }

    override fun visitFunctionCall(ctx: FunCallParser.FunctionCallContext) {
        val name = ctx.IDENTIFIER().text
        val args = ctx.arguments().expression().map { this.calc(it) }
        returnValue = currentScope.getFun(name)?.apply(args) ?: error("Function $name is undefined")
    }

    override fun visitReturnBlock(ctx: FunCallParser.ReturnBlockContext) {
        ctx.expression().accept(this)
        itsTimeToStop = true
    }

    private fun calc(ctx: ParserRuleContext) = this.also { ctx.accept(this) }.returnValue

    companion object {
        inline fun applyOperator(str: String, left: () -> Int, right: () -> Int): Int = when (str) {
            "*" -> left() * right()
            "/" -> left() / right()
            "%" -> left() % right()
            "+" -> left() + right()
            "-" -> left() - right()
            "<" -> if (left() < right()) 1 else 0
            "<=" -> if (left() <= right()) 1 else 0
            ">=" -> if (left() >= right()) 1 else 0
            ">" -> if (left() > right()) 1 else 0
            "==" -> if (left() == right()) 1 else 0
            "!=" -> if (left() != right()) 1 else 0
            "&&" -> if (left() != 0 && right() != 0) 1 else 0
            "||" -> if (left() != 0 || right() != 0) 1 else 0
            else -> error("Unknown operator")
        }
    }
//    override fun visitFile(ctx: FileContext) {
//        ctx.block().accept(this)
//    }
//
//    override fun visitBlock(ctx: BlockContext) {
//        for (statement in ctx.statements) {
//            statement.accept(this)
//        }
//    }
//
//    override fun visitStatement(ctx: StatementContext) {
//        ctx.functionCall().accept(this)
//    }
//
//    override fun visitFunctionCall(ctx: FunctionCallContext) {
//        val functionName = ctx.functionName()
//        val kFunction = BuiltIns.functions[functionName] ?: error("No such function: $functionName")
//
//        val arguments = ctx.arguments.map { transformArgument(it) }
//        kFunction.call(*arguments.toTypedArray())
//    }
//
//    private fun transformArgument(argument: ArgumentContext): Any {
//        return with(argument) {
//            argumentName { BuiltIns.properties[this]?.call() }
//                ?: numberLiteral { this.toDoubleOrNull() }
//                ?: error("Unexpected argument: ${argument.text}")
//        }
//    }
}