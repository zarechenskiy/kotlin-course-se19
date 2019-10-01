package ru.hse.dsl

import java.io.OutputStream
import java.io.PrintWriter

interface Tag {
    fun render(builder: StringBuilder)
}

abstract class DocumentTag(private val name: String,
                           private val attribute: String? = null,
                           private val options: List<Pair<String, String>>? = null)
    : Tag {
    protected val children = arrayListOf<Tag>()

    protected fun <T : Tag> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    override fun render(builder: StringBuilder) {
        builder.append("\n\\begin{$name}${renderAttribute()}${renderOptions()}\n")
        for (c in children) {
            c.render(builder)
        }
        builder.append("\\end{$name}\n\n")
    }

    fun renderAttribute() = when(attribute) { null -> "" else -> "{$attribute}" }

    fun renderOptions() = options?.joinToString(",", "[", "]") { (key, value) -> "$key=$value" } ?: ""

    operator fun String.unaryPlus() {
        children.add(Text(this))
    }

    fun frame(title: String? = null, vararg options: Pair<String, String>, init: Frame.() -> Unit)
            = initTag(Frame(title, options.toList()), init)

    fun alignment(attribute: String, init: Alignment.() -> Unit) = initTag(Alignment(attribute), init)

    fun math(init: Math.() -> Unit) = initTag(Math(), init)

    fun itemize(init: Itemize.() -> Unit) = initTag(Itemize(), init)

    fun enumerate(init: Enumerate.() -> Unit) = initTag(Enumerate(), init)

    fun customTag(name: String, vararg options: Pair<String, String>, init: CustomTag.() -> Unit)
            = initTag(CustomTag(name, options.toList()), init)
}

abstract class PreambleTag(private val name: String,
                           private val attribute: String,
                           private val options: List<String>)
    : Tag {

    override fun render(builder: StringBuilder) {
        builder.append("\\$name${renderOptions()}${renderAttribute()}\n")
    }

    fun renderAttribute() = "{$attribute}"

    fun renderOptions() = options.joinToString(",", "[", "]")
}

class Tex : Tag {

    private val preamble = arrayListOf<PreambleTag>()
    private val documents = arrayListOf<Document>()

    fun documentclass(attribute: String)
            = initPreambleTag(Documentclass(attribute, emptyList()))

    fun usepackage(attribute: String, vararg options: String)
            = initPreambleTag(Usepackage(attribute, options.toList()))

    fun document(init: Document.() -> Unit)
            = initDocument(Document(), init)

    protected fun initDocument(tag: Document, init: Document.() -> Unit): Document {
        documents.add(tag)
        tag.init()
        return tag
    }

    protected fun <T : PreambleTag> initPreambleTag(tag: T): T {
        preamble.add(tag)
        return tag
    }

    override fun render(builder: StringBuilder) {
        preamble.map { child -> child.render(builder) }
        builder.append("\n")
        documents.map { child -> child.render(builder) }
    }

    fun toOutputStream(out: OutputStream) {
        val stringBuilder = StringBuilder()
        val writer = PrintWriter(out)
        render(stringBuilder)
        writer.print(stringBuilder)
        writer.flush()
    }
}

class Documentclass(attribute: String, options: List<String>) : PreambleTag("documentclass", attribute, options)

class Usepackage(attribute: String, options: List<String>) : PreambleTag("usepackage", attribute, options)

class Document : DocumentTag("document")

class Frame(title: String?, options: List<Pair<String, String>>) : DocumentTag("frame", title, options)

class Alignment(name: String) : DocumentTag(name)

class Math : DocumentTag("math")

class Itemize : DocumentTag("itemize") {
    fun item(init: Item.() -> Unit) = initTag(Item(), init)
}

class Enumerate : DocumentTag("enumerate") {
    fun item(init: Item.() -> Unit) = initTag(Item(), init)
}

class CustomTag(name: String, options: List<Pair<String, String>>) : DocumentTag(name, options = options)

class Item : DocumentTag("item") {

    override fun render(builder: StringBuilder) {
        for (text in children) {
            builder.append("    \\item ")
            text.render(builder)
        }
    }
}

class Text(val text: String) : Tag {
    override fun render(builder: StringBuilder) {
        builder.append(text + "\n")
    }
}

fun tex(init: Tex.() -> Unit): Tex {
    val document = Tex()
    document.init()
    return document
}
