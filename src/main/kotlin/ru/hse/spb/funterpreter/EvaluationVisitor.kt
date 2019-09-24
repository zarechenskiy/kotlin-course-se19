package ru.hse.spb.funterpreter

import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.misc.ParseCancellationException
import ru.hse.spb.parser.FunGrammarBaseVisitor
import ru.hse.spb.parser.FunGrammarLexer
import ru.hse.spb.parser.FunGrammarParser
import java.lang.ArithmeticException

fun runProgram(program: CharStream): ProgramResult {
    val lexer = FunGrammarLexer(program).apply {
        removeErrorListeners()
        addErrorListener(object : BaseErrorListener() {
            override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int, msg: String, e: RecognitionException?) {
                throw ParseCancellationException("line $line:$charPositionInLine $msg")
            }
        })
    }
    val parser = FunGrammarParser(CommonTokenStream(lexer))
    val visitor = EvaluationVisitor()
    parser.file().accept(visitor)
    return ProgramResult(visitor)
}

fun runProgram(program: String): ProgramResult = runProgram(CharStreams.fromString(program))

internal class EvaluationVisitor : FunGrammarBaseVisitor<Unit> {
    private val scope : ValueScope

    var returned: Value? = null
        private set
    var exception : FunException? = null // we should exit as soon as it isn't null
        private set
    var value : Value = IntValue()
        private set

    constructor(parent : EvaluationVisitor) : this(parent.scope)

    constructor(parentScope : ValueScope) : super() {
        scope = parentScope.childScope()
    }

    constructor() : super() {
        scope = Scope()
    }

    override fun visitFile(ctx: FunGrammarParser.FileContext) {
        ctx.block().accept(this)
    }

    override fun visitStatement(ctx: FunGrammarParser.StatementContext) {
        ctx.getChild(0).accept(this)  // statement has only one child
    }

    override fun visitBlock_with_braces(ctx: FunGrammarParser.Block_with_bracesContext) {
        ctx.block().accept(this)
    }

    override fun visitBlock(ctx: FunGrammarParser.BlockContext) {
        for (statement in ctx.statement()) {
            statement.accept(this)
            var shouldStop = returned != null
            exception?.backtrace?.push("\tat line ${statement.getStart().line}: ${statement.text}")?.let { shouldStop = true }
            if (shouldStop) {
                break
            }
        }
    }

    override fun visitFunction(ctx: FunGrammarParser.FunctionContext) {
        scope.create(ctx.identifier().text, Function(scope, ctx)) // function overloading is permitted
    }

    override fun visitVariable(ctx: FunGrammarParser.VariableContext) {
        val variableName = ctx.identifier().text
        val variableValue : Value = if (ctx.expression() != null) {
            ctx.expression().accept(this)
            if (exception != null)
                return
            this.value
        } else {
            IntValue(0)
        }
        scope.create(variableName, variableValue)
    }

    override fun visitL_while(ctx: FunGrammarParser.L_whileContext) {
        val condition = ctx.expression()
        val blockVisitor = childVisitor()
        while (checkCondition(condition) && blockVisitor.exception == null) {
            ctx.block_with_braces().accept(blockVisitor)
        }
        exception = exception ?: blockVisitor.exception
    }

    override fun visitL_if(ctx: FunGrammarParser.L_ifContext) {
        val condition = ctx.expression()
        val blockVisitor = childVisitor()
        if (checkCondition(condition)) {
            ctx.block_with_braces(0).accept(blockVisitor)
        } else {
            if (exception != null) {
                return
            }
            ctx.block_with_braces(1).accept(blockVisitor)
        }
        exception = exception ?: blockVisitor.exception // actually exception cant be not null here
    }

    override fun visitAssignment(ctx: FunGrammarParser.AssignmentContext) {
        val variableName = ctx.identifier().text
        ctx.expression().accept(this)
        if (exception != null) {
            return
        }
        if (!scope.assign(variableName, value)) {
            exception = FunException("Unknown variable $variableName")
        }
    }

    override fun visitL_return(ctx: FunGrammarParser.L_returnContext) {
        ctx.expression().accept(this)
        if (exception != null) {
            return
        }
        returned = value
    }

    override fun visitExpression(ctx: FunGrammarParser.ExpressionContext) {
        ctx.binary_expression().accept(this)
    }

    override fun visitSingular_expression(ctx: FunGrammarParser.Singular_expressionContext) {
        if (ctx.expression() != null) {
            ctx.expression().accept(this)
            return
        }
        ctx.getChild(0).accept(this)
    }

    override fun visitLiteral(ctx: FunGrammarParser.LiteralContext) {
        value = IntValue(ctx.LITERAL().text.toInt()) // it should be a correct string representation
    }

    override fun visitIdentifier(ctx: FunGrammarParser.IdentifierContext) {
        val variableName = ctx.IDENTIFIER().text
        val variableValue = scope.get(variableName)
        if (variableValue == null) {
            exception = FunException("Unknown identifier $variableName")
            return
        }
        value = variableValue
    }

    override fun visitFunction_call(ctx: FunGrammarParser.Function_callContext) {
        val functionName = ctx.identifier().text
        if (functionName == "println") {
            callPrintln(ctx.arguments())
            return
        }

        ctx.identifier().accept(this)
        if (exception != null) {
            return
        }
        val function = value

        if (function !is Function) {
            exception = FunException("Identifier ${ctx.identifier().text} is not a function")
            return
        }

        val argumentCount = ctx.arguments().expression().size
        if (argumentCount != function.argumentCount) {
            exception = FunException("Expected ${function.argumentCount} arguments, given $argumentCount")
            return
        }

        val argumentValues = evaluateArguments(ctx.arguments())
        if (exception != null) {
            return
        }

        callFunction(function, argumentValues)
    }

    private fun evaluateArguments(arguments: FunGrammarParser.ArgumentsContext): List<Value> {
        val argumentsExpressions = arguments.expression()

        val argumentValues : MutableList<Value> = MutableList(0) { IntValue(0) }

        for (argumentExpression in argumentsExpressions) {
            argumentExpression.accept(this)
            exception?.backtrace?.push("\tat argument ${argumentExpression.text}")
            if (exception != null) {
                return argumentValues
            }
            argumentValues.add(value)
        }
        return argumentValues
    }

    private fun callFunction(function: Function, argumentValues: List<Value>) {
        val functionScope = function.parentScope.childScope()
        for (i in 0 until function.argumentCount) {
            functionScope.create(function.argumentNames[i], argumentValues[i])
        }
        val functionVisitor = EvaluationVisitor(functionScope)
        function.body.accept(functionVisitor)
        exception = exception ?: functionVisitor.exception
        if (exception == null) {
            value = functionVisitor.returned ?: IntValue()
        }
    }

    private fun callPrintln(arguments: FunGrammarParser.ArgumentsContext) {
        val argumentValues = evaluateArguments(arguments)
        if (exception != null) {
            return
        }
        println(argumentValues.toString()) // TODO print with style
    }

    override fun visitBinary_expression(ctx: FunGrammarParser.Binary_expressionContext) {
        ctx.or().accept(this)
    }

    override fun visitOr(ctx: FunGrammarParser.OrContext) {
        visitBinaryOperator(ctx.or(), ctx.and()) { a, b -> ((a != 0) || (b != 0)).toInt() }
    }

    override fun visitAnd(ctx: FunGrammarParser.AndContext) {
        visitBinaryOperator(ctx.and(), ctx.equality()) { a, b -> ((a != 0) && (b != 0)).toInt() }
    }

    override fun visitEquality(ctx: FunGrammarParser.EqualityContext) {
        visitBinaryOperator(ctx.equality(), ctx.add_subtract(), chooseOperator(ctx.EQ_OPERATOR()?.text))
    }

    override fun visitAdd_subtract(ctx: FunGrammarParser.Add_subtractContext) {
        visitBinaryOperator(ctx.add_subtract(), ctx.mult_divide(), chooseOperator(ctx.ADD_OPERATOR()?.text))
    }

    override fun visitMult_divide(ctx: FunGrammarParser.Mult_divideContext) {
        visitBinaryOperator(ctx.mult_divide(), ctx.singular_expression(), chooseOperator(ctx.MULT_OPERATOR()?.text))
    }

    private fun chooseOperator(operatorText : String?): ((Int, Int) -> Int)? = when (operatorText) {
        ">" -> { a, b -> (a > b).toInt() }
        "<" -> { a, b -> (a < b).toInt() }
        ">=" -> { a, b -> (a >= b).toInt() }
        "<=" -> { a, b -> (a <= b).toInt() }
        "==" -> { a, b -> (a == b).toInt() }
        "!=" -> { a, b -> (a != b).toInt() }
        "+" -> { a, b -> a + b }
        "-" -> { a, b -> a - b }
        "*" -> { a, b -> a * b }
        "/" -> { a, b -> a / b }
        "%" -> { a, b -> a % b }
        // kotlin didn't allow just throwing an exception here
        else -> null
    }

    private fun visitBinaryOperator(leftExpr: ParserRuleContext?, rightExpr: ParserRuleContext, operator: ((Int, Int) -> Int)?) {
        if (leftExpr == null) {
            rightExpr.accept(this)
            return
        }

        fun acceptExpr(expr : ParserRuleContext): IntValue {
            expr.accept(this)
            if (exception != null) {
                return IntValue()
            }
            val valueCopy = value
            if (valueCopy !is IntValue) {
                exception = FunException("Trying to use operator not on a numeric value")
                return IntValue()
            }
            return valueCopy
        }

        val leftValue = acceptExpr(leftExpr)
        if (exception != null) {
            return
        }
        val rightValue = acceptExpr(rightExpr)
        if (exception != null) {
            return
        }

        try {
            value = IntValue(operator!!(leftValue.value, rightValue.value))
        } catch (e: ArithmeticException) {
            exception = FunException(e.message!!)
        }
    }

    private fun childVisitor() = EvaluationVisitor(this)

    private fun checkCondition(condition : FunGrammarParser.ExpressionContext) : Boolean {
        condition.accept(this)
        return exception == null && value is IntValue && (value as IntValue).value != 0
    }
}

internal fun Boolean.toInt() = if (this) 1 else 0

class ProgramResult internal constructor(val returned: Value?, val value: Value, val exception: FunException?) {
    internal constructor(visitor: EvaluationVisitor): this(visitor.returned, visitor.value, visitor.exception)
}