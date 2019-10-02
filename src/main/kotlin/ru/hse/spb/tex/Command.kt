package ru.hse.spb.tex

import ru.hse.spb.tex.util.convertToParameters
import ru.hse.spb.tex.util.parametersOrNothing
import java.io.Writer
import java.lang.StringBuilder

open class CommandWithBody<T : Element>(private val text: String, protected val body: T) : Element {
    private val squareArguments = arrayListOf<ArrayList<String>>()
    private val figureArguments = arrayListOf<ArrayList<String>>()
    private val arguments = StringBuilder()

    fun addSquareArguments(vararg squareParams: String) {
        arguments.append(squareParams.joinToString(", ", "[", "]"))
    }

    fun addFigureArguments(vararg figureParams: String) {
        arguments.append(figureParams.joinToString(", ", "{", "}"))
    }

    fun initBody(bodyInitializer: T.() -> Unit) = body.bodyInitializer()

    override fun render(output: Writer, indent: String) {
        output.appendln(indent + commandText())
        body.render(output, indent)
    }

    protected fun commandText(): String = "\\$text$arguments"
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
