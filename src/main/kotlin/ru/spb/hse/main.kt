package ru.spb.hse

import java.io.DataOutputStream
import java.io.OutputStream

@DslMarker
annotation class TeXTagMarker

interface Element {
    fun render(builder: StringBuilder, indent: String)
}

class TextElement(private val text: String) : Element {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent$text\n")
    }
}

@TeXTagMarker
abstract class NestedTag(private val tagName: String,
                         private val parameters: Array<out Pair<String, String>>) : Element {
    private val children = arrayListOf<Element>()

    protected fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }

    fun enumerate(vararg parameters: Pair<String, String>, init: Enumerate.() -> Unit) =
        initTag(Enumerate(parameters), init)

    fun itemize(vararg parameters: Pair<String, String>, init: Itemize.() -> Unit) =
        initTag(Itemize(parameters), init)

    fun math(vararg parameters: Pair<String, String>, init: Math.() -> Unit) =
        initTag(Math(parameters), init)

    fun alignment(vararg parameters: Pair<String, String>, init: Alignment.() -> Unit) =
        initTag(Alignment(parameters), init)

    fun customTag(name: String, vararg parameters: Pair<String, String>, init: CustomTag.() -> Unit) =
        initTag(CustomTag(name, parameters), init)

    protected fun renderTag(builder: StringBuilder, indent: String) {
        builder.append("$indent\\begin")
        if (parameters.isNotEmpty()) {
            builder.append("[${parameters.joinToString(separator = ",") {
                    x -> x.first + "=" + x.second }}]")
        }
        builder.append("{$tagName}\n")
    }

    protected fun renderChildren(builder: StringBuilder, indent: String) {
        for (child in children) {
            child.render(builder, "$indent    ")
        }
    }

    protected fun closeTag(builder: StringBuilder, indent: String) {
        builder.append("$indent\\end{$tagName}\n")
    }

    override fun render(builder: StringBuilder, indent: String) {
        renderTag(builder, indent)
        renderChildren(builder, indent)
        closeTag(builder, indent)
    }
}

abstract class Enumeration(name: String,
                  parameters: Array<out Pair<String, String>>) : NestedTag(name, parameters) {
    fun item(init: Item.() -> Unit): Item = initTag(Item(), init)
}

class Item : NestedTag("item", arrayOf()) {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\item\n")
        renderChildren(builder, indent)
    }
}

class Enumerate(parameters: Array<out Pair<String, String>>) : Enumeration("enumerate", parameters)
class Itemize(parameters: Array<out Pair<String, String>>) : Enumeration("itemize", parameters)
class Math(parameters: Array<out Pair<String, String>>) : NestedTag("math", parameters)
class Alignment(parameters: Array<out Pair<String, String>>) : NestedTag("align*", parameters)
class CustomTag(name: String,
                parameters: Array<out Pair<String, String>>) : NestedTag(name, parameters)

class DocumentClass(private val clazz: String,
                    private val parameters: Array<out String>) : Element {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\documentclass")
        if (parameters.isNotEmpty()) {
            builder.append("[${parameters.joinToString(separator=",")}]")
        }
        builder.append("{$clazz}\n")
    }
}

class Package(private val packageName: String,
              private val parameters: Array<out String>) : Element {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\usepackage")
        if (parameters.isNotEmpty()) {
            builder.append("[${parameters.joinToString(separator=",")}]")
        }
        builder.append("{$packageName}\n")
    }
}

class Frame(private val frameTitle: String,
            parameters: Array<out Pair<String, String>>) : NestedTag("frame", parameters) {
    override fun render(builder: StringBuilder, indent: String) {
        renderTag(builder, indent)
        if (frameTitle.isNotBlank()) {
            builder.append("$indent    \\frametitle{$frameTitle}\n")
        }
        renderChildren(builder, indent)
        closeTag(builder, indent)
    }
}

class Document : NestedTag("document", arrayOf()) {
    private var documentClass: DocumentClass? = null
    private val packages = arrayListOf<Package>()

    fun documentClass(clazz: String, vararg parameters: String) {
        documentClass = DocumentClass(clazz, parameters)
    }

    fun usepackage(packageName: String, vararg parameters: String) {
        packages.add(Package(packageName, parameters))
    }

    fun frame(frameTitle: String = "",
              vararg parameters: Pair<String, String>,
              init: Frame.() -> Unit): Frame = initTag(Frame(frameTitle, parameters), init)

    override fun render(builder: StringBuilder, indent: String) {
        documentClass!!.render(builder, indent)
        for (pack in packages) {
            pack.render(builder, indent)
        }
        super.render(builder, indent)
    }

    override fun toString(): String {
        val builder = StringBuilder()
        render(builder, "")
        return builder.toString()
    }

    fun toOutputStream(outputStream: OutputStream) {
        DataOutputStream(outputStream).writeUTF(toString())
    }
}

fun document(init: Document.() -> Unit): Document {
    val document = Document()
    document.init()
    return document
}