package ru.hse.spb.tex

import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.StringWriter
import java.io.Writer


interface Element {
    fun render(output: Writer, indent: String)


    fun toWriter(output: Writer) {
        render(output, "")
    }

    fun toOutputStream(output: OutputStream) {
        OutputStreamWriter(output).use { toWriter(it) }
    }

    val stringRepresentation
        get() = StringWriter().also {
            toWriter(it)
        }.toString()
}

class TextStatement(private val text: String) : Element {
    override fun render(output: Writer, indent: String) {
        output.appendln(indent + text)
    }
}

@DslMarker
annotation class TeXMarker

@TeXMarker
open class Elements : Element {
    protected val elements = arrayListOf<Element>()

    fun <T : Element> addElement(element: T, init: T.() -> Unit = {}): T {
        element.init()
        elements.add(element)
        return element
    }

    // to use as lambda
//    fun <T : Element> addElement(element: T): T {
//        elements.add(element)
//        return element
//    }

    override fun render(output: Writer, indent: String) {
        for (statement in elements) {
            statement.render(output, indent)
        }
    }
}
