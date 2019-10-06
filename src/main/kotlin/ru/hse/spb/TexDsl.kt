package ru.hse.spb

import java.io.PrintStream


@DslMarker
annotation class TexTagMarker


interface Element {
    fun toOutputStream(outStream: PrintStream, ident: String = "")
}

class TextElement(private val text: String) : Element {
    override fun toOutputStream(outStream: PrintStream, ident: String) {
        outStream.println("$ident$text")
    }
}

abstract class OneLineTag(private val name: String, private vararg val attrs: String) : Element {
    override fun toOutputStream(outStream: PrintStream, ident: String) {
        outStream.print("$ident\\$name")
        outStream.println(
            if (attrs.isNotEmpty()) {
                attrs.joinToString(", ", "{", "}")
            } else {
                ""
            }
        )
    }
}

@TexTagMarker
abstract class TagWithChildren(private val name: String, private vararg val opts: Pair<String, String>) : Element {
    val children = arrayListOf<Element>()

    fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    override fun toOutputStream(outStream: PrintStream, ident: String) {
        outStream.print("$ident\\begin{$name}")
        outStream.println(
            if (opts.isNotEmpty()) {
                opts.joinToString(", ", "[", "]") { (k, v) -> "$k=$v" }
            } else {
                ""
            }
        )
        children.map { it.toOutputStream(outStream, "$ident    ") }
        outStream.println("$ident\\end{$name}")
    }

}

abstract class TagWithChildrenAndText(name: String,
                                      vararg opts: Pair<String, String>) : TagWithChildren(name, *opts) {
    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }
}


class Document : TagWithChildren("document") {
    fun documentClass(className: String) = initTag(DocumentClass(className), {})
    fun usepackage(vararg packageNames: String) = initTag(UsePackage(*packageNames), {})
    fun frame(frameTitle: String, vararg opts: Pair<String, String>, init: Frame.() -> Unit) {
        val frame = Frame(*opts)
        frame.initTag(FrameTitle(frameTitle), {})
        initTag(frame, init)
    }
}

class DocumentClass(className: String) : OneLineTag("documentClass", className)

class UsePackage(vararg packageNames: String) : OneLineTag("usepackage", *packageNames)

class Frame(vararg opts: Pair<String, String>) : TagWithChildren("frame", *opts) {
    fun itemize(init: Itemize.() -> Unit) = initTag(Itemize(), init)

    fun enumerate(init: Enumerate.() -> Unit) = initTag(Enumerate(), init)

    fun math(init: Math.() -> Unit) = initTag(Math(), init)

    fun flushleft(init: FlushLeft.() -> Unit) = initTag(FlushLeft(), init)

    fun flushright(init: FlushRight.() -> Unit) = initTag(FlushRight(), init)

    fun centering(init: Centering.() -> Unit) = initTag(Centering(), init)

    fun customTag(tagName: String, vararg opts: Pair<String, String>, init: CustomTag.() -> Unit) =
        initTag(CustomTag(tagName, *opts), init)
}

class FrameTitle(title: String) : OneLineTag("frametitle", title)

class Itemize() : TagWithChildren("itemize") {
    fun item(init: Item.() -> Unit) = initTag(Item(), init)
}

open class Item() : OneLineTag("item") {
    protected val children = arrayListOf<Element>()

    open operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }

    override fun toOutputStream(outStream: PrintStream, ident: String) {
        super.toOutputStream(outStream, ident)
        children.map { it.toOutputStream(outStream, ident) }
    }
}

class EnumItem(private val num: Int) : Item() {
    override operator fun String.unaryPlus() {
        super.children.add(TextElement("$num. $this"))
    }
}

class Enumerate() : TagWithChildren("enumerate") {
    private var num = 0
    fun item(init: Item.() -> Unit) = initTag(EnumItem(num++), init)
}

class Math() : TagWithChildrenAndText("math")

class FlushLeft() : TagWithChildrenAndText("flushleft")

class FlushRight() : TagWithChildrenAndText("flushright")

class Centering() : TagWithChildrenAndText("centring")

class CustomTag(tagName: String, vararg opts: Pair<String, String>) : TagWithChildrenAndText(tagName, *opts)

fun document(init: Document.() -> Unit): Document {
    val document = Document()
    document.init()
    return document
}