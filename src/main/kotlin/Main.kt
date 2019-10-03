package ru.hse.spb

import java.io.OutputStream

interface Element {
    fun toOutputStream(stream: OutputStream, indent: String)
}

abstract class Tag(
    private val tagName: String,
    private val arg: String
): Element {

    override fun toOutputStream(stream: OutputStream, indent: String) {
        stream.write("$indent\\$tagName{$arg}${System.lineSeparator()}".toByteArray())
    }
}

abstract class TagWithBlock(
    private val tagName: String,
    protected val children: MutableList<Element> = ArrayList()
): Element {

    override fun toOutputStream(stream: OutputStream, indent: String) {
        stream.write("$indent\\begin{$tagName}${System.lineSeparator()}".toByteArray())
        for (child in children) {
            child.toOutputStream(stream, "$indent    ")
        }
        stream.write("$indent\\end{$tagName}${System.lineSeparator()}".toByteArray())
    }
}

class DocumentClass(docClass: String): Tag("documentclass", docClass)

class Document: TagWithBlock("document") {

    private var docClass: DocumentClass? = null

    fun documentClass(docClass: String): DocumentClass {
        val documentClass = DocumentClass(docClass)
        check(this.docClass == null) { "Can not call documentClass more than one time" }
        this.docClass = documentClass
        this.children.add(this.docClass!!)
        return documentClass
    }

    fun toOutputStream(stream: OutputStream) {
        super.toOutputStream(stream, "")
    }
}

fun document(init: Document.() -> Unit): Document {
    val document = Document()
    document.init()
    return document
}

fun main() {
    document {
        documentClass("beamer")
    }.toOutputStream(System.out)
}
