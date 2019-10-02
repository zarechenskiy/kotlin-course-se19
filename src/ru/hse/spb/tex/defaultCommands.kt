package ru.hse.spb.tex

import java.io.Writer


open class BeginCommand<E : Element>(val tag: String, body: E) : CommandWithBody<E>("begin", body) {
    init {
        addFigureArguments(tag)
    }

    override fun render(output: Writer, indent: String) {
        output.appendln(indent + commandText())
        body.render(output, "\t$indent")
        output.appendln("$indent\\end{$tag}")
    }
}

open class BeginInitializer<E : Element>(
    tag: String,
    commandConsumer: (BeginCommand<E>) -> Any,
    bodyProducer: () -> E
) : CommandInitializer<E, BeginCommand<E>>(
    BeginCommand(tag, bodyProducer()).also { commandConsumer(it)}
)


class Item : CommandWithBody<Statements>("item", Statements())
class Items : Elements() {
    val item get() = CommandInitializer(addReadyElement(Item()))
}
