package ru.hse.spb

import java.io.OutputStream

class CustomTag(
    private val tagName: String,
    private val attribute: Pair<String, String>
): TagWithBlock(tagName) {

    override fun toOutputStream(stream: OutputStream, indent: String) {
        stream.write(
            "$indent\\begin{$tagName}[${attribute.first}=${attribute.second}]${System.lineSeparator()}".toByteArray()
        )
        for (child in children) {
            child.toOutputStream(stream, "$indent    ")
        }
        stream.write("$indent\\end{$tagName}${System.lineSeparator()}".toByteArray())
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
    private val attribute: Pair<String, String>
) : TagWithBlock(tagName) {

    init {
        initTag(FrameTitle(frameTitle), {})
    }

    override fun toOutputStream(stream: OutputStream, indent: String) {
        stream.write("$indent\\begin{frame}[${attribute.first}=${attribute.second}]${System.lineSeparator()}".toByteArray())
        for (child in children) {
            child.toOutputStream(stream, "$indent    ")
        }
        stream.write("$indent\\end{frame}${System.lineSeparator()}".toByteArray())
    }
}

class FlushLeft: TagWithBlock("flushleft")
class FlushRight: TagWithBlock("flushright")
class Center: TagWithBlock("center")

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
