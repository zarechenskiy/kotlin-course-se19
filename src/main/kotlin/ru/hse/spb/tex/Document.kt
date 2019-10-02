package ru.hse.spb.tex

import java.io.Writer

class Document: Statements() {
    private var documentClass = DocumentClass().apply { addFigureArguments("article") }
    private val prelude = Statements()

    fun documentClass(): CommandInitializer<Command.EmptyElement, DocumentClass> {
        documentClass = DocumentClass()
        return CommandInitializer(documentClass)
    }

    fun initCommand(commandText: String) = prelude.command(commandText)
    fun def(newCommand: String) = initCommand("def\\$newCommand")
    fun <T> withPrelude(initCommands: Statements.() -> T) = prelude.initCommands()

    fun newcommand(newCommand: String) = withPrelude {
        bracesCommand(newCommand)
    }
    fun newcommand(newCommand: String, init: FigureBracesStatements.() -> Unit) = withPrelude {
        bracesCommand(newCommand).invoke(init)
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
