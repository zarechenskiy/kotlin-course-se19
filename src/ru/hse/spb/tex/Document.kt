package ru.hse.spb.tex

import java.io.Writer

class Document: Statements() {
    private var documentClass = DocumentClass().apply { addFigureArguments("article") }
    private val prelude = Statements()

    fun documentClass(documentClassName: String): CommandInitializer<Command.EmptyElement, DocumentClass> {
        documentClass = DocumentClass()
        documentClass.addFigureArguments(documentClassName)
        return CommandInitializer(documentClass)
    }

    fun initCommand(commandText: String) = prelude.command(commandText)
    fun def(newCommand: String) = initCommand("def\\$newCommand")
    fun newcommand(newCommand: String) = initCommand("newcommand{\\$newCommand}")
    fun withPrelude(initCommands: Statements.() -> Unit) = prelude.initCommands()

    val usepackage get() = CommandInitializer(prelude.addReadyElement(Command("usepackage")))

    val frame get() = CommandInitializer(
        addReadyElement(
            BeginCommand(
                "frame",
                Statements()
            )
        )
    )

    override fun render(output: Writer, indent: String) {
        output.apply {
            documentClass.render(output, indent)
            appendln("")
            prelude.render(output, indent)
            appendln("$indent\\begin{document}")
            super.render(output, indent)
            appendln("$indent\\end{document}")
        }
    }

    class DocumentClass : Command("documentclass")
}

fun document(init: Document.() -> Unit) = Document().apply(init)
