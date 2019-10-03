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

class DocumentClass(docClass: String): Tag("documentclass", docClass)
class Package(name: String): Tag("usepackage", name)
class FrameTitle(title: String): Tag("frametitle", title)

class Math(formula: String): Tag("math", formula) {

    override fun toOutputStream(stream: OutputStream, indent: String) {
        stream.write("$indent\$$arg\$${System.lineSeparator()}".toByteArray())
    }
}

class Item: TagWithBlock("item") {

    override fun toOutputStream(stream: OutputStream, indent: String) {
        stream.write("$indent\\item${System.lineSeparator()}".toByteArray())
        for (child in children) {
            child.toOutputStream(stream, "$indent    ")
        }
    }
}

class Frame(
    tagName: String,
    frameTitle: String,
    private val attributes: Pair<String, String>
) : TagWithBlock(tagName) {

    init {
        initTag(FrameTitle(frameTitle), {})
    }

    override fun toOutputStream(stream: OutputStream, indent: String) {
        stream.write("$indent\\begin{frame}[${attributes.first}=${attributes.second}]${System.lineSeparator()}".toByteArray())
        for (child in children) {
            child.toOutputStream(stream, "$indent    ")
        }
        stream.write("$indent\\end{frame}${System.lineSeparator()}".toByteArray())
    }
}

class Itemize: TagWithBlock("itemize") {

    fun item(init: Item.() -> Unit) = initTag(Item(), init)
}
class Enumerate: TagWithBlock("enumerate") {

    fun item(init: Item.() -> Unit) = initTag(Item(), init)
}

class Document: TagWithBlock("document") {

    fun documentClass(docClass: String) {
        val documentClass = DocumentClass(docClass)
        this.children.add(documentClass)
    }

    fun usepackage(vararg packages: String) {
        for (p in packages) {
            this.children.add(Package(p))
        }
    }

    fun toOutputStream(stream: OutputStream) {
        super.toOutputStream(stream, "")
    }
}

fun document(init: Document.() -> Unit): Document {
    val document = Document()
    document.init()
    return document
}



fun main() {
    val rows = arrayListOf("a", "b", "c")
    document {
        usepackage("babel", "russian", "minted", "enumerate")
        documentClass("beamer")
        frame("Best", "allowframebreaks" to "true") {
            itemize {
                for (row in rows) {
                    item { +"$row" }
                }
            }
        }
    }.toOutputStream(System.out)
}
