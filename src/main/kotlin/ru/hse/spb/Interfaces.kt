package ru.hse.spb

import java.io.PrintWriter

interface Element {
    fun render(outputStream: PrintWriter, indent: String = "")
}

interface WithChildren : Element {
    val children: ArrayList<Element>

    fun <T : Element> addChild(block: T, init: T.() -> Unit): T {
        block.init()
        children.add(block)
        return block
    }

    fun renderChildren(outputStream: PrintWriter, indent: String = "") {
        children.forEach { it.render(outputStream, indent) }
    }
}

interface WithItem : WithChildren {
    fun item(init: Item.() -> Unit) = addChild(Item(), init)
}