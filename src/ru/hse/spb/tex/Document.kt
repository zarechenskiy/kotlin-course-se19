package ru.hse.spb.tex

import ru.hse.spb.tex.util.Command
import ru.hse.spb.tex.util.CommandInitializer
import ru.hse.spb.tex.util.parametersOrNothing
import java.io.Writer

class Document: Statements() {
    private var documentClass = "article"
    private var documentClassArguments: String? = null
    private val prelude = Statements()

    fun documentClass(documentClassName: String, parameters: String? = null) {
        documentClass = documentClassName
        documentClassArguments = parameters
    }

    fun initCommand(commandText: String) = prelude.command(commandText)
    fun def(newComand: String) = initCommand("def\\$newComand")
    fun withPrelude(initCommands: Statements.() -> Unit) = prelude.initCommands()

    val usepackage get() = CommandInitializer(prelude.addReadyElement(Command("usepackage")))

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
