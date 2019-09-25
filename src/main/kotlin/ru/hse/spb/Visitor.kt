package ru.hse.spb

import ru.hse.spb.parser.FunCallBaseVisitor
import ru.hse.spb.parser.FunCallParser
import java.lang.IllegalArgumentException
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties

class Space(val parent: Space? = null) {
    private val functions = mutableMapOf<String, FunCallParser.FunctionContext>()
    private val variables = mutableMapOf<String, Int?>()

    fun createChild(): Space {
        return Space(this)
    }

    fun findFunction(name: String): FunCallParser.FunctionContext? {
        return functions[name] ?: parent?.findFunction(name)
    }

    fun rewriteVariable(name: String, value: Int) {
        if (variables.containsKey(name)) {
            variables[name] = value
            return
        }
        parent?.rewriteVariable(name, value) ?: throw IllegalArgumentException()
    }

    fun findVariable(name: String): Int? {
        return variables[name] ?: parent?.findVariable(name)
    }

    fun addFunction(func: FunCallParser.FunctionContext) {
        val name = func.IDENTIFIER().text
        require(findFunction(name) == null)
        functions[name] = func
    }

    fun addVariable(name: String, value: Int?) {
        require(!variables.containsKey(name))
        variables[name] = value
    }

    fun extend(func: FunCallParser.FunctionContext, arguments: List<Int>): Space {
        val argumentNames = func.argumentList().toNames()
        val space = createChild()
        for ((name, exp) in argumentNames.zip(arguments)) {
            space.variables[name] = exp
        }
        return space
    }
}

class Visitor : FunCallBaseVisitor<Unit>() {
    var namespace = Space()
    val stack = mutableListOf<Int>()
    var exit = false

    override fun visitFile(ctx: FunCallParser.FileContext) {
        ctx.block().accept(this)
    }

    override fun visitBlock(ctx: FunCallParser.BlockContext) {
        for (statement in ctx.statement()) {
            statement.accept(this)
            if (exit) {
                exit = false
                return
            }
        }
        if (stack.isEmpty()) {
            stack.add(0)
        }
    }

    override fun visitStatement(ctx: FunCallParser.StatementContext) {
        val list = listOf(ctx.functionCall(),
                ctx.assigment(),
                ctx.derji(),
                ctx.esli(),
                ctx.poka(),
                ctx.variable(), ctx.function(),
                ctx.expression()).filterNotNull()
        visit(list.single())
    }

    override fun visitFunctionCall(ctx: FunCallParser.FunctionCallContext) {
        val functionName = ctx.functionName()

        val arguments = ctx.parameterList().toListArguments().map {
            it.accept(this)
            stack.pop()
        }
        if (functionName == "println") {
            println(arguments.joinToString(" "))
            return
        }
        val function = namespace.findFunction(functionName) ?: throw IllegalArgumentException()
        namespace = namespace.extend(function, arguments)
        function.blockWithBraces().accept(this)
        namespace = namespace.parent ?: throw IllegalArgumentException()
    }

    override fun visitVariable(ctx: FunCallParser.VariableContext) {
        val value = if (ctx.expression() != null) {
            ctx.expression().accept(this)
            stack.pop()
        } else {
            null
        }
        namespace.addVariable(ctx.IDENTIFIER().text, value)
    }

    override fun visitDerji(ctx: FunCallParser.DerjiContext) {
        visit(ctx.expression())
        exit = true
    }

    override fun visitInnerExpression(ctx: FunCallParser.InnerExpressionContext) {
        if (ctx.value() != null) {
            visitValue(ctx.value())
            return
        }
        visitExpression(ctx.expression())
    }

    override fun visitAssigment(ctx: FunCallParser.AssigmentContext) {
        val name = ctx.IDENTIFIER().text
        require(namespace.findVariable(name) != null)
        ctx.expression().accept(this)
        namespace.rewriteVariable(name, stack.pop())
    }

    override fun visitBlockWithBraces(ctx: FunCallParser.BlockWithBracesContext) {
        namespace = namespace.createChild()
        visitBlock(ctx.block())
        namespace = namespace.parent ?: throw IllegalArgumentException()
    }

    override fun visitFunction(ctx: FunCallParser.FunctionContext) {
        namespace.addFunction(ctx)
    }

    override fun visitExpression(ctx: FunCallParser.ExpressionContext) {
        val currentOperation = listOf(ctx.binaryExperession(),
                ctx.innerExpression(), ctx.functionCall()).filterNotNull().singleOrNull() ?: return
        visit(currentOperation)
    }

    override fun visitValue(ctx: FunCallParser.ValueContext) {
        if (ctx.IDENTIFIER() != null) {
            val value = namespace.findVariable(ctx.IDENTIFIER().text) ?: throw  IllegalArgumentException()
            stack.add(value)
            return
        }
        stack.add(ctx.LITERAL().text.toInt())
    }

    override fun visitBinaryExperession(ctx: FunCallParser.BinaryExperessionContext) {
        ctx.left.accept(this)
        val left = stack.pop()
        ctx.right.accept(this)
        val right = stack.pop()
        val result = when (ctx.binaryOperator().text) {
            "+" -> left + right
            "-" -> left - right
            "*" -> left * right
            "/" -> left / right
            "%" -> left % right
            "||" -> (left.toBool() || right.toBool()).toInt()
            "&&" -> (left.toBool() && right.toBool()).toInt()
            "<" -> (left < right).toInt()
            ">" -> (left > right).toInt()
            "==" -> (left == right).toInt()
            "!=" -> (left != right).toInt()
            ">=" -> (left >= right).toInt()
            "<=" -> (left <= right).toInt()
            else -> throw IllegalArgumentException()
        }
        stack.add(result)

    }

    override fun visitEsli(ctx: FunCallParser.EsliContext) {

        ctx.expression().accept(this)
        if (stack.pop().toBool()) {
            ctx.blockWithBraces(0).accept(this)
        } else if (ctx.blockWithBraces(1) != null) {
            ctx.blockWithBraces(1).accept(this)
        }

    }

    override fun visitPoka(ctx: FunCallParser.PokaContext) {
        val condition = ctx.expression()
        while (true) {
            condition.accept(this)
            if (!stack.pop().toBool()) {
                return
            }
            ctx.blockWithBraces().accept(this)
        }

    }
}

private fun FunCallParser.FunctionCallContext.functionName() = IDENTIFIER().text
private fun FunCallParser.ParameterListContext.toListArguments(): List<FunCallParser.ExpressionContext> {
    var currentList = this
    val result = mutableListOf<FunCallParser.ExpressionContext>()
    var expression = currentList.expression()
    while (expression != null) {
        result.add(currentList.expression())
        currentList = currentList.parameterList() ?: break
        expression = currentList.expression()
    }
    return result
}

private fun Int.toBool(): Boolean {
    return this != 0
}

private fun Boolean.toInt(): Int {
    if (this) {
        return 1
    }
    return 0
}

private fun MutableList<Int>.pop(): Int {
    require(this.isNotEmpty())
    val result = this.last()
    this.removeAt(this.lastIndex)
    return result
}

private fun FunCallParser.ArgumentListContext.toNames(): List<String> {
    var currentList = this
    val result = mutableListOf<String>()
    var argument = currentList.IDENTIFIER()
    while (argument != null) {
        result.add(argument.text)
        currentList = currentList.argumentList() ?: break
        argument = currentList.IDENTIFIER()
    }
    return result
}