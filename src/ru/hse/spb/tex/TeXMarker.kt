package ru.hse.spb.tex

import ru.hse.spb.tex.util.ItemGenerator
import ru.hse.spb.tex.util.pairsToParameter
import ru.hse.spb.tex.util.parametersOrNothing
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Writer
import java.io.StringWriter

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

    fun command(command: String) = +"\\$command"
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
//    fun item(init: Statements.() -> Unit) = addElement(Item(), init)
//    fun item(parameter: String, init: Statements.() -> Unit) = addElement(Item(parameter), init)

    override fun render(output: Writer, indent: String) {
        output.appendln("$indent\\begin{$name}")
        super.render(output, indent + "\t")
        output.appendln("$indent\\end{$name}")
    }

//    class Item(private val parameter: String? = null) : Statements() {
//        override fun render(output: Writer, indent: String) {
//            output.appendln("$indent\\item${parametersOrNothing(parameter)}")
//            super.render(output, indent)
//        }
//    }
}

class Document: Statements() {
    private var documentClass = "article"
    private var documentClassArguments: String? = null
    private val prelude = Statements()

    fun documentClass(documentClassName: String, parameters: String? = null) {
        documentClass = documentClassName
        documentClassArguments = parameters
    }

    fun initCommand(command: String) = prelude.command(command)

    fun usepackage(name: String, param1: Pair<String, String>, vararg params: Pair<String, String>) {
        initCommand("usepackage${pairsToParameter(param1, *params)}{$name}")
    }
    fun usepackage(name: String, vararg params: String) {
        initCommand("usepackage${parametersOrNothing(*params)}{$name}")
    }

    fun toWriter(output: Writer) {
        render(output, "")
    }

    fun toOutputStream(output: OutputStream) {
        OutputStreamWriter(output).use { toWriter(it) }
    }

    override fun toString(): String {
        return StringWriter().also {
            toWriter(it)
        }.toString()
    }

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
