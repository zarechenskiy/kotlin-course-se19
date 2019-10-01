package ru.hse.spb.tex.util

import ru.hse.spb.tex.Element
import ru.hse.spb.tex.Elements
import ru.hse.spb.tex.ManualNewlineStatements
import ru.hse.spb.tex.Statements
import java.io.Writer


open class Tag(private val name: String) : Statements() {
    override fun render(output: Writer, indent: String) {
        output.appendln("$indent\\begin{$name}")
        super.render(output, indent + "\t")
        output.appendln("$indent\\end{$name}")
    }
}

open class BeginCommand<E : Element>(val tag: String, body: E) : CommandWithBody<E>("begin", body) {
    init {
        addFigureArguments(tag)
    }

    override fun render(output: Writer, indent: String) {
        output.appendln(indent + commandText())
        body.render(output, "\t$indent")
        output.appendln("\\end{$tag}")
    }
}

open class BeginInitializer<E : Element>(
    tag: String,
    commandConsumer: (BeginCommand<E>) -> Any,
    bodyProducer: () -> E
) : CommandInitializer<E, BeginCommand<E>>(
    commandConsumer,
    { BeginCommand(tag, bodyProducer())}
)


class Item : CommandWithBody<Statements>("item", Statements())
class ItemInitializer(consumer: (Element) -> Unit) : CommandInitializer<Statements, Item>(consumer, { Item() })
class Items : Elements() {
    val item get() = ItemInitializer { addReadyElement(it) }
}

open class ItemTagInitializer(
    tag: String,
    commandConsumer: (BeginCommand<Items>) -> Any
) : BeginInitializer<Items>(tag, commandConsumer, { Items() })

open class StatementsTagInitializer(
    tag: String,
    commandConsumer: (BeginCommand<Statements>) -> Any
) : BeginInitializer<Statements>(tag, commandConsumer, { Statements() })

open class ManualNewlineStatementsTagInitializer(
    tag: String,
    commandConsumer: (BeginCommand<ManualNewlineStatements>) -> Any
) : BeginInitializer<ManualNewlineStatements>(tag, commandConsumer, { ManualNewlineStatements() })