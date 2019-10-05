package ru.hse.spb

import java.io.PrintStream

@DslMarker
annotation class TexElemMarker

@TexElemMarker
interface TexElement {
    fun toOutputStream(stream: PrintStream)
}

class Text(private val content: String) : TexElement {
    override fun toOutputStream(stream: PrintStream) {
        stream.println(content.trimMargin())
    }
}

class Item : Tag("item") {
    override fun toOutputStream(stream: PrintStream) {
        stream.println("\\$name")
        children.forEach { it.toOutputStream(stream) }
    }
}

class Math(private val formula: String) : Tag("math") {
    override fun toOutputStream(stream: PrintStream) {
        stream.println("\\$name{$formula}")
        children.forEach { it.toOutputStream(stream) }
    }
}

class DocumentClass(private val className: String) : Tag("documentclass") {
    override fun toOutputStream(stream: PrintStream) {
        stream.println("\\$name{$className}")
        children.forEach { it.toOutputStream(stream) }
    }
}

class UsePackage(private vararg val args: String) : Tag("usepackage") {
    override fun toOutputStream(stream: PrintStream) {
        stream.println("\\$name{${args.joinToString(",")}}")
        children.forEach { it.toOutputStream(stream) }
    }
}

class Document : Block("document")

class Itemize : Block("itemize") {
    fun item(init: Item.() -> Unit) = newElem(Item(), init)
}

class Enumerate : Block("enumerate") {
    fun item(init: Item.() -> Unit) = newElem(Item(), init)
}

class Frame(val frameTitle: String, vararg args: Pair<String, String>)
    : Tag("frame", *args) {
    override fun toOutputStream(stream: PrintStream) {
        stream.println(
                when (options.isEmpty()) {
                    true -> "\\begin{$name}"
                    false -> "\\begin{$name}[${options.joinToString(",") { it.first + "=" + it.second }}]"
                }
        )
        stream.println("\\frametitle{${frameTitle}}")
        for (child in children) {
            child.toOutputStream(stream)
        }
        stream.println("\\end{$name}")
    }
}

class CustomTag(name: String, vararg args: Pair<String, String>) :
        Tag(name, *args)

abstract class Block(private val name: String) : TexElement {
    protected val children = mutableListOf<TexElement>()

    fun <T : TexElement> newElem(elem: T, init: T.() -> Unit): T {
        elem.init()
        this.children.add(elem)
        return elem
    }

    fun itemize(init: Itemize.() -> Unit) = newElem(Itemize(), init)
    fun enumerate(init: Enumerate.() -> Unit) = newElem(Enumerate(), init)
    fun math(formula: String) = newElem(Math(formula), {})
    fun documentClass(formula: String) = newElem(DocumentClass(formula), {})
    fun usepackage(vararg args: String) = newElem(UsePackage(*args), {})
    fun frame(frameTitle: String, vararg args: Pair<String, String>, init: Frame.() -> Unit) =
            newElem(Frame(frameTitle, *args), init)

    fun customTag(name: String, vararg args: Pair<String, String>, init: Tag.() -> Unit) =
            newElem(CustomTag(name, *args), init)

    operator fun String.unaryPlus() {
        children.add(Text(this))
    }

    override fun toOutputStream(stream: PrintStream) {
        stream.println("\\begin{$name}")
        for (child in children) {
            child.toOutputStream(stream)
        }
        stream.println("\\end{$name}")
    }
}

abstract class Tag(val name: String, vararg val options: Pair<String, String>) : Block(name) {
    override fun toOutputStream(stream: PrintStream) {
        stream.println(
                when (options.isEmpty()) {
                    true -> "\\begin{$name}"
                    false -> "\\begin{$name}[${options.joinToString(",") { it.first + "=" + it.second }}]"
                }
        )
        for (child in children) {
            child.toOutputStream(stream)
        }
        stream.println("\\end{$name}")
    }
}

fun document(init: Document.() -> Unit): Document {
    val document = Document()
    document.init()
    return document
}