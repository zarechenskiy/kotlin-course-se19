package ru.hse.spb.tex

interface Element {
    fun render(builder: StringBuilder, indent: String)
}

abstract class Statement : Element

class TextStatement(private val text: String) : Statement() {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append(indent + text + "\n")
    }
}

@DslMarker
annotation class TeXMarker

@TeXMarker
open class Elements : Element {
    protected val elements = arrayListOf<Element>()

    protected fun <T : Element> addElement(element: T, init: T.() -> Unit): T {
        element.init()
        elements.add(element)
        return element
    }

    override fun render(builder: StringBuilder, indent: String) {
        for (statement in elements) {
            statement.render(builder, indent)
        }
    }
}

open class Statements : Elements() {

    operator fun String.unaryPlus() {
        elements.add(TextStatement(this))
    }

    fun paragraph() = +""

    fun enumerate(init: Enumerate.() -> Unit) = addElement(Enumerate(), init)
    fun itemize(init: Itemize.() -> Unit) = addElement(Itemize(), init)
    fun itemTag(tag: String, init: ItemTag.() -> Unit) = addElement(ItemTag(tag), init)

    fun customTag(tag: String, init: Tag.() -> Unit) = addElement(Tag(tag), init)
    fun customTag(tag: String, parameter: String, init: Tag.() -> Unit) =
        addElement(Tag(tag + parameter), init)
    fun customTag(tag: String, parameter: Pair<String, String>, init: Tag.() -> Unit) =
        addElement(Tag(tag + pairsToParameter(parameter)), init)

    fun command(command: String) = +"\\$command"
}

open class Tag(private val name: String) : Statements() {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\begin{$name}\n")
        super.render(builder, indent + "\t")
        builder.append("$indent\\end{$name}\n")
    }
}

open class ItemTag(private val name: String) : Elements() {

    fun item(init: Statements.() -> Unit) = addElement(Item(), init)
    fun item(parameter: String, init: Statements.() -> Unit) = addElement(Item(parameter), init)

    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\begin{$name}\n")
        super.render(builder, indent + "\t")
        builder.append("$indent\\end{$name}\n")
    }

    class Item(private val parameter: String? = null) : Statements() {
        override fun render(builder: StringBuilder, indent: String) {
            builder.append("$indent\\item${parametersOrNothing(parameter)}\n")
            super.render(builder, indent)
        }
    }
}

class Enumerate : ItemTag("enumerate")
class Itemize : ItemTag("itemize")

fun parametersOrNothing(vararg parameter: String?): String {
    return "[${parameter.filter { it != null && it != "" }.joinToString(", ")}]".takeIf { it.length > 2 } ?: ""
}

fun pairsToParameter(vararg params: Pair<String, String>): String {
    return parametersOrNothing(params.joinToString(", ") { "${it.first} = ${it.second}" })
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

    fun toStringBuilder(builder: StringBuilder) {
        render(builder, "")
    }

    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\documentclass${parametersOrNothing(documentClassArguments)}{$documentClass}")
        builder.append("\n")
        prelude.render(builder, indent)
        builder.append("$indent\\begin{document}")
        super.render(builder, indent)
        builder.append("$indent\\end{document}")
    }
}

fun document(init: Document.() -> Unit) = Document().apply(init)