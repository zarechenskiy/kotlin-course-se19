package ru.hse.spb

import java.io.OutputStream

interface Elem {
    fun render(builder: StringBuilder, indent: String)

}

class TextElem(private val text: String) : Elem {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append(indent)
        builder.append(text)
    }
}

abstract class Property(private val name: String, vararg val params: String) : Elem {
    override fun render(builder: StringBuilder, indent: String) {
        for (param in params) {
            builder.append(indent)
            builder.append("\\$name{$param}\n")
        }
    }
}

@DslMarker
annotation class LatexTagMarker

@LatexTagMarker
abstract class Tag(protected var beginMarker: String,
                   protected val endMarker: String,
                   protected val separator: String = "\n") : Elem {
    var children: MutableList<Elem> = mutableListOf()

    protected fun <T : Tag> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    fun customTag(name: String, param: Pair<String, String>? = null, init: CustomTag.() -> Unit) {
        initTag(CustomTag(name, param), init)
    }

    operator fun String.unaryPlus() {
        children.add(TextElem(this))
    }

    override fun render(builder: StringBuilder, indent: String) {
        builder.append(indent)
        builder.append(beginMarker)
        var beforeNextChild = ""
        for (child in children) {
            builder.append(beforeNextChild)
            if (indent.endsWith("\n")) {
                child.render(builder, indent)
            } else {
                child.render(builder, "")
            }
            beforeNextChild = separator
        }
        builder.append(endMarker)
    }

    override fun toString(): String {
        val builder = StringBuilder()
        render(builder, "")
        return builder.toString()
    }

    fun toOutputStream(stream: OutputStream) {
        stream.write(toString().toByteArray())
    }
}

class MathTag : Tag("$", "$")

abstract class BeginEndTag(name: String, params: Pair<String, String>? = null, separator: String ="\n") :
        Tag(beginMarker = "\\begin{$name}" + if (params != null) {
            "[${params.first}=${params.second}]\n"
        } else {
            "\n"
        },
                endMarker = "\n\\end{$name}",
                separator=separator) {


    fun itemize(init: Itemize.() -> Unit) = initTag(Itemize(), init)

    fun enumerate(init: Enumerate.() -> Unit) = initTag(Enumerate(), init)

    fun align(params: Pair<String, String>? = null, init: Align.() -> Unit) = initTag(Align(params), init)

    fun math(init: MathTag.() -> Unit) = initTag(MathTag(), init)

}

class Document(params: Pair<String, String>?) : BeginEndTag("document", params) {
    private var properties: MutableList<Property> = mutableListOf()

    override fun render(builder: StringBuilder, indent: String) {
        for (property in properties) {
            property.render(builder, indent)
        }
        super.render(builder, indent)
    }

    fun documentClass(name: String) {
        properties.add(DocumentClass(name))
    }

    fun usepackage(vararg packages: String) {
        properties.add(UsePackage(*packages))
    }

    fun frame(frameTitle: String, param: Pair<String, String>? = null, init: Frame.() -> Unit) {
        initTag(Frame(frameTitle, param), init)
    }

}

class Frame(arguments: Pair<String, String>?) : BeginEndTag("frame", arguments) {
    constructor(title: String, arguments: Pair<String, String>?) : this(arguments) {
        beginMarker = beginMarker.substring(0, beginMarker.length - 1) + "\\frametitle{$title}\n"
    }
}

class Itemize() : BeginEndTag("itemize") {
    fun item(init: Item.() -> Unit) = initTag(Item(), init)
}

class Enumerate(): BeginEndTag("enumerate") {
    fun item(init: Item.() -> Unit) = initTag(Item(), init)
}

class Line(): Tag("", "", "&") {
    constructor(vararg parts: String) : this() {
        for (part in parts) {
            children.add(TextElem(part))
        }
    }

    override fun render(builder: StringBuilder, indent: String) {
        super.render(builder, "$indent    ")
    }
}

class Item: Tag("\\item ", "") {
    override fun render(builder: StringBuilder, indent: String) {
        super.render(builder, "$indent    ")
    }
}

class Align(params: Pair<String, String>?) : BeginEndTag("align", params, separator = " \\\\\n") {
    fun line(vararg parts: String) = children.add(Line(*parts))

}

class CustomTag(name: String, params: Pair<String, String>?) : BeginEndTag(name, params)

class DocumentClass(name: String) : Property("documentclass", name)

class UsePackage(vararg packages: String) : Property("usepackage", *packages)

fun document(params: Pair<String, String>? = null, init: Document.() -> Unit): Document {
    val document = Document(params)
    document.init()
    return document
}