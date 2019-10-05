package ru.hse.spb.texdsl

import java.io.*

class Document : Enviroment( "document", null) {
    private var documentClass: DocumentClass? = null
    private var packages: MutableList<UsePackage> = mutableListOf()

    override fun print(indentLevel: Int, writer: PrintWriter) {
        if (documentClass == null) {
            throw TexException("No document class specified")
        }

        documentClass!!.print(0, writer)

        for (usePackage in packages) {
            usePackage.print(0, writer)
        }

        super.print(0, writer)
    }

    private class DocumentClass(name: String) : CommandWithoutBody("documentclass", arrayOf(name))

    fun usepackage(name: String, vararg optionalParameters: Any) {
        val newPackage = UsePackage(name)
        newPackage.addOptionalParameters(*optionalParameters)
        packages.add(newPackage)
    }

    fun documentClass(className: String, vararg optionalParameters: Any) {
        if (documentClass != null) {
            throw TexException("Where can be only one document class")
        }

        documentClass = DocumentClass(className)
        documentClass!!.addOptionalParameters(*optionalParameters)
    }

    fun toOutputStream(output: OutputStream) {
        val writer = PrintWriter(output)
        print(0, writer)
        writer.flush()
    }

    override fun toString(): String {
        val byteOutputStream = ByteArrayOutputStream()
        toOutputStream(byteOutputStream)

        return byteOutputStream.toString()
    }

    fun toTex(fileName: String) {
        val file = File("$fileName.tex")
        file.createNewFile()
        toOutputStream(FileOutputStream(file))
    }
}

fun document(init: Document.() -> Unit): Document {
    val document = Document()
    document.init()
    return document
}

class UsePackage(name: String): CommandWithoutBody("usepackage", arrayOf(name)) {
    override fun print(indentLevel: Int, writer: PrintWriter) {
        printIndent(indentLevel, writer)
        printText("\\usepackage", writer)
        printOptionalParameters(writer)
        printCompulsoryParameters(writer)
        printNewline(writer)
    }
}