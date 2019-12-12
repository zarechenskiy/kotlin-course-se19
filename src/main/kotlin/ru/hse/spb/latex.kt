package ru.hse.spb

import java.io.OutputStream
import java.lang.IllegalStateException

@DslMarker
annotation class LatexTagMarker

abstract class Element(protected val builder: StringBuilder, protected val indent: String) {
    protected val nextIndent = "$indent  "

    abstract fun begin()

    open fun end() {}
}

class TextElement(private val text: String, builder: StringBuilder, indent: String) : Element(builder, indent) {
    override fun begin() {
        builder.append("$indent$text\n")
    }
}

@LatexTagMarker
abstract class Scope(builder: StringBuilder, indent: String) : Element(builder, indent) {
    protected fun <T : Element> initTag(tag: T, init: T.() -> Unit = {}) {
        beforeTag()
        tag.begin()
        tag.init()
        tag.end()
    }

    operator fun String.unaryPlus() = initTag(TextElement(this, builder, nextIndent))

    protected open fun beforeTag() {}
}

abstract class Environment(builder: StringBuilder,
                           indent: String,
                           private val name: String,
                           private val parameters: Array<Pair<String, String>> = emptyArray())
    : Scope(builder, indent) {

    override fun begin() {
        builder.append("$indent\\begin{$name}")
        if (!parameters.isEmpty()) {
            builder.append("[${parameters.joinToString{(k, v) -> "$k=$v"}}]")
        }
        builder.append("\n")
    }

    override fun end() {
        builder.append("$indent\\end{$name}\n")
    }
}

abstract class CommonEnvironment(builder: StringBuilder,
                                 indent: String,
                                 name: String,
                                 parameters: Array<Pair<String, String>> = emptyArray())
    : Environment(builder, indent, name, parameters) {

    fun itemize(init: Itemize.() -> Unit) = initTag(Itemize(builder, nextIndent), init)

    fun enumerate(init: Enumerate.() -> Unit) = initTag(Enumerate(builder, nextIndent), init)

    fun math(init: Math.() -> Unit) = initTag(Math(builder, nextIndent), init)

    fun align(init: Align.() -> Unit) = initTag(Align(builder, nextIndent), init)

    fun customTag(name: String, vararg parameters: Pair<String, String>, init: CustomTag.() -> Unit) =
            initTag(CustomTag(builder, nextIndent, name, arrayOf(*parameters)), init)
}

class Document : CommonEnvironment(StringBuilder(), "", "document") {
    private var beginPreamble = false
    private var endPreamble = false
    private var endDocument = false

    override fun toString(): String {
        beforeTag()
        endDocument()
        return builder.toString()
    }

    fun toOutputStream(stream: OutputStream) {
        stream.write(toString().toByteArray())
    }

    fun documentClass(argument: String, vararg parameters: String) {
        if (beginPreamble) {
            throw IllegalStateException("Two \\documentclass commands.")
        }
        beginPreamble = true
        DocumentClass(builder, indent, argument, arrayOf(*parameters)).begin()
    }

    fun usePackage(argument: String, vararg parameters: String) {
        if (!beginPreamble) {
            throw IllegalStateException("\\usepackage before \\documentclass.")
        }
        if (endPreamble) {
            throw IllegalStateException("\\usepackage can be used only in preamble.")
        }
        UsePackage(builder, indent, argument, arrayOf(*parameters)).begin()
    }

    fun frame(frameTitle: String? = null, vararg parameters: Pair<String, String>, init: Frame.() -> Unit) =
        initTag(Frame(builder, nextIndent, frameTitle, arrayOf(*parameters)), init)

    override fun beforeTag() {
        if (!beginPreamble) {
            throw IllegalStateException("No \\documentclass.")
        }
        if (!endPreamble) {
            super.begin()
            endPreamble = true
        }
    }

    private fun endDocument() {
        if (!endDocument) {
            super.end()
            endDocument = true
        }
    }
}

abstract class Command(builder: StringBuilder,
                       indent: String,
                       private val name: String,
                       private val argument: String,
                       private val parameters: Array<String> = emptyArray())
    : Element(builder, indent) {

    override fun begin() {
        builder.append("$indent\\$name")
        if (!parameters.isEmpty()) {
            builder.append("[${parameters.joinToString()}]")
        }
        builder.append("{$argument}\n")
    }
}

class DocumentClass(builder: StringBuilder,
                    indent: String,
                    argument: String,
                    parameters: Array<String> = emptyArray())
    : Command(builder, indent, "documentclass", argument, parameters)

class UsePackage(builder: StringBuilder,
                 indent: String,
                 argument: String,
                 parameters: Array<String> = emptyArray())
    : Command(builder, indent, "usepackage", argument, parameters)

class FrameTitle(builder: StringBuilder,
                 indent: String,
                 title: String)
    : Command(builder, indent, "frametitle", title, emptyArray())

class Frame(builder: StringBuilder,
            indent: String,
            private val frameTitle: String? = null,
            parameters: Array<Pair<String, String>>)
    : CommonEnvironment(builder, indent, "frame", parameters) {

    override fun begin() {
        super.begin()
        if (frameTitle != null) {
            FrameTitle(builder, nextIndent, frameTitle).begin()
        }
    }
}

class Itemize(builder: StringBuilder, indent: String) : CommonEnvironment(builder, indent, "itemize") {
    fun item(init: Item.() -> Unit) = initTag(Item(builder, nextIndent), init)
}

class Enumerate(builder: StringBuilder, indent: String) : CommonEnvironment(builder, indent, "enumerate") {
    fun item(init: Item.() -> Unit) = initTag(Item(builder, nextIndent), init)
}

class Item(builder: StringBuilder, indent: String) : CommonEnvironment(builder, indent, "item")

class Math(builder: StringBuilder, indent: String) : Environment(builder, indent, "math")

class Align(builder: StringBuilder, indent: String) : Environment(builder, indent, "align")

class CustomTag(builder: StringBuilder,
                indent: String,
                name: String,
                parameters: Array<Pair<String, String>>)
    : CommonEnvironment(builder, indent, name, parameters)

fun document(init: Document.() -> Unit): Document {
    val latex = Document()
    latex.init()
    return latex
}
