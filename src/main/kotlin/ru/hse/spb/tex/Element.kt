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
