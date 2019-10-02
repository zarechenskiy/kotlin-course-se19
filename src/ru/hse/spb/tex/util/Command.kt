package ru.hse.spb.tex.util

import ru.hse.spb.tex.Element
import ru.hse.spb.tex.Elements
import ru.hse.spb.tex.Statements
import java.io.Writer

open class CommandWithBody<T : Element>(val text: String, protected val body: T) : Element {
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

open class CommandInitializer<Y : Element, T : CommandWithBody<Y>>(val command: T) {

    operator fun get(vararg params: String): CommandInitializer<Y, T> {
        command.addSquareArguments(*params)
        return this
    }
    operator fun get(param: Pair<String, String>, vararg params: Pair<String, String>): CommandInitializer<Y, T> =
        get(pairsToParameter(param, *params))

    operator fun invoke(vararg figureParams: String): CommandInitializer<Y, T> {
        command.addFigureArguments(*figureParams)
        return this
    }

    operator fun invoke(commandInit: Y.() -> Unit) {
        command.initBody(commandInit)
    }

    operator fun invoke(vararg figureParams: String, commandInit: Y.() -> Unit) {
        invoke(*figureParams)
        invoke(commandInit)
    }
}

open class CommandWithoutBodyInitializer(
    commandName: String,
    commandConsumer: (Command) -> Any
)
    : CommandInitializer<Command.EmptyElement, Command>(
    Command(commandName).also { commandConsumer(it) }
)