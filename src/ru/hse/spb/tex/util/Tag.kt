package ru.hse.spb.tex.util

import ru.hse.spb.tex.Elements
import ru.hse.spb.tex.Statements
import java.io.Writer

open class Tag(private val name: String) : Statements() {
    override fun render(output: Writer, indent: String) {
        output.appendln("$indent\\begin{$name}")
        super.render(output, indent + "\t")
        output.appendln("$indent\\end{$name}")
    }
}

open class ItemTag(private val name: String) : Elements() {
    val item = ItemGenerator(this)

    override fun render(output: Writer, indent: String) {
        output.appendln("$indent\\begin{$name}")
        super.render(output, indent + "\t")
        output.appendln("$indent\\end{$name}")
    }
}