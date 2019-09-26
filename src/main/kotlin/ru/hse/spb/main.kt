package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.hse.spb.parser.FunLanguageBaseVisitor
import ru.hse.spb.parser.FunLanguageLexer
import ru.hse.spb.parser.FunLanguageParser
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions

fun main(args: Array<String>) {
    val lexer = FunLanguageLexer(CharStreams.fromFileName(args[0]))
    val parser = FunLanguageParser(CommonTokenStream(lexer))

    print(parser.file().accept(Interpreter()))
}

object BuiltIns {
    object Functions {
        fun println(vararg output: Int) {
            println(output)
        }
    }

    class BuiltInFunction(private val kFunction: KFunction<*>) {
        fun call(vararg arguments: Any?) {
            kFunction.call(Functions, *arguments)
        }
    }

    val functions = Functions::class
            .declaredMemberFunctions
            .associate { it.name to BuiltInFunction(it) }
}

class Interpreter : FunLanguageBaseVisitor<ReturnValue>() {

    private var functionSpace: NameSpace<FunLanguageParser.NameContext,
            Pair<FunLanguageParser.Parameter_namesContext, FunLanguageParser.BlockContext>>? = null
    private var variableSpace: NameSpace<FunLanguageParser.NameContext, Int>? = null

    override fun visitFile(ctx: FunLanguageParser.FileContext) : ReturnValue {
        return ctx.block().accept(this)
    }

    override fun visitBlock(ctx: FunLanguageParser.BlockContext) : ReturnValue {
        try {
            functionSpace = NameSpace(functionSpace)
            variableSpace = NameSpace(variableSpace)
            for (statement in ctx.statements) {
                val value = statement.accept(this)
                if (value.ret) {
                    return value
                }
            }
            return ReturnValue()
        }
        finally {
            variableSpace = variableSpace!!.parent
            functionSpace = functionSpace!!.parent
        }
    }

    override fun visitBlock_with_braces(ctx: FunLanguageParser.Block_with_bracesContext) : ReturnValue {
        return ctx.block().accept(this)
    }

    override fun visitStatement(ctx: FunLanguageParser.StatementContext) : ReturnValue {
        when {
            ctx.assignment() != null -> return ctx.assignment().accept(this)
            ctx.expression() != null -> return ctx.expression().accept(this)
            ctx.function() != null -> return ctx.function().accept(this)
            ctx.r_if() != null -> return ctx.r_if().accept(this)
            ctx.r_return() != null -> return ctx.r_return().accept(this)
            ctx.r_while() != null -> return ctx.r_while().accept(this)
            ctx.variable() != null -> return ctx.variable().accept(this)
        }
        return ReturnValue(false, ret = true)
    }

    override fun visitFunction(ctx: FunLanguageParser.FunctionContext) : ReturnValue {
        if (!functionSpace!!.addValue(ctx.name(), Pair(ctx.parameter_names(), ctx.block_with_braces().block()))) {
            println("Function Definition")
            return ReturnValue(false, ret = true)
        }
        return ReturnValue()
    }

    override fun visitVariable(ctx: FunLanguageParser.VariableContext) : ReturnValue {
        var value = 0
        if (ctx.expression() != null) {
            val res = ctx.expression().accept(this)
            if (res.ret) {
                return res
            }
            value = res.value
        }
        if (!variableSpace!!.addValue(ctx.name(), value)) {
            println("Variable Definition")
            return ReturnValue(false, ret = true)
        }
        return ReturnValue()
    }

    override fun visitR_while(ctx: FunLanguageParser.R_whileContext) : ReturnValue {
        while (true) {
            var ret = ctx.expression().accept(this)
            if (ret.ret) {
                return ret
            }
            if (ret.value == 0) {
                break
            }
            ret = ctx.block_with_braces().accept(this)
            if (ret.ret) {
                return ret
            }
        }
        return ReturnValue()
    }

    override fun visitR_if(ctx: FunLanguageParser.R_ifContext) : ReturnValue {
        val blocks = ctx.block_with_braces()
        val value = ctx.expression().accept(this)
        if (value.ret) {
            return value
        }
        if (value.value != 0) {
            return blocks[0].accept(this)
        }
        else if (blocks.size > 1) {
            return blocks[1].accept(this)
        }
        return ReturnValue()
    }

