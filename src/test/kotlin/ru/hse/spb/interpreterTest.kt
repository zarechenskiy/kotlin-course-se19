package ru.hse.spb

import org.antlr.v4.runtime.CommonToken
import org.antlr.v4.runtime.tree.TerminalNodeImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.hse.spb.parser.FunLanguageParser.*
import java.io.ByteArrayOutputStream
import java.io.PrintWriter

class InterpreterTest {
    /**
     * manually creates AST of the following code:
     *
     * var a = 3
     * fun func() {
     *      a = 4
     * }
     * func()
     * println(a)
     *
     * and testes interpreter on the AST
     */
    @Test
    fun test() {
        val fileContext = FileContext(null, -1)
        val blockContext = BlockContext(null, -1)
        fileContext.addAnyChild(blockContext)

        val functionBlockContext = BlockContext(null, -1)
        functionBlockContext.addAnyChild(createVariableOrAssignmentStatement("a", "4", true))

        blockContext.addAnyChild(createVariableOrAssignmentStatement("a", "3", false))
        blockContext.addAnyChild(createFunctionStatement("func", functionBlockContext))
        blockContext.addAnyChild(createFunctionCallStatement("func", ArgumentsContext(null, -1)))

        val argumentsContext = ArgumentsContext(null, -1)
        val expressionContext = ExpressionContext(null, -1)
        val simpleExpressionContext = SimpleExpressionContext(null, -1)
        simpleExpressionContext.addAnyChild(TerminalNodeImpl(CommonToken(IDENTIFIER, "a")))
        simpleExpressionContext.start = CommonToken(IDENTIFIER, "ignored")
        argumentsContext.addAnyChild(expressionContext)
        expressionContext.addAnyChild(simpleExpressionContext)

        blockContext.addAnyChild(createFunctionCallStatement("println", argumentsContext))

        val byteArray = ByteArrayOutputStream()
        PrintWriter(byteArray).use {
            fileContext.accept(FunLanguageVisitorImplementation(it))
        }
        assertEquals("4\n", byteArray.toString())
    }

    private fun createVariableOrAssignmentStatement(name: String,
                                                    value: String,
                                                    isAssignment: Boolean): StatementContext {
        val statement = StatementContext(null, -1)
        val variableContext = if (isAssignment) {
            AssignmentContext(null, -1)
        } else {
            VariableContext(null, -1)
        }

        val expressionContext = ExpressionContext(null, -1)
        val simpleExpressionContext = SimpleExpressionContext(null, -1)
        statement.addAnyChild(variableContext)
        variableContext.addAnyChild(TerminalNodeImpl(CommonToken(IDENTIFIER, name)))
        variableContext.addAnyChild(expressionContext)
        expressionContext.addAnyChild(simpleExpressionContext)
        simpleExpressionContext.addAnyChild(TerminalNodeImpl(CommonToken(LITERAL, value)))
        return statement
    }

    private fun createFunctionStatement(name: String, block: BlockContext): StatementContext {
        val statement = StatementContext(null, -1)
        val functionContext = FunctionContext(null, -1)
        val parameterNamesContext = ParameterNamesContext(null, -1)
        val blockWithBracesContext = BlockWithBracesContext(null, -1)
        statement.addAnyChild(functionContext)
        functionContext.addAnyChild(TerminalNodeImpl(CommonToken(IDENTIFIER, name)))
        functionContext.addAnyChild(parameterNamesContext)
        blockWithBracesContext.addAnyChild(block)
        functionContext.addAnyChild(blockWithBracesContext)
        return statement
    }

    private fun createFunctionCallStatement(name: String, arguments: ArgumentsContext): StatementContext {
        val statementContext = StatementContext(null, -1)
        val functionCallContext = FunctionCallContext(null, -1)
        functionCallContext.addAnyChild(TerminalNodeImpl(CommonToken(IDENTIFIER, name)))
        functionCallContext.addAnyChild(arguments)
        statementContext.addAnyChild(functionCallContext)
        return statementContext
    }
}