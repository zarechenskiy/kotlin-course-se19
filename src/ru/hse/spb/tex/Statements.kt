package ru.hse.spb.tex

import java.io.Writer

// It's because of this::addElement and it only only accesses this@Elements.elements and doesn't call any methods
@Suppress("LeakingThis")
open class Statements : Elements() {

    open operator fun String.unaryPlus() {
        elements.add(TextStatement(this))
    }

    fun itemTag(tag: String) =
        CommandInitializer(addElement(BeginCommand(tag, Items())))
    fun itemTag(tag: String, init: Items.() -> Unit) = itemTag(tag).invoke(init)

    val enumerate
        get() = itemTag("enumerate")
    val itemize
        get() = itemTag("itemize")

    fun emptyLn() = +""

    fun customTag(tag: String) =
        CommandInitializer(addElement(BeginCommand(tag, Statements())))
    fun customTag(tag: String, init: Statements.() -> Unit) =
        customTag(tag).invoke(init)

    fun customManualNewlineTag(tag: String) =
        CommandInitializer(
            addElement(
                BeginCommand(
                    tag,
                    ManualNewlineStatements()
                )
            )
        )
    fun customManualNewlineTag(tag: String, init: ManualNewlineStatements.() -> Unit) =
        customManualNewlineTag(tag).invoke(init)

    val math
        get() = customManualNewlineTag("math")

    val align
        get() = customManualNewlineTag("align*")

    fun command(name: String) = CommandInitializer(addElement(Command(name)))

    fun bracesCommand(name: String) =
        CommandInitializer(addElement(FigureBracesCommand(name)))
    fun bracesCommand(name: String, init: FigureBracesStatements.() -> Unit) =
        CommandInitializer(addElement(FigureBracesCommand(name))).invoke(init)

}

open class ManualNewlineStatements : Statements() {
    override operator fun String.unaryPlus() {
        elements.add(TextStatement(this + "\\\\"))
    }
}

open class FigureBracesStatements : Statements() {
    override fun render(output: Writer, indent: String) {
        output.appendln("$indent{")
        for (statement in elements) {
            statement.render(output, indent + "\t")
        }
        output.appendln("$indent}")
    }
}
