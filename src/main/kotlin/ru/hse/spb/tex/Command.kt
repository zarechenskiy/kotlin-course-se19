package ru.hse.spb.tex

import ru.hse.spb.tex.util.parametersOrNothing
import java.io.Writer

open class CommandWithBody<T : Element>(private val text: String, protected val body: T) : Element {
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
    private fun squareParametersText(): String =
        parametersOrNothing(*squareArguments.toTypedArray())
    private fun figureParametersText(): String = if (figureArguments.isNotEmpty()) {
        figureArguments.joinToString("}{", "{", "}") {
            it.joinToString(", ")
        }
    } else {
        ""
    }
}

open class Command(text: String) : CommandWithBody<Command.EmptyElement>(
    text,
    EmptyElement()
) {
    class EmptyElement : Element {
        override fun render(output: Writer, indent: String) {}
    }
}

open class FigureBracesCommand(text: String) : CommandWithBody<FigureBracesStatements>(
    text,
    FigureBracesStatements()
) {
    override fun render(output: Writer, indent: String) {
        output.append(indent + commandText())
        body.render(output, indent)
    }
}
