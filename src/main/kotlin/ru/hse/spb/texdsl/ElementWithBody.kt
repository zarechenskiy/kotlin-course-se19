package ru.hse.spb.texdsl

import java.io.PrintWriter

abstract class ElementWithBody(compulsoryParameters: Array<out Any>? = null) :
    ElementWithParameters(compulsoryParameters) {
    private var children: MutableList<Element>? = null

    fun printBody(indentLevel: Int, writer: PrintWriter) {
        printNewline(writer)
        if (children != null) {
            for (child in children!!) {
                child.print(indentLevel + 1, writer)
            }
        }
    }

    fun addChild(element: Element) {
        if (children == null) {
            children = mutableListOf()
        }

        children!!.add(element)
    }

    fun frame(vararg compulsoryParameters: Any, init: Frame.() -> Unit): Frame {
        return initChild(Frame(compulsoryParameters), init)
    }

    fun frame(frameTitle: String, vararg optionalParameters: Any, init: Frame.() -> Unit): Frame {
        val f = initChild(Frame(arrayOf(frameTitle)), init)
        f.addOptionalParameters(*optionalParameters)
        return f
    }

    fun customTag(name: String, vararg compulsoryArguments: Any, init: CustomTag.() -> Unit): CustomTag {
        return initChild(CustomTag(name, compulsoryArguments), init)
    }

    fun itemize(vararg compulsoryArguments: Any, init: Itemized.() -> Unit): Itemized {
        return initChild(Itemized(compulsoryArguments), init)
    }

    fun math(vararg compulsoryArguments: Any, init: Math.() -> Unit): Math {
        return initChild(Math(compulsoryArguments), init)
    }

    fun enumerate(vararg compulsoryArguments: Any, init: Enumerate.() -> Unit): Enumerate {
        return initChild(Enumerate(compulsoryArguments), init)
    }

    fun flushleft(vararg compulsoryArguments: Any, init: FlushLeft.() -> Unit): FlushLeft {
        return initChild(FlushLeft(compulsoryArguments), init)
    }

    fun flushright(vararg compulsoryArguments: Any, init: FlushRight.() -> Unit): FlushRight {
        return initChild(FlushRight(compulsoryArguments), init)
    }

    fun center(vararg compulsoryArguments: Any, init: Center.() -> Unit): Center {
        return initChild(Center(compulsoryArguments), init)
    }

    operator fun String.unaryPlus() {
        addChild(TextElement(this))
    }

    private fun <T : Element> initChild(element: T, init: T.() -> Unit): T {
        element.init()
        addChild(element)
        return element
    }
}

open class Enviroment(private val name: String, compulsoryParameters: Array<out Any>? = null): CommandWithBody("begin", compulsoryParameters, name) {
    override fun print(indentLevel: Int, writer: PrintWriter) {
        super.print(indentLevel, writer)

        printText("\\end{${name}}", indentLevel, writer)
        printNewline(writer)
    }
}

class Math(compulsoryParameters: Array<out Any>?) : Enviroment("displaymath", compulsoryParameters)

class Enumerate(compulsoryParameters: Array<out Any>?) : ItemizedEnviroment("enumerate", compulsoryParameters)

class Frame(compulsoryParameters: Array<out Any>?) : Enviroment("frame", compulsoryParameters)

class CustomTag(name: String, compulsoryParameters: Array<out Any>?) : Enviroment(name, compulsoryParameters)

class Itemized(compulsoryArguments: Array<out Any>?): ItemizedEnviroment("itemize", compulsoryArguments)

open class ItemizedEnviroment(name: String, compulsoryArguments: Array<out Any>?): Enviroment(name, compulsoryArguments) {
    fun item(vararg compulsoryArguments: Any, init: Item.() -> Unit): Item {
        val item = Item(compulsoryArguments)
        item.init()
        addChild(item)
        return item
    }
}

class FlushLeft(compulsoryArguments: Array<out Any>?): Enviroment("flushleft", compulsoryArguments)
class FlushRight(compulsoryArguments: Array<out Any>?): Enviroment("flushright", compulsoryArguments)
class Center(compulsoryArguments: Array<out Any>?): Enviroment("center", compulsoryArguments)


class Item(compulsoryArguments: Array<out Any>?): CommandWithBody("item", compulsoryArguments)