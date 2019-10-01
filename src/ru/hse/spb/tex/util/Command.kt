package ru.hse.spb.tex.util

import ru.hse.spb.tex.Element
import ru.hse.spb.tex.Elements
import ru.hse.spb.tex.Statements
import ru.hse.spb.tex.TextArguments
import java.io.Writer

open class CommandWithBody<T : Element>(val name: String, protected val body: T) : Element {
    var parameters: String? = null

    fun initParams(vararg params: String) {
        parameters = parametersOrNothing(*params)
    }
    fun initParams(vararg params: Pair<String, String>) {
        parameters = pairsToParameter(*params)
    }

    fun initBody(bodyInitializer: T.() -> Unit) = body.bodyInitializer()

    override fun render(output: Writer, indent: String) {
        output.appendln("$indent\\$name${parameters ?: ""} ")
        body.render(output, indent)
    }
}

open class Command(name: String) : CommandWithBody<TextArguments>(name, TextArguments())

open class CommandGenerator<Y : Element, T : CommandWithBody<Y>>(
    protected val elements: Elements,
    val producer: () -> T
) {
    operator fun get(vararg params: String): (Y.() -> Unit) -> Unit {
        val command = producer()
        command.initParams(*params)
        elements.addElement(command)
        return { command.initBody(it) }
    }
    operator fun get(param: Pair<String, String>, vararg params: Pair<String, String>): (Y.() -> Unit) -> Unit {
        return get(pairsToParameter(param, *params))
    }

    operator fun invoke(commandInit: Y.() -> Unit) {
        get()(commandInit)
    }
}


// Some default commands

class Item : CommandWithBody<Statements>("item", Statements())
class ItemGenerator(elements: Elements) : CommandGenerator<Statements, Item>(elements, { Item() })