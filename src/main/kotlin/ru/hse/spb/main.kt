package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.hse.spb.parser.FunCallBaseVisitor
import ru.hse.spb.parser.FunCallLexer
import ru.hse.spb.parser.FunCallParser
import ru.hse.spb.parser.FunCallParser.*
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties

private val program = """
    |foo(PI,)
    |bar()
    |sum(1, 2,)
""".trimMargin()

fun main() {
    val lexer = FunCallLexer(CharStreams.fromString(program))
    val parser = FunCallParser(CommonTokenStream(lexer))

    parser.file().accept(StatementsEvaluationVisitor())
}

class StatementsEvaluationVisitor : FunCallBaseVisitor<Unit>() {
    override fun visitFile(ctx: FileContext) {
        ctx.block().accept(this)
    }

    override fun visitBlock(ctx: BlockContext) {
        for (statement in ctx.statements) {
            statement.accept(this)
        }
    }

    override fun visitStatement(ctx: StatementContext) {
        ctx.functionCall().accept(this)
    }

    override fun visitFunctionCall(ctx: FunctionCallContext) {
        val functionName = ctx.functionName()
        val kFunction = BuiltIns.functions[functionName] ?: error("No such function: $functionName")

        val arguments = ctx.arguments.map { transformArgument(it) }
        kFunction.call(*arguments.toTypedArray())
    }

    private fun transformArgument(argument: ArgumentContext): Any {
        return with(argument) {
            argumentName { BuiltIns.properties[this]?.call() }
                ?: numberLiteral { this.toDoubleOrNull() }
                ?: error("Unexpected argument: ${argument.text}")
        }
    }
}

object BuiltIns {
    object Functions {
        fun foo(d: Double) {
            println("It's $d")
        }

        fun bar() {
            println("From bar")
        }

        fun sum(a: Double, b: Double) {
            println("Sum of $a and $b is ${a + b}")
        }
    }

    object Properties {
        const val PI = 3.14
    }

    class BuiltInFunction(private val kFunction: KFunction<*>) {
        fun call(vararg arguments: Any?) {
            kFunction.call(Functions, *arguments)
        }
    }

    val functions = Functions::class
            .declaredMemberFunctions
            .associate { it.name to BuiltInFunction(it) }

    val properties = Properties::class
            .declaredMemberProperties
            .associateBy { it.name }
}
private fun FunctionCallContext.functionName() = IDENTIFIER().text
private inline fun ArgumentContext.argumentName(f: String.() -> Any?) = IDENTIFIER()?.text?.f()
private inline fun ArgumentContext.numberLiteral(f: String.() -> Any?) = NUMBER_LITERAL()?.text?.f()
