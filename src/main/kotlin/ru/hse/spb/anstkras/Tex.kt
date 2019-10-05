package ru.hse.spb.anstkras

import java.io.PrintStream

interface Element {
    fun render(builder: StringBuilder, indent: String)
}

class TextElement(val text: String) : Element {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent$text\n")
    }
}

abstract class Tag(val name: String) : Element {
    val children = arrayListOf<Element>()

    protected fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    protected fun <T : Element> initTag(tag: T): T {
        children.add(tag)
        return tag
    }

    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\begin{$name}\n")
        for (c in children) {
            c.render(builder, indent + "  ")
        }
        builder.append("$indent\\end{$name}\n")
    }

    fun customTag(name: String, vararg pair: Pair<String, String>) = initTag(CustomTag(name, *pair))

    fun customTag(name: String, vararg pair: Pair<String, String>, init: CustomTag.() -> Unit) =
        initTag(CustomTag(name, *pair), init)
}

abstract class TagWithText(name: String) : Tag(name) {
    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }

    fun center(init: Center.() -> Unit) = initTag(Center(), init)
    fun flushleft(init: FlushLeft.() -> Unit) = initTag(FlushLeft(), init)
    fun flushright(init: FlushRight.() -> Unit) = initTag(FlushRight(), init)
}

abstract class TagWithContent(name: String) : TagWithText(name) {
    fun math(init: Math.() -> Unit) = initTag(Math(), init)

    fun frame(frameTitle: String, option: Pair<String, String>, init: Frame.() -> Unit) =
        initTag(Frame(frameTitle, option), init)

    fun itemize(init: Itemize.() -> Unit) = initTag(Itemize(), init)

    fun enumerate(init: Enumerate.() -> Unit) = initTag(Enumerate(), init)
}

class CustomTag(tagName: String, vararg val args: Pair<String, String>) :
    TagWithText(tagName) {

    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\begin{$name}")
        builder.append(args.joinToString(prefix = "[", postfix = "]") { "${it.first} = ${it.second}" })
        builder.append("\n")
        for (c in children) {
            c.render(builder, indent + "  ")
        }
        builder.append("$indent\\end{$name}\n")
    }
}

fun document(init: Document.() -> Unit): Document {
    val doc = Document()
    doc.init()
    return doc
}

class Document() : TagWithContent("document") {
    fun documentClass(init: String) = initTag(DocumentClass(init))

    fun usePackage(vararg init: String) = initTag(UsePackage(*init))

    fun toOutputStream(stream: PrintStream) {
        val stringBuilder = StringBuilder()
        render(stringBuilder, "")
        stream.println(stringBuilder)
    }
}

class DocumentClass(private val type: String) : Tag("documentclass") {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\$name{$type}\n")
    }
}

class UsePackage(private vararg val packs: String) : Tag("usepackage") {
    override fun render(builder: StringBuilder, indent: String) {
        for (pack in packs) {
            builder.append("$indent\\$name{$pack}\n")
        }
    }
}

class Frame(private val title: String, private val option: Pair<String, String>) : TagWithContent("frame") {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\begin{$name}[${option.first} = ${option.second}]\n")
        builder.append("$indent\\frametitle{$title}\n")
        for (c in children) {
            c.render(builder, indent + "    ")
        }
        builder.append(builder.append("$indent\\end{$name}\n"))
    }
}

@DslMarker
annotation class ItemTagMarker

@ItemTagMarker
class Itemize : Tag("itemize") {
    fun item(init: Item.() -> Unit) = initTag(Item(), init)
}

@ItemTagMarker
class Enumerate : Tag("enumerate") {
    fun item(init: Item.() -> Unit) = initTag(Item(), init)
}

@ItemTagMarker
class Item : TagWithContent("item") {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\$name\n")
        for (c in children) {
            c.render(builder, indent + "    ")
        }
    }
}

class Center : TagWithText("center")
class FlushLeft : TagWithText("flushleft")
class FlushRight : TagWithText("flushright")

class Math : TagWithText("math") {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent$\n")
        for (c in children) {
            c.render(builder, indent + "    ")
        }
        builder.append("$indent$\n")
    }
}