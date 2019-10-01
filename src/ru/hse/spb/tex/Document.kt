package ru.hse.spb.tex

import ru.hse.spb.tex.util.CommandWithoutBodyGenerator
import ru.hse.spb.tex.util.parametersOrNothing
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Writer
import java.io.StringWriter

class Document: Statements() {
    private var documentClass = "article"
    private var documentClassArguments: String? = null
    private val prelude = Statements()

    fun documentClass(documentClassName: String, parameters: String? = null) {
        documentClass = documentClassName
        documentClassArguments = parameters
    }

    fun initCommand(command: String) = prelude.command(command)

    val usepackage = CommandWithoutBodyGenerator("usepackage", prelude::addReadyElement)

    fun toWriter(output: Writer) {
        render(output, "")
    }

    fun toOutputStream(output: OutputStream) {
        OutputStreamWriter(output).use { toWriter(it) }
    }

    override fun toString(): String {
        return StringWriter().also {
            toWriter(it)
        }.toString()
    }

    override fun render(output: Writer, indent: String) {
        output.apply {
            append("$indent\\documentclass${parametersOrNothing(documentClassArguments)}{$documentClass}")
            appendln("")
            prelude.render(output, indent)
            appendln("$indent\\begin{document}")
            super.render(output, indent)
            appendln("$indent\\end{document}")
        }
    }
}

fun document(init: Document.() -> Unit) = Document().apply(init)
