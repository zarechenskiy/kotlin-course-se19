package ru.hse.spb.tex

import ru.hse.spb.tex.util.Command
import ru.hse.spb.tex.util.ItemGenerator
import ru.hse.spb.tex.util.pairsToParameter
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
class TextArguments : Element {
    private val arguments = arrayListOf<String>()

    operator fun String.unaryPlus() {
        arguments.add(this)
    }

    override fun render(output: Writer, indent: String) {
        if (arguments.isNotEmpty()) {
            output.appendln("{${arguments.joinToString(", ")}}")
        }
    }

}

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

    fun enumerate(init: ItemTag.() -> Unit) = itemTag("enumerate", init)
    fun itemize(init: ItemTag.() -> Unit) = itemTag("itemize", init)
    fun itemTag(tag: String, init: ItemTag.() -> Unit) = addElement(ItemTag(tag), init)

    fun customTag(tag: String, init: Tag.() -> Unit) = addElement(Tag(tag), init)
    fun customTag(tag: String, parameter: String, init: Tag.() -> Unit) =
        addElement(Tag(tag + parameter), init)
    fun customTag(tag: String, parameter: Pair<String, String>, init: Tag.() -> Unit) =
        addElement(Tag(tag + pairsToParameter(parameter)), init)

    fun command(name: String) = addElement(Command(name))
}

open class Tag(private val name: String) : Statements() {
    override fun render(output: Writer, indent: String) {
        output.appendln("$indent\\begin{$name}")
        super.render(output, indent + "\t")
        output.appendln("$indent\\end{$name}")
    }
}

open class ItemTag(private val name: String) : Elements() {
    val item = ItemGenerator(this)

    override fun render(output: Writer, indent: String) {
        output.appendln("$indent\\begin{$name}")
        super.render(output, indent + "\t")
        output.appendln("$indent\\end{$name}")
    }
}