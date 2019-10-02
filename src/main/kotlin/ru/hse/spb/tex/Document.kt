package ru.hse.spb.tex

import java.io.Writer
import java.lang.RuntimeException

class Document: Statements() {
    private val prelude = Statements()
    private var documentClassCommand = DocumentClass().apply { addFigureArguments("article") }
    private var documentClassInitialized = false

    val documentClass: CommandInitializer<Command.EmptyElement, DocumentClass>
        @Throws(MultipleDocumentClassException::class)
        get() {
            if (documentClassInitialized) {
                throw MultipleDocumentClassException()
            }
            documentClassInitialized = true
            documentClassCommand = DocumentClass()
            return CommandInitializer(documentClassCommand)
        }

    fun initCommand(commandText: String) = prelude.command(commandText)
    fun def(newCommand: String) = initCommand("def\\$newCommand")
    fun <T> withPrelude(initCommands: Statements.() -> T) = prelude.initCommands()

    fun newcommand(newCommand: String) = withPrelude {
        bracesCommand("newcommand")("\\$newCommand")
    }
    fun newcommand(newCommand: String, init: FigureBracesStatements.() -> Unit) = withPrelude {
        bracesCommand("newcommand")("\\$newCommand").invoke(init)
    }

    val usepackage get() = CommandInitializer(prelude.addElement(Command("usepackage")))

    val frame get() = CommandInitializer(
        addElement(
            BeginCommand(
                "frame",
                Statements()
            )
        )
    )

    override fun render(output: Writer, indent: String) {
        output.apply {
            documentClassCommand.render(output, indent)
            appendln("")
            prelude.render(output, indent)
            appendln("$indent\\begin{document}")
            super.render(output, indent)
            appendln("$indent\\end{document}")
        }
    }

    class DocumentClass : Command("documentclass")
    class MultipleDocumentClassException : RuntimeException("Multiple documentClass calls for one Document")
}

fun document(init: Document.() -> Unit) = Document().apply(init)