    override fun visitAssignment(ctx: FunLanguageParser.AssignmentContext) : ReturnValue {
        val value = ctx.expression().accept(this)
        if (value.ret) {
            return value
        }
        if (!variableSpace!!.updateValue(ctx.name(), value.value)) {
            println("Variable not found")
            return ReturnValue(false, ret = true)
        }
        return ReturnValue()
    }

    override fun visitR_return(ctx: FunLanguageParser.R_returnContext): ReturnValue {
        val value = ctx.expression().accept(this)
        if (value.ret) {
            return value
        }
        return ReturnValue(true, ret = true, value = value.value)
    }

    override fun visitExpression(ctx: FunLanguageParser.ExpressionContext): ReturnValue {
        when {
            ctx.function_call() != null -> return ctx.function_call().accept(this)
            ctx.binary_expression() != null -> return ctx.binary_expression().accept(this)
            ctx.name() != null -> return ctx.name().accept(this)
            ctx.constant() != null -> return ctx.constant().accept(this)
            ctx.expression() != null -> return ctx.expression().accept(this)
        }
        return ReturnValue(false, ret = true)
    }

    override fun visitFunction_call(ctx: FunLanguageParser.Function_callContext): ReturnValue {
        val description = functionSpace!!.getValue(ctx.name())
        if (description == null) {
            println("Function definition not found")
            return ReturnValue(false, ret = true)
        }
        val names = description.first.names
        val args = ctx.arguments().expressions
        if (names.size != args.size) {
            println("Arguments amount does not match")
            return ReturnValue(false, ret = true)
        }
        try {
            functionSpace = NameSpace(functionSpace)
            variableSpace = NameSpace(variableSpace)
            for (i in names.indices) {
                val variableValue = variableSpace!!.parent!!.getValue(args[i].name())
                if (variableValue == null) {
                    println("Variable not found")
                    return ReturnValue(false, ret = true)
                }
                variableSpace!!.addValue(names[i], variableValue)
            }
            return description.second.accept(this)
        }
        finally {
            functionSpace = functionSpace!!.parent
            variableSpace = variableSpace!!.parent
        }
    }

    override fun visitName(ctx: FunLanguageParser.NameContext): ReturnValue {
        val variableValue = variableSpace!!.getValue(ctx)
        if (variableValue == null) {
            println("Variable not found")
            return ReturnValue(false, ret = true)
        }
        return ReturnValue(true, ret = false, value = variableValue)
    }

    override fun visitConstant(ctx: FunLanguageParser.ConstantContext): ReturnValue {
        return ReturnValue(true, ret = false, value = ctx.LITERAL().text.toInt())
    }

    override fun visitBinary_expression(ctx: FunLanguageParser.Binary_expressionContext): ReturnValue {
        return ctx.or_expression().accept(this)
    }

    override fun visitOr_expression(ctx: FunLanguageParser.Or_expressionContext): ReturnValue {
        val values = mutableListOf<Boolean>()
        for (expression in ctx.expressions) {
            val value = expression.accept(this)
            if (value.ret) {
                return value
            }
            if (value.value != 0) {
                values.add(true)
            }
            else {
                values.add(false)
            }
        }
        var result = values[0]
        for (i in 1 until values.size) {
            result = result.or(values[i])
        }
        return if (result) {
            ReturnValue(true, ret = false, value = 1)
        }
        else {
            ReturnValue(true, ret = false, value = 0)
        }
    }

    override fun visitAnd_expression(ctx: FunLanguageParser.And_expressionContext): ReturnValue {
        val values = mutableListOf<Boolean>()
        for (expression in ctx.expressions) {
            val value = expression.accept(this)
            if (value.ret) {
                return value
            }
            if (value.value != 0) {
                values.add(true)
            }
            else {
                values.add(false)
            }
        }
        var result = values[0]
        for (i in 1 until values.size) {
            result = result.and(values[i])
        }
        return if (result) {
            ReturnValue(true, ret = false, value = 1)
        }
        else {
            ReturnValue(true, ret = false, value = 0)
        }
    }

    override fun visitEqual_expression(ctx: FunLanguageParser.Equal_expressionContext): ReturnValue {
        val values = mutableListOf<Int>()
        for (expression in ctx.expressions) {
            val value = expression.accept(this)
            if (value.ret) {
                return value
            }
            values.add(value.value)
        }
        var result = values[0]
        for (i in 1 until values.size) {
            when {
                ctx.ops[i - 1].text == "==" -> result = if (result == values[i]) 1 else 0
                ctx.ops[i - 1].text == "!=" -> result = if (result == values[i]) 0 else 1
            }
        }
        return ReturnValue(true, ret = false, value = result)
    }

    override fun visitLess_expression(ctx: FunLanguageParser.Less_expressionContext): ReturnValue {
        val values = mutableListOf<Int>()
        for (expression in ctx.expressions) {
            val value = expression.accept(this)
            if (value.ret) {
                return value
            }
            values.add(value.value)
        }
        var result = values[0]
        for (i in 1 until values.size) {
            when {
                ctx.ops[i - 1].text == "<" -> result = if (result < values[i]) 1 else 0
                ctx.ops[i - 1].text == "<=" -> result = if (result <= values[i]) 1 else 0
                ctx.ops[i - 1].text == ">" -> result = if (result > values[i]) 1 else 0
                ctx.ops[i - 1].text == ">=" -> result = if (result >= values[i]) 1 else 0
            }
        }
        return ReturnValue(true, ret = false, value = result)
    }

    override fun visitSum_expression(ctx: FunLanguageParser.Sum_expressionContext): ReturnValue {
        val values = mutableListOf<Int>()
        for (expression in ctx.expressions) {
            val value = expression.accept(this)
            if (value.ret) {
                return value
            }
            values.add(value.value)
        }
        var result = values[0]
        for (i in 1 until values.size) {
            when {
                ctx.ops[i - 1].text == "+" -> result += values[i]
                ctx.ops[i - 1].text == "-" -> result -= values[i]
            }
        }
        return ReturnValue(true, ret = false, value = result)
    }

    override fun visitMult_expression(ctx: FunLanguageParser.Mult_expressionContext): ReturnValue {
        val values = mutableListOf<Int>()
        for (expression in ctx.expressions) {
            val value = expression.accept(this)
            if (value.ret) {
                return value
            }
            values.add(value.value)
        }
        var result = values[0]
        for (i in 1 until values.size) {
            when {
                ctx.ops[i - 1].text == "*" -> result *= values[i]
                ctx.ops[i - 1].text == "/" -> {
                    if (values[i] == 0) {
                        println("Division by zero")
                        return ReturnValue(false, ret = true)
                    }
                    result /= values[i]
                }
                ctx.ops[i - 1].text == "%" -> {
                    if (values[i] == 0) {
                        println("Division by zero")
                        return ReturnValue(false, ret = true)
                    }
                    result %= values[i]
                }
            }
        }
        return ReturnValue(true, ret = false, value = result)
    }

    override fun visitBase_binary_expression(ctx: FunLanguageParser.Base_binary_expressionContext): ReturnValue {
        when {
            ctx.function_call() != null -> return ctx.function_call().accept(this)
            ctx.name() != null -> return ctx.name().accept(this)
            ctx.constant() != null -> return ctx.constant().accept(this)
            ctx.expression() != null -> return ctx.expression().accept(this)
        }
        return ReturnValue(false, ret = true)
    }
}

class NameSpace<N, V>(val parent: NameSpace<N, V>?) {
    private var valueByName = HashMap<N, V>()

    fun getValue(name: N) : V? {
        if (valueByName.containsKey(name)) {
            return valueByName[name]
        }
        if (parent != null) {
            return parent.getValue(name)
        }
        return null
    }

    fun addValue(name: N, value: V): Boolean {
        if (valueByName.containsKey(name)) {
            return false
        }
        valueByName[name] = value
        return true
    }

    fun updateValue(name: N, value: V): Boolean {
        if (valueByName.containsKey(name)) {
            valueByName[name] = value
            return true
        }
        if (parent != null) {
            return parent.updateValue(name, value)
        }
        return false
    }
}

data class ReturnValue(val ok: Boolean = true, val ret: Boolean = false, val value: Int = 0)