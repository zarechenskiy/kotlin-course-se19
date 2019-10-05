package ru.hse.spb

abstract class Property(name: String) : Elem(name) {

}

abstract class Elem(name: String) {

}

abstract class Tag(beginMarker: String, endMarker: String)

class MathTag() : Tag("$", "$")

abstract class BeginEndTag(name: String, params: Pair<String, String>?) : Tag("\\begin{$name}\n", "\\end{$name}\n") {
    var children: MutableList<Tag> = mutableListOf()

    protected fun <T : Tag> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

}

class Document(params: Pair<String, String>?) : BeginEndTag("document", params) {
    var properties: MutableList<Property> = mutableListOf()

    fun documentClass(name: String) {
        properties.add(DocumentClass(name))
    }

    fun usepackage(vararg packages: String) {
        properties.add(UsePackage(*packages))
    }

    fun frame(frameTitle: String, param: Pair<String, String>, init: Frame.() -> Unit) {
        initTag(Frame(frameTitle, param), init)
    }

    fun itemize(init: Itemize.() -> Unit) {
        initTag(Itemize(), init)
    }

    fun enumerate(init: Enumerate.() -> Unit) {
        initTag(Enumerate(), init)
    }

    fun align(params: Pair<String, String>?, init: Align.() -> Unit) {
        initTag(Align(params), init)
    }

}

class Frame(title: String, arguments: Pair<String, String>?) : BeginEndTag("frame", arguments)

class Itemize() : BeginEndTag("itemize", null)

class Enumerate(): BeginEndTag("enumerate", null)

class Align(params: Pair<String, String>?) : BeginEndTag("align", params)

class DocumentClass(name: String) : Property("documentclass") {

}

class UsePackage(vararg packages: String) : Property("usepackage") {

}

fun document(params: Pair<String, String>? = null, init: Document.() -> Unit): Document {
    val document = Document(params)
    document.init()
    return document
}