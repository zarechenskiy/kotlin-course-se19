package ru.hse.lyubortk.kotlin

import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.util.stream.Collectors.joining

private const val BLOCK_INDENT = "    "
private const val DOCUMENT_ENVIRONMENT = "document"
private const val ITEMIZE_ENVIRONMENT = "itemize"
private const val ENUMERATE_ENVIRONMENT = "enumerate"
private const val DOCUMENT_CLASS_COMMAND = "documentclass"
private const val USEPACKAGE_COMMAND = "usepackage"
private const val MATH_ENVIRONMENT = "gather*"
private const val ITEM_COMMAND = "item"
private const val FRAME_ENVIRONMENT = "frame"
private const val FLUSHLEFT_ENVIRONMENT = "flushleft"
private const val FLUSHRIGHT_ENVIRONMENT = "flushright"
private const val CENTER_ENVIRONMENT = "center"
private const val FRAMETITLE_COMMAND = "frametitle"
private const val BEGIN_COMMAND = "begin"
private const val END_COMMAND = "end"

@DslMarker
annotation class TexDslMarker

@TexDslMarker
interface Element {
    fun render(out: PrintWriter, indent: String)
}

class TextElement(private val text: String) : Element {
    override fun render(out: PrintWriter, indent: String) {
        out.println("$indent$text")
    }
}

abstract class Command(private val commandName: String,
                       mandatoryArguments: List<String> = listOf(),
                       optionalArguments: List<Pair<String, String>> = listOf()) : Element {
    protected val mandatoryArgumentsText =
        if (mandatoryArguments.isNotEmpty()) {
            mandatoryArguments.joinToString(", ", "{", "}")
        } else {
            ""
        }

    private val optionalArgumentsText: String =
        if (optionalArguments.isNotEmpty()) {
            optionalArguments.stream().map {
                "${it.first}=${it.second}"
            }.collect(joining(", ", "[", "]"))
        } else {
            ""
        }

    override fun render(out: PrintWriter, indent: String) {
        out.println("$indent\\$commandName$mandatoryArgumentsText$optionalArgumentsText")
    }
}

abstract class CommandWithBody(commandName: String,
                               mandatoryArguments: List<String> = listOf(),
                               optionalArguments: List<Pair<String, String>> = listOf(),
                               private val closeCommand: String? = null)
    : Command(commandName, mandatoryArguments, optionalArguments) {

    protected val children = mutableListOf<Element>()

    override fun render(out: PrintWriter, indent: String) {
        super.render(out, indent)
        for (child in children) {
            child.render(out, "$indent$BLOCK_INDENT")
        }
        closeCommand?.let { out.println("$indent\\$closeCommand$mandatoryArgumentsText") }
    }

    fun frame(vararg options: Pair<String, String>, frameTitle: String? = null, init: Frame.() -> Unit): Frame {
        val frame = Frame(*options)
        frameTitle?.let { frame.children.add(FrameTitle(frameTitle)) }
        frame.init()
        children.add(frame)
        return frame
    }

    fun itemize(vararg options: Pair<String, String>, init: Itemize.() -> Unit) = initChild(Itemize(*options), init)

    fun math(init: Math.() -> Unit) = initChild(Math(), init)

    fun flushleft(init: FlushLeft.() -> Unit) = initChild(FlushLeft(), init)

    fun flushright(init: FlushRight.() -> Unit) = initChild(FlushRight(), init)

    fun center(init: Center.() -> Unit) = initChild(Center(), init)

    fun enumerate(vararg options: Pair<String, String>, init: Enumerate.() -> Unit)
            = initChild(Enumerate(*options), init)

    fun customTag(name: String, vararg options: Pair<String, String>, init: CustomTag.() -> Unit)
            = initChild(CustomTag(name, *options), init)

    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }

    protected fun <T : Element> initChild(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }
}

abstract class Environment(name: String, vararg options: Pair<String, String>)
    : CommandWithBody(BEGIN_COMMAND, listOf(name), options.asList(), END_COMMAND)

class Document : Environment(DOCUMENT_ENVIRONMENT) {
    private var texDocumentClass: DocumentClass? = null
    private val packages = arrayListOf<UsePackage>()

    override fun render(out: PrintWriter, indent: String) {
        texDocumentClass?.render(out, indent)
        for (texPackage in packages) {
            texPackage.render(out, indent)
        }
        super.render(out, indent)
    }

    fun documentClass(name: String) {
        texDocumentClass?.let { throw RuntimeException("Document class was already specified") }
        texDocumentClass = DocumentClass(name)
    }

    fun usepackage(firstName: String, vararg otherNames: String) {
        packages.add(UsePackage(firstName, *otherNames))
    }

    fun toOutputStream(out: OutputStream) {
        val writer = PrintWriter(out)
        render(writer, "")
        writer.flush()
    }

    override fun toString(): String {
        val stream = ByteArrayOutputStream()
        stream.use {
            toOutputStream(it)
        }
        return stream.toString()
    }

    fun checkHasDocumentClassSpecified() {
        texDocumentClass ?: throw RuntimeException("Document class was not specified")
    }
}

class Itemize(vararg options: Pair<String, String>) : Environment(ITEMIZE_ENVIRONMENT, *options) {
    fun item(init: Item.() -> Unit) = initChild(Item(), init)
}

class Enumerate(vararg options: Pair<String, String>) : Environment(ENUMERATE_ENVIRONMENT, *options) {
    fun item(init: Item.() -> Unit) = initChild(Item(), init)
}

class DocumentClass(name: String) : Command(DOCUMENT_CLASS_COMMAND, listOf(name))
class UsePackage(vararg names: String) : Command(USEPACKAGE_COMMAND, names.asList())
class Math : Environment(MATH_ENVIRONMENT)
class Item : CommandWithBody(ITEM_COMMAND)
class Frame(vararg options: Pair<String, String>) : Environment(FRAME_ENVIRONMENT, *options)
class FlushLeft : Environment(FLUSHLEFT_ENVIRONMENT)
class FlushRight : Environment(FLUSHRIGHT_ENVIRONMENT)
class Center : Environment(CENTER_ENVIRONMENT)
class FrameTitle(title: String) : CommandWithBody(FRAMETITLE_COMMAND, listOf(title))
class CustomTag(name: String, vararg options: Pair<String, String>) : Environment(name, *options)

fun document(init: Document.() -> Unit): Document {
    val document = Document()
    document.init()
    document.checkHasDocumentClassSpecified()
    return document
}