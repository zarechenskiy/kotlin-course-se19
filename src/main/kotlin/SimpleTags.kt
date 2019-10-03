package ru.hse.spb

import java.io.OutputStream

class DocumentClass(docClass: String): Tag("documentclass", docClass)
class Package(name: String): Tag("usepackage", name)
class FrameTitle(title: String): Tag("frametitle", title)

class Math(formula: String): Tag("math", formula) {

    override fun toOutputStream(stream: OutputStream, indent: String) {
        stream.write("$indent\$$arg\$${System.lineSeparator()}".toByteArray())
    }
}
