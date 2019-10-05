package ru.hse.spb

import java.io.OutputStream

/**
 * Base abstraction for any LaTeX tag
 * */
interface Element {
    fun toOutputStream(stream: OutputStream, indent: String)
}

/**
 * Base abstraction for text
 * */
class TextElement(private val text: String): Element {

    override fun toOutputStream(stream: OutputStream, indent: String) {
        stream.write("$indent$text${System.lineSeparator()}".toByteArray())
    }
}

/**
 * Abstraction for any simple tag without body
 * */
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

/**
 * Abstraction for tag with complex nested body
 * */
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
        vararg attributes: Pair<String, String>,
        init: Frame.() -> Unit
    ) = initTag(Frame("frame", frameTitle, attributes.toMap()), init)

    fun itemize(init: Itemize.() -> Unit) = initTag(Itemize(), init)

    fun enumerate(init: Enumerate.() -> Unit) = initTag(Enumerate(), init)

    fun math(formula: String) = initTag(Math(formula), {})

    fun flushleft(init: FlushLeft.() -> Unit) = initTag(FlushLeft(), init)

    fun flushright(init: FlushRight.() -> Unit) = initTag(FlushRight(), init)

    fun center(init: Center.() -> Unit) = initTag(Center(), init)

    fun customTag(
        name: String,
        vararg attributes: Pair<String, String>,
        init: CustomTag.() -> Unit
    ) = initTag(CustomTag(name, attributes.toMap()), init)

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
