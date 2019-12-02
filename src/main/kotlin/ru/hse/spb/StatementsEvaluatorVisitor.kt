package ru.hse.spb

import org.antlr.v4.runtime.ParserRuleContext
import ru.hse.spb.parser.FunInterpreterBaseVisitor
import ru.hse.spb.parser.FunInterpreterParser


class StatementsEvaluatorVisitor(funcs: Map<String, Function<Int>>) :
        FunInterpreterBaseVisitor<Any>() {

    private var curState = State().apply {funcs.forEach {addFunc(it.key, it.value)}}

    override fun visitFile(ctx: FunInterpreterParser.FileContext) {
        ctx.block().accept(this)
    }

    override fun visitBlock(ctx: FunInterpreterParser.BlockContext) {
        ctx.statements.forEach { if (!curState.isReturned) it.accept(this) }
    }

    override fun visitBlockWithBraces(ctx: FunInterpreterParser.BlockWithBracesContext) {
        val prevState = curState
        val newState = State(curState)
        curState = newState
        ctx.block().accept(this)
        curState = prevState
        curState.returnValue = newState.returnValue
        curState.isReturned = newState.isReturned
    }

    override fun visitWhileStatement(ctx: FunInterpreterParser.WhileStatementContext) {
        while (ctx.expression().accept(this) as Int != 0) {
            ctx.blockWithBraces().accept(this)
        }
    }

    override fun visitIfStatement(ctx: FunInterpreterParser.IfStatementContext) {
        if (ctx.expression().accept(this) != 0) {
            ctx.thenBlock().accept(this)
        } else {
            if (ctx.elseBlock() != null) {
                ctx.elseBlock().accept(this)
            }
        }
    }

    override fun visitAssignment(ctx: FunInterpreterParser.AssignmentContext) {
        val variableName = ctx.IDENTIFIER().text
        val value: Int = ctx.expression().accept(this) as Int? ?: 0
        curState.setVariable(variableName, value)
    }

    override fun visitReturnStatement(ctx: FunInterpreterParser.ReturnStatementContext) {
        val returnResult = ctx.expression().accept(this) as Int?
        curState.returnValue = returnResult
        curState.isReturned = true
    }

    override fun visitFunction(ctx: FunInterpreterParser.FunctionContext) {
        val funName = ctx.IDENTIFIER().text
        val args = ctx.functionArgumentNames().parameters.map { it.text }
        val block = ctx.blockWithBraces().block() ?: error("Block shouldn't be null")
        curState.addFunc(funName, CodeFunction<Int>(block, args, this))
    }

    override fun visitVariableAssign(ctx: FunInterpreterParser.VariableAssignContext) {
        val variableName = ctx.IDENTIFIER().text
        val value: Int = ctx.expression().accept(this) as Int? ?: 0
        curState.newVariable(variableName, value)
    }

    override fun visitVariable(ctx: FunInterpreterParser.VariableContext): Int? {
        val variableName = ctx.IDENTIFIER().text
        return curState.getVariable(variableName)
                ?: error("Unknown variable '$variableName' at ${lineNumberString(ctx)}")
    }

    override fun visitCompExpression(ctx: FunInterpreterParser.CompExpressionContext): Int {
        if (ctx.OP_COMP().size > 0) {
            return processBinaryOperator(
                    ctx.OP_COMP().map { it.text },
                    ctx.addExpression()
            ) { operator, prev, cur ->
                when (operator) {
                    "<" -> if (prev < cur) 1 else 0
                    "<=" -> if (prev <= cur) 1 else 0
                    "==" -> if (prev == cur) 1 else 0
                    ">=" -> if (prev >= cur) 1 else 0
                    ">" -> if (prev > cur) 1 else 0
                    else -> error("Unknown operator '$operator' at ${lineNumberString(ctx)}")
                }
            }
        }
        return ctx.addExpression(0).accept(this) as Int
    }

    private fun processBinaryOperator(operators: List<String>, rules: List<ParserRuleContext>,
                                      processFunc: (String, Int, Int) -> Int): Int {
        val operationsIter = operators.iterator()
        return rules.map { it.accept(this) as Int }.foldr1 { prev, cur ->
            processFunc(operationsIter.next(), prev, cur)
        }
    }

    override fun visitLogicalExpression(ctx: FunInterpreterParser.LogicalExpressionContext): Int {
        if (ctx.OP_LOGICAL().size > 0) {
            return processBinaryOperator(
                    ctx.OP_LOGICAL().map { it.text },
                    ctx.compExpression()
            ) { operator, cur, prev ->
                when (operator) {
                    "||" -> if (prev != 0 || cur != 0) 1 else 0
                    "&&" -> if (prev != 0 && cur != 0) 1 else 0
                    else -> error("Unknown operator '$operator' at ${lineNumberString(ctx)}")
                }
            }
        }
        return ctx.compExpression(0).accept(this) as Int
    }

    override fun visitAddExpression(ctx: FunInterpreterParser.AddExpressionContext): Int {
        if (ctx.OP_ADD().size > 0) {
            return processBinaryOperator(
                    ctx.OP_ADD().map { it.text },
                    ctx.multExpression()
            ) { operator, cur, prev ->
                when (operator) {
                    "+" -> cur + prev
                    "-" -> cur - prev
                    else -> error("Unknown operator '$operator' at ${lineNumberString(ctx)}")
                }
            }
        }
        return ctx.multExpression(0).accept(this) as Int
    }

    override fun visitMultExpression(ctx: FunInterpreterParser.MultExpressionContext): Int {
        if (ctx.OP_MULT().size > 0) {
            return processBinaryOperator(
                    ctx.OP_MULT().map { it.text },
                    ctx.justExpression()
            ) { operator, cur, prev ->
                when (operator) {
                    "*" -> prev * cur
                    "/" -> prev / cur
                    "%" -> prev % cur
                    else -> error("Unknown operator ''$operator'' at ${lineNumberString(ctx)}")
                }
            }
        }
        return ctx.justExpression(0).accept(this) as Int
    }

    private fun executeFunction(func: Function<Int>, args: List<Int>): Any? {
        val functionArgs = func.argsList()
        val prevState = curState
        val newState = State(curState)
        curState = newState
        functionArgs.zip(args).forEach { curState.newVariable(it.first, it.second) }
        func.compute(curState)
        curState = prevState
        return newState.returnValue ?: 0
    }

    override fun visitFunctionCall(ctx: FunInterpreterParser.FunctionCallContext): Any? {
        val funcName = ctx.IDENTIFIER().text
        val args: List<Int> = ctx.funcitonArguments().arguments.map { it.accept(this) as Int }
        if (funcName == "println") {
            args.forEach { println(it) }
            return 0
        }
        val func = curState.getFunc(funcName)
        if (func != null) {
            if (func.argsList().size != args.size) {
                error("Wrong number of arguments at ${lineNumberString(ctx)}")
            }
            val res = executeFunction(func, args)
            return res
        } else {
            error("Unknown function $funcName at ${lineNumberString(ctx)}")
        }
    }

    override fun visitNumber(ctx: FunInterpreterParser.NumberContext): Int {
        return ctx.NUMBER_LITERAL().text.toInt()
    }

    override fun visitBracketsExpression(ctx: FunInterpreterParser.BracketsExpressionContext): Any {
        return ctx.expression().accept(this)
    }

    private fun lineNumberString(ctx: ParserRuleContext): String {
        return "line number ${ctx.start.line}"
    }
}

private fun <T> Iterable<T>.foldr1(func: (acc: T, T) -> T): T {
    val iterator = this.iterator()
    var acc = iterator.next()
    while (iterator.hasNext()) {
        acc = func(acc, iterator.next())
    }
    return acc
}