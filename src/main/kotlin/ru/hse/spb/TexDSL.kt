package ru.hse.spb

import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.lang.RuntimeException

const val INDENT = "  "

@DslMarker
annotation class TexDSL

@TexDSL
abstract class BlockWithOptions(private val name: String,
                                private val options: Map<String, String> = hashMapOf()) : WithChildren {
    override val children: ArrayList<Element> = arrayListOf()

    override fun render(outputStream: PrintWriter, indent: String) {
        outputStream.write("$indent\\begin{$name}")
        outputOptions(outputStream)
        renderChildren(outputStream, indent + INDENT)
        outputStream.append("$indent\\end{$name}\n")
    }

    protected fun outputOptions(outputStream: PrintWriter) {
        if (options.isNotEmpty()) {
            outputStream.write(options.map { "${it.key} = ${it.value}" }.joinToString(", ", "[", "]"))
        }
        outputStream.write("\n")
    }

    fun customTag(name: String,
                  vararg options: Pair<String, String>,
                  init: CustomTag.() -> Unit) = addChild(CustomTag(name, options.toMap()), init)

    fun itemize(init: Itemize.() -> Unit) = addChild(Itemize(), init)

    fun enumerate(init: Enumerate.() -> Unit) = addChild(Enumerate(), init)

    fun math(init: Math.() -> Unit) = addChild(Math(), init)

    fun flushleft(init: FlushLeft.() -> Unit) = addChild(FlushLeft(), init)
    fun flushright(init: FlushRight.() -> Unit) = addChild(FlushRight(), init)
    fun center(init: Center.() -> Unit) = addChild(Center(), init)

    operator fun String.unaryPlus() {
        children.add(TextElement(this.trimMargin()))
    }
}

fun document(init: Document.() -> Unit): Document {
    val doc = Document()
    doc.init()
    return doc
}

class Document : BlockWithOptions("document") {
    private var documentClass: DocumentClass? = null
    private val usePacakages: ArrayList<UsePackage> = arrayListOf()

    fun documentClass(name: String, vararg options: String): DocumentClass {
        documentClass = DocumentClass(name, options.toList())
        return documentClass!!
    }

    fun usepackage(vararg values: String): UsePackage {
        if (values.isEmpty()) {
            throw RuntimeException("usepackage with zero arguments.")
        }

        usePacakages.add(UsePackage(values.asList()))

        return usePacakages.last()
    }

    fun frame(frameTitle: String? = null,
              vararg options: Pair<String, String>,
              init: Frame.() -> Unit) = addChild(Frame(frameTitle, options.toMap()), init)

    override fun render(outputStream: PrintWriter, indent: String) {
        documentClass?.render(outputStream)
        usePacakages.forEach { it.render(outputStream) }

        super.render(outputStream, indent)
    }

    fun toOutputStream(out: OutputStream) {
        val outWriter = PrintWriter(out)

        render(outWriter)
        outWriter.flush()
    }

    override fun toString(): String {
        val out = ByteArrayOutputStream()
        toOutputStream(out)
        return out.toString()
    }
}

abstract class TopLevelDecl(private val name: String,
                            private val values: List<String>,
                            private val options: List<String> = listOf()) : Element {
    override fun render(outputStream: PrintWriter, indent: String) {
        outputStream.write("\\$name")
        if (options.isNotEmpty()) {
            outputStream.write(options.joinToString(", ", "[", "]"))
        }

        outputStream.write("{${values.joinToString(", ")}}\n")
    }
}

class DocumentClass(value: String, options: List<String>) : TopLevelDecl("documentclass", listOf(value), options = options)
class UsePackage(values: List<String>) : TopLevelDecl("usepackage", values)

class TextElement(private val text: String) : Element {
    override fun render(outputStream: PrintWriter, indent: String) {
        outputStream.write("$indent$text\n")
    }
}

class CustomTag(name: String,
                options: Map<String, String> = hashMapOf()) : BlockWithOptions(name, options)

class Frame(private val title: String?  = null,
            options: Map<String, String> = hashMapOf()) : BlockWithOptions("frame", options) {
    override fun render(outputStream: PrintWriter, indent: String) {
        outputStream.write("$indent\\begin{frame}")
        outputOptions(outputStream)

        if (title != null) {
            outputStream.write("${indent + INDENT}\\frametitle{$title}\n")
        }

        renderChildren(outputStream, indent + INDENT)
        outputStream.append("$indent\\end{frame}\n")
    }
}

class Item : BlockWithOptions("item") {
    override fun render(outputStream: PrintWriter, indent: String) {
        outputStream.write("$indent\\item\n")
        renderChildren(outputStream, indent + INDENT)
    }
}

class Itemize(options: Map<String, String> = hashMapOf()) : BlockWithOptions("itemize", options), WithItem
class Enumerate(options: Map<String, String> = hashMapOf()) : BlockWithOptions("enumerate", options), WithItem

class Math(options: Map<String, String> = hashMapOf()) : BlockWithOptions("math", options)

// Alignment
class FlushLeft(options: Map<String, String> = hashMapOf()) : BlockWithOptions("flushleft", options)
class FlushRight(options: Map<String, String> = hashMapOf()) : BlockWithOptions("flushright", options)
class Center(options: Map<String, String> = hashMapOf()) : BlockWithOptions("center", options)