package ru.hse.spb

import java.io.*
import java.util.*

interface Element {
    fun render(builder: StringBuilder, indent: String)
}

class TextElement(private val text: String) : Element {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent${text.replace("\n", "\n$indent")}\n")
    }
}

@DslMarker
annotation class TexTagMarker

@TexTagMarker
abstract class SimpleTag : Element {
    val children = arrayListOf<Element>()

    protected fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    override fun render(builder: StringBuilder, indent: String) {
        for (c in children) {
            c.render(builder, "$indent  ")
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        render(builder, "")
        return builder.toString()
    }
}

abstract class SimpleTagWithText : SimpleTag() {
    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }
}

abstract class ComplexTag(private val name: String, private val argument: Pair<String, String>? = null)
    : SimpleTagWithText() {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\begin{$name}")
        if (argument != null) {
            builder.append("[${argument.first}=${argument.second}]")
        }
        builder.append("\n")
        for (c in children) {
            c.render(builder, "$indent  ")
        }
        builder.append("$indent\\end{$name}\n")
    }
}

abstract class TexCommand(name: String, argument: Pair<String, String>? = null) : ComplexTag(name, argument) {
    fun frame(argument: Pair<String, String>, frameTitle: String, init: Frame.() -> Unit) =
        initTag(Frame(frameTitle, argument), init)

    fun itemize(argument: Pair<String, String>? = null, init: Itemize.() -> Unit) =
        initTag(Itemize(argument), init)

    fun customTag(argument: Pair<String, String>? = null, name: String, init: CustomTag.() -> Unit) =
        initTag(CustomTag(name, argument), init)

    fun math(argument: Pair<String, String>? = null, init: CustomTag.() -> Unit) =
        initTag(CustomTag("math", argument), init)

    fun enumerate(argument: Pair<String, String>? = null, init: Enumerate.() -> Unit) =
        initTag(Enumerate(argument), init)
}

class CustomTag(name: String, argument: Pair<String, String>? = null) : TexCommand(name, argument)

class Document : TexCommand("document") {
    private var documentClassName: String? = null
    private var usepackages: MutableSet<String> = TreeSet()

    fun documentClass(value: String) {
        documentClassName = value
    }

    fun usepackage(vararg values: String) {
        usepackages.addAll(values)
    }

    override fun render(builder: StringBuilder, indent: String) {
        if (documentClassName != null) {
            builder.append("$indent\\documentclass{$documentClassName}\n")
        }
        if (usepackages.isNotEmpty()) {
            builder.append("$indent\\usepackage{")
            val arr = usepackages.toTypedArray()
            for (pac in 0..arr.size - 2) {
                builder.append("${arr[pac]}, ")
            }
            builder.append(arr.last())
            builder.append("}\n")
        }
        super.render(builder, indent)
    }

    fun toOutputStream(out: OutputStream) {
        val builder = StringBuilder()
        render(builder, "")
        val stream = PrintStream(out)
        stream.print(builder.toString())
        stream.flush()
    }
}

class Frame(title: String, argument: Pair<String, String>?) : TexCommand("frame", argument) {
    init {
        children.add(TextElement("\\frametitle{$title}"))
    }
}

class Item : SimpleTagWithText() {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\item\n")
        super.render(builder, "$indent  ")
    }
}

class Itemize(argument: Pair<String, String>?) : TexCommand("itemize", argument) {
    fun item(init: Item.() -> Unit) = initTag(Item(), init)
}

class Enumerate(argument: Pair<String, String>?) : TexCommand("enumerate", argument) {
    fun item(init: Item.() -> Unit) = initTag(Item(), init)
}

fun document(init: Document.() -> Unit): Document {
    val tex = Document()
    tex.init()
    return tex
}