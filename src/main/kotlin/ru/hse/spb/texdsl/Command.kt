package ru.hse.spb.texdsl

import java.io.PrintWriter

/**
 * in \usepackage{packageName}, usepackage - name, packageName - secondary name.
 */
fun printName(element: ElementWithParameters, name: String?, secondaryName: String? = null, writer: PrintWriter) {
    element.printText("\\", writer)
    if (name != null) {
        element.printText(name, writer)
    }
    if (secondaryName != null) {
        element.printText("{$secondaryName}", writer)
    }
}

/**
 * single-line command in latex: \usepackage[arg=val]{packageName}
 *
 * Command can have compulsory arguments (one that has to be arguments of the command) in {} brackets and
 * optional arguments in [] bracket
 *
 * Arguments can be printed as an Array of Pair<String, String> (property - value) or just as an Array of String (only values).
 */
open class CommandWithoutBody(private val name : String?, compulsoryArguments: Array<out Any>? = null, private val secondaryName: String? = null) :
    ElementWithParameters(compulsoryArguments) {

    override fun print(indentLevel: Int, writer: PrintWriter) {
        printIndent(indentLevel, writer)
        printName(this, name, secondaryName, writer)
        printParameters(writer)
        printNewline(writer)
    }
}

/**
 * Same command, but can have body (anything inside): \enumerate{\item \enumerate{\item}}
 */
open class CommandWithBody(
    private val name: String?,
    compulsoryArguments: Array<out Any>? = null,
    private val secondaryName: String? = null
) : ElementWithBody(compulsoryArguments) {
    override fun print(indentLevel: Int, writer: PrintWriter) {
        printIndent(indentLevel, writer)
        printName( this, name, secondaryName, writer)
        printParameters(writer)
        printBody(indentLevel, writer)
    }
}