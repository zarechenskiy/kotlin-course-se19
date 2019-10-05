package ru.hse.spb

//import sun.jvm.hotspot.oops.CellTypeState.value
//import sun.security.ssl.HandshakeOutStream
import java.io.PrintStream


interface Element {
    fun toOutputStream(outStream: PrintStream)
}

class TextElement(val text: String) : Element {
    override fun toOutputStream(outStream: PrintStream) {
        outStream.println(text)
    }
}

abstract class Tag(val name: String, vararg val attrs: String) : Element {
    fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        return tag
    }

    override fun toOutputStream(outStream: PrintStream) {
        outStream.println("\\$name${attrs.joinToString(", ", "(", ")")}")
    }
}


@DslMarker
annotation class TexTagMarker

@TexTagMarker
abstract class TagWithChildren(val name: String, vararg val attrs: Pair<String, String>) : Element {
    val children = arrayListOf<Element>()

    fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    override fun toOutputStream(outStream: PrintStream) {
        outStream.println("\\begin{$name}")
        if (attrs.isNotEmpty()) {
            outStream.println(attrs.map { (value, key) -> "$value=$key" })
        }
        outStream.println(children.map { it.toOutputStream(outStream) })
        outStream.println("\\end{$name}")
    }
}


class Document : TagWithChildren("document") {
    fun documentClass(className: String) = initTag(DocumentClass(className), {})
    fun usepackage(vararg packageNames: String) = initTag(Usepackage(*packageNames), {})
}

class DocumentClass(className: String) : Tag("documentClass", className)
class Usepackage(vararg packageNames: String) : Tag("usepackage", *packageNames)


fun document(init: Document.() -> Unit): Document {
    val document = Document()
    document.init()
    return document
}