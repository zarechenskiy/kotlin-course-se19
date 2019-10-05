package ru.hse.spb

import java.io.DataOutputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.util.*

interface Tag {
    fun render(builder: StringBuilder)

    fun toOutputStream(out: OutputStream) {
        val writer = PrintWriter(out)
        val stringBuilder = StringBuilder()
        render(stringBuilder)
        writer.print(stringBuilder)
        writer.flush()
    }
}

@DslMarker
annotation class TexMarker

class TextElement(private val text: String) : Tag {
    override fun render(builder: StringBuilder) {
        builder.append("$text\n")
    }
}

open class WrappingTag(private val name: String,
                       private val attributes: List<Pair<String, String>> = Collections.emptyList(),
                       private val subTag: Pair<String, String>? = null) : Tag {
    protected val children = arrayListOf<Tag>()

    protected fun <T : Tag> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    override fun render(builder: StringBuilder) {
        builder.append("\\begin{$name}${renderAttributes()}\n")
        if (subTag != null) {
            builder.append("\\${subTag.first}{${subTag.second}}\n")
        }
        for (c in children) {
            c.render(builder)
        }
        builder.append("\\end{$name}\n")
    }

    private fun renderAttributes(): String {
        if (attributes.isEmpty()) {
            return ""
        }
        return attributes.joinToString(",", "[", "]") {
            pair -> "${pair.first}=${pair.second}"
        }
    }

    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }

    fun frame(frametitle: String, vararg attributes: Pair<String, String>,
              init: Frame.() -> Unit) = initTag(Frame(frametitle, attributes.toList()), init)

    fun itemize(init: Itemize.() -> Unit) = initTag(Itemize(), init)

    fun enumerate(init: Enumerate.() -> Unit) = initTag(Enumerate(), init)

    fun math(init: Math.() -> Unit) = initTag(Math(), init)

    fun alignment(init: Align.() -> Unit) = initTag(Align(), init)

    fun customWrappingTag(tagTitle: String, vararg attributes: Pair<String, String>,
                          init: CustomWrappingTag.() -> Unit) =
            initTag(CustomWrappingTag(tagTitle, attributes.toList()), init)

    fun customSingleTag(tagTitle: String, option: String, vararg attributes: String,
                          init: CustomSingleTag.() -> Unit) =
            initTag(CustomSingleTag(tagTitle, option, attributes.toList()), init)
}

class CustomWrappingTag(tagTitle: String, attributes: List<Pair<String, String>>) :
        WrappingTag(tagTitle, attributes)

class CustomSingleTag(tagTitle: String, option: String, attributes: List<String>) :
        SingleTag(tagTitle, option, attributes)

@TexMarker
open class SingleTag(private val name: String,
                     private val option: String,
                     private val attributes: List<String>) : Tag {

    override fun render(builder: StringBuilder) {
        builder.append("\\$name${renderOptions()}${renderAttributes()}\n")
    }

    private fun renderOptions(): String {
        if (option.isEmpty()) {
            return ""
        }
        return "{$option}"
    }

    private fun renderAttributes(): String {
        if (attributes.isEmpty()) {
            return ""
        }
        return attributes.joinToString(",", "[", "]")
    }
}

class Align : WrappingTag("align")

class Math : WrappingTag("math")

class Enumerate : WrappingTag("enumerate") {
    fun item(init: Item.() -> Unit) = initTag(Item(), init)
}

class Item : SingleTag("item", "", Collections.emptyList()) {
    private val children = arrayListOf<Tag>()

    override fun render(builder: StringBuilder) {
        super.render(builder)
        for (c in children) {
            c.render(builder)
        }
    }

    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }
}

class Itemize : WrappingTag("itemize") {
    fun item(init: Item.() -> Unit) = initTag(Item(), init)
}

class Frame(frametitle: String, parameters: List<Pair<String, String>>) :
        WrappingTag("frame", parameters.toList(), Pair("frametitle", frametitle))

@TexMarker
class Document : WrappingTag("document") {
    private val singleTags = arrayListOf<SingleTag>()

    override fun render(builder: StringBuilder) {
        for (singleTag in singleTags.reversed()) {
            children.add(0, singleTag)
        }
        super.render(builder)
    }

    fun userPackage(packageName: String, vararg parameters: String) {
        singleTags.add(SingleTag("userpackage", packageName, parameters.toList()))
    }

    fun documentClass(documentName: String, vararg parameters: String) {
        singleTags.add(SingleTag("documentclass", documentName, parameters.toList()))
    }
}

fun document(init: Document.() -> Unit): Document {
    val document = Document()
    document.init()
    return document
}