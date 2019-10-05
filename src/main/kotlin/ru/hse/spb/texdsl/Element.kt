package ru.hse.spb.texdsl

import java.io.PrintWriter
import java.lang.RuntimeException

@DslMarker
annotation class TexDslMarker

@TexDslMarker
abstract class Element {
    private val indent = "    "

    protected fun printIndent(indentLevel: Int, writer: PrintWriter) {
        for (i in 0 until indentLevel) {
            writer.print(indent)
        }
    }

    internal fun printText(text: String, indentLevel: Int, writer: PrintWriter) {
        printIndent(indentLevel, writer)
        writer.print(text)
    }

    internal fun printText(text: String, writer: PrintWriter) {
        writer.print(text)
    }

    internal fun printNewline(writer: PrintWriter) {
        writer.print("\n")
    }

    /**
     * Prints LaTeX representation of the given element.
     */
    internal abstract fun print(indentLevel: Int, writer: PrintWriter)
}

class TextElement(private val text: String) : Element() {
    override fun print(indentLevel: Int, writer: PrintWriter) {
        printIndent(indentLevel, writer)
        printText(text, writer)
        printNewline(writer)
    }
}

class TexException(text: String) : RuntimeException(text)