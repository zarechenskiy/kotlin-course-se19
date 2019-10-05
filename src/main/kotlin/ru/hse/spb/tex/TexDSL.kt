package ru.hse.spb.tex

import java.io.ByteArrayOutputStream
import java.io.PrintWriter

@DslMarker
annotation class TexDSLMarker

@TexDSLMarker
abstract class TexElement {
    abstract fun toOutputStream(outputStream: PrintWriter, indent: String = "")
}

class Text(private val text: String): TexElement() {
    override fun toOutputStream(outputStream: PrintWriter, indent: String) {
        outputStream.println("$indent$text")
    }
}

open class Block(private val name: String, private vararg val args: Pair<String,String>): TexElement() {
    protected val children = mutableListOf<TexElement>()

    override fun toOutputStream(outputStream: PrintWriter, indent: String) {
        outputStream.print("$indent\\begin{$name}")
        if (args.isNotEmpty()) {
            outputStream.print(args
                    .joinToString(", ", "[", "]", transform = { it.first + "=" + it.second }))
        }
        outputStream.println()
        children.forEach { it.toOutputStream(outputStream, "$indent ") }
        outputStream.println("$indent\\end{$name}")
    }

    protected fun <T: TexElement> addChild(element: T, init: T.() -> Unit): T {
        element.init()
        children.add(element)
        return element
    }

    fun itemize(init: Itemize.() -> Unit) = addChild(Itemize(), init)
    fun enumerate(init: Enumerate.() -> Unit) = addChild(Enumerate(), init)
    fun math(init: Math.() -> Unit) = addChild(Math(), init)
    fun frame(frameTitle: String? = null, vararg args: Pair<String,String>, init: Frame.() -> Unit): Frame {
        val frame = Frame(*args)
        frameTitle?.let { frame.children.add(FrameTitle(frameTitle)) }
        frame.init()
        children.add(frame)
        return frame
    }
    fun flushleft(init: FlushLeft.() -> Unit) = addChild(FlushLeft(), init)
    fun flushright(init: FlushRight.() -> Unit) = addChild(FlushRight(), init)
    fun center(init: Center.() -> Unit) = addChild(Center(), init)

    operator fun String.unaryPlus() {
        children.add(Text(this.trimIndent()))
    }
}

class Document: Block("document") {
    private var documentClass: DocumentClass? = null
    private var usepackage: Usepackage? = null

    override fun toOutputStream(outputStream: PrintWriter, indent: String) {
        documentClass?.toOutputStream(outputStream, indent)
        usepackage?.toOutputStream(outputStream, indent)
        super.toOutputStream(outputStream, indent)
        outputStream.flush()
    }

    fun documentClass(name: String, vararg args: String) {
        documentClass = DocumentClass(name, *args)
    }

    fun usepackage(vararg args: String) {
        usepackage = Usepackage(*args)
    }

    override fun toString(): String {
        val out = ByteArrayOutputStream()
        this.toOutputStream(PrintWriter(out))
        return out.toString()
    }
}

class Itemize: Block("itemize") {
    fun item(init: Item.() -> Unit) = addChild(Item(), init)
}
class Enumerate: Block("enumerate") {
    fun item(init: Item.() -> Unit) = addChild(Item(), init)
}
class Math: Block("math")
class Frame(vararg args: Pair<String,String>): Block("frame", *args)
class FlushLeft: Block("flushleft")
class FlushRight: Block("flushright")
class Center: Block("center")
class Item: Block("item") {
    override fun toOutputStream(outputStream: PrintWriter, indent: String) {
        outputStream.println("$indent\\item")
        children.forEach { it.toOutputStream(outputStream, "$indent ") }
    }
}

open class SingleTag(private val name: String, private vararg val args: String): TexElement() {
    override fun toOutputStream(outputStream: PrintWriter, indent: String) {
        outputStream.println("$indent\\$name{${args.joinToString(", ")}}")
    }
}

class DocumentClass(vararg args: String): SingleTag("documentclass", *args)
class Usepackage(vararg args: String): SingleTag("usepackage", *args);
class FrameTitle(name: String): SingleTag("frametitle", name)

fun document(init: Document.() -> Unit): Document {
    val document = Document()
    document.init()
    return document
}