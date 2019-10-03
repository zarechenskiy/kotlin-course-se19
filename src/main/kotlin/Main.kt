package ru.hse.spb

import java.io.OutputStream

interface Element {
    fun toOutputStream(stream: OutputStream, indent: String)
}

abstract class Tag(
    private val tagName: String,
    private val arg: String?
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

    fun frame(init: Frame.() -> Unit) = initTag(Frame(), init)

    fun itemize(init: Itemize.() -> Unit) = initTag(Itemize(), init)

    fun enumerate(init: Enumerate.() -> Unit) = initTag(Enumerate(), init)

    override fun toOutputStream(stream: OutputStream, indent: String) {
        stream.write("$indent\\begin{$tagName}${System.lineSeparator()}".toByteArray())
        for (child in children) {
            child.toOutputStream(stream, "$indent    ")
        }
        stream.write("$indent\\end{$tagName}${System.lineSeparator()}".toByteArray())
    }
}

class DocumentClass(docClass: String): Tag("documentclass", docClass)
class Package(name: String): Tag("usepackage", name)
class FrameTitle(title: String): Tag("frametitle", title)
class Item: TagWithBlock("item") {

    override fun toOutputStream(stream: OutputStream, indent: String) {
        stream.write("$indent\\item${System.lineSeparator()}".toByteArray())
        for (child in children) {
            child.toOutputStream(stream, "$indent    ")
        }
    }
}

class Frame: TagWithBlock("frame") {

    fun frametitle(title: String) {
        val frameTitle = FrameTitle(title)
        this.children.add(frameTitle)
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
    document {
        usepackage("babel", "russian", "minted", "enumerate")
        documentClass("beamer")
        enumerate {
            item {

            }
            frame {
                frame {
                    enumerate {
                        item {

                        }
                    }
                }
            }
        }
    }.toOutputStream(System.out)
}
