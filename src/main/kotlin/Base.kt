package ru.hse.spb

import java.io.OutputStream

interface Element {
    fun toOutputStream(stream: OutputStream, indent: String)
}

class TextElement(private val text: String): Element {

    override fun toOutputStream(stream: OutputStream, indent: String) {
        stream.write("$indent$text${System.lineSeparator()}".toByteArray())
    }
}

abstract class Tag(
    private val tagName: String,
    protected val arg: String
): Element {

    override fun toOutputStream(stream: OutputStream, indent: String) {
        stream.write("$indent\\$tagName{$arg}${System.lineSeparator()}".toByteArray())
    }
}

@DslMarker
annotation class TexTagMarker

@TexTagMarker
abstract class TagWithBlock(
    private val tagName: String,
    protected val children: MutableList<Element> = ArrayList()
): Element {

    protected fun <T: Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        this.children.add(tag)
        return tag
    }

    fun frame(
        frameTitle: String,
        attributes: Pair<String, String>,
        init: Frame.() -> Unit
    ) = initTag(Frame("frame", frameTitle, attributes), init)

    fun itemize(init: Itemize.() -> Unit) = initTag(Itemize(), init)

    fun enumerate(init: Enumerate.() -> Unit) = initTag(Enumerate(), init)

    fun math(formula: String) = initTag(Math(formula), {})

    fun customTag(
        name: String,
        parameter: Pair<String, String>,
        init: CustomTag.() -> Unit
    ) = initTag(CustomTag(name, parameter), init)

    override fun toOutputStream(stream: OutputStream, indent: String) {
        stream.write("$indent\\begin{$tagName}${System.lineSeparator()}".toByteArray())
        for (child in children) {
            child.toOutputStream(stream, "$indent    ")
        }
        stream.write("$indent\\end{$tagName}${System.lineSeparator()}".toByteArray())
    }

    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }
}
