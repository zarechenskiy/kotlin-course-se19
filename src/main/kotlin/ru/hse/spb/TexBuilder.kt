package ru.hse.spb

import java.io.PrintStream

@DslMarker
annotation class TexElementMarker

@TexElementMarker
interface Element {
    fun toOutputStream(outputStream: PrintStream)
}


abstract class Tree : Element {
    protected val children = arrayListOf<Element>()

    protected fun <T : Element> initChild(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }
}

abstract class Tag(
    protected val name: String,
    private val property: Property? = null,
    private vararg val options: Pair<String, String>
) :
    Tree() {

    override fun toOutputStream(outputStream: PrintStream) {
        when (options.isEmpty()) {
            true -> outputStream.println("\\begin{$name}")
            else -> outputStream.println("\\begin{$name}${options.map { pair -> pair.first + "=" + pair.second }}")
        }
        property?.toOutputStream(outputStream)
        children.forEach { it.toOutputStream(outputStream) }
        outputStream.println("\\end{$name}")
    }

    operator fun String.unaryPlus() = children.add(TextElement(this))

    fun itemize(init: Aggregate.() -> Unit) = initChild(Aggregate("itemize"), init)

    fun enumerate(init: Aggregate.() -> Unit) = initChild(Aggregate("enumerate"), init)

    fun math(init: Math.() -> Unit) = initChild(Math(), init)

    fun customTag(name: String, vararg options: Pair<String, String>, init: CustomTag.() -> Unit) =
        initChild(CustomTag(name, *options), init)
}


class Aggregate(private val name: String) : Tree() {

    fun item(init: Item.() -> Unit) = initChild(Item(), init)

    override fun toOutputStream(outputStream: PrintStream) {
        outputStream.println("\\begin{$name}")
        children.forEach { it.toOutputStream(outputStream) }
        outputStream.println("\\end{$name}")
    }
}

class TextElement(private val text: String) : Element {
    override fun toOutputStream(outputStream: PrintStream) {
        outputStream.println(text)
    }
}

class Property(private val property: String, private vararg val value: String) : Element {
    override fun toOutputStream(outputStream: PrintStream) {
        outputStream.println("\\$property{${value.joinToString(separator = ",")}}")
    }
}

class Frame(title: String, vararg opt: Pair<String, String>) :
    Tag("frame", Property("frametitle", title), options = *opt)

class Item : Tag("item") {
    override fun toOutputStream(outputStream: PrintStream) {
        outputStream.println("\\$name")
        children.forEach { it.toOutputStream(outputStream) }
    }
}

class Document : Tag("document")

class Math : Tag("math")

class CustomTag(name: String, vararg opt: Pair<String, String>) : Tag(name, options = *opt)

class TeX : Tree() {
    override fun toOutputStream(outputStream: PrintStream) {
        children.forEach { it.toOutputStream(outputStream) }
    }

    fun document(init: Document.() -> Unit) = initChild(Document(), init)

    fun documentClass(cls: String) = initChild(Property("documentclass", cls), {})

    fun usepackage(vararg pkg: String) = initChild(Property("usepackage", *pkg), {})

    fun frame(frameTitle: String, vararg opt: Pair<String, String>, init: Frame.() -> Unit) =
        initChild(Frame(frameTitle, *opt), init)
}

fun tex(init: TeX.() -> Unit): TeX {
    val tex = TeX()
    tex.init()
    return tex
}