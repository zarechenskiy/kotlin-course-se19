package ru.hse.spb.tex

import ru.hse.spb.tex.util.*
import java.io.Writer


interface Element {
    fun render(output: Writer, indent: String)
}

abstract class Statement : Element

class TextStatement(private val text: String) : Statement() {
    override fun render(output: Writer, indent: String) {
        output.appendln(indent + text)
    }
}

@DslMarker
annotation class TeXMarker

@TeXMarker
open class Elements : Element {
    protected val elements = arrayListOf<Element>()

    fun <T : Element> addElement(element: T, init: T.() -> Unit = {}): T {
        element.init()
        elements.add(element)
        return element
    }

    // to use as lambda
    fun <T : Element> addReadyElement(element: T): T {
        elements.add(element)
        return element
    }

    override fun render(output: Writer, indent: String) {
        for (statement in elements) {
            statement.render(output, indent)
        }
    }
}

open class Statements : Elements() {

    operator fun String.unaryPlus() {
        elements.add(TextStatement(this))
    }

    fun paragraph() = +""

    // addReadyElement only accesses this@Elements.elements and doesn't call any methods
    @Suppress("LeakingThis")
    val enumerate = ItemTagGenerator("enumerate", this::addReadyElement)
    @Suppress("LeakingThis")
    val itemize = ItemTagGenerator("itemize", this::addReadyElement)
    fun itemTag(tag: String) = ItemTagGenerator(tag, this::addReadyElement)
    fun itemTag(tag: String, init: Items.() -> Unit) = ItemTagGenerator(tag, this::addReadyElement).also {
        it.invoke(init)
    }

    fun customTag(tag: String) = StatementsTagGenerator(tag, this::addReadyElement)
    fun customTag(tag: String, init: Statements.() -> Unit) = StatementsTagGenerator(tag, this::addReadyElement).also {
        it.invoke(init)
    }

    fun command(name: String) = CommandWithoutBodyGenerator(name, this::addReadyElement)
}