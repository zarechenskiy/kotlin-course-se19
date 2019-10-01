package ru.hse.spb.tex.util

import ru.hse.spb.tex.Element
import ru.hse.spb.tex.Elements
import ru.hse.spb.tex.Statements
import java.io.Writer

open class CommandWithBody<T : Element>(val text: String, private val body: T) : Element {
    private val squareArguments = arrayListOf<String>()
    private val figureArguments = arrayListOf<ArrayList<String>>()

    fun addSquareArguments(vararg squareParams: String) {
        squareArguments.addAll(squareParams)
    }

    fun addFigureArguments(vararg figureParams: String) {
        figureArguments.add(arrayListOf(*figureParams))
    }

    fun initBody(bodyInitializer: T.() -> Unit) = body.bodyInitializer()

    override fun render(output: Writer, indent: String) {
        output.appendln(indent + commandText())
        body.render(output, indent)
    }

    protected fun commandText(): String = "\\$text${squareParametersText()}${figureParametersText()}"
    private fun squareParametersText(): String = parametersOrNothing(*squareArguments.toTypedArray())
    private fun figureParametersText(): String = if (figureArguments.isNotEmpty()) {
        figureArguments.joinToString("}{", "{", "}") {
            it.joinToString(", ")
        }
    } else {
        ""
    }
}

open class Command(text: String) : CommandWithBody<Command.EmptyElement>(text, EmptyElement()) {
    class EmptyElement : Element {
        override fun render(output: Writer, indent: String) {}
    }
}

open class CommandGenerator<Y : Element, T : CommandWithBody<Y>>(
    protected val commandConsumer: (T) -> Any,
    val producer: () -> T
) {
    operator fun get(vararg params: String): ParameterCollector {
        return ParameterCollector(squareParams = params)
    }
    operator fun get(param: Pair<String, String>, vararg params: Pair<String, String>): ParameterCollector {
        return get(pairsToParameter(param, *params))
    }

    operator fun invoke(commandInit: Y.() -> Unit) {
        get()(commandInit)
    }

    operator fun invoke(vararg figureParams: String): ParameterCollector {
        return ParameterCollector(figureParams = figureParams)
    }

    inner class ParameterCollector(
        squareParams: Array<out String> = emptyArray(), figureParams: Array<out String>? = null
    ) {
        private val command = producer()
        init {
            commandConsumer(command)
            command.addSquareArguments(*squareParams)
            if (figureParams != null) {
                command.addFigureArguments(*figureParams)
            }
        }

        operator fun invoke(vararg figureParams: String): ParameterCollector {
            command.addFigureArguments(*figureParams)
            return this
        }

        operator fun invoke(commandInit: Y.() -> Unit) {
            command.initBody(commandInit)
        }
    }
}

open class CommandWithoutBodyGenerator(
    private val commandName: String,
    commandConsumer: (Command) -> Any
)
    : CommandGenerator<Command.EmptyElement, Command>(
    commandConsumer,
    { Command(commandName) }
)