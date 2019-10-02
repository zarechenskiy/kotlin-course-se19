package ru.hse.spb

import java.io.PrintStream

@DslMarker
annotation class TexItemMarker

fun document(init: Document.() -> Unit): Document {
    val doc = Document()
    doc.init()
    return doc
}

interface Printable {
    fun toTex(): String
}

abstract class ElementWithChildren<T : Printable>() : Printable {
    val children = mutableListOf<T>()

    override fun toTex(): String {
        return listToTex(children)
    }

    fun listToTex(list: List<Printable>): String {
        val result = StringBuilder()
        for (child in list) {
            result.append(child.toTex() + "\n")
        }
        return result.toString()
    }

    fun <U : T> initChild(element: U, init: U.() -> Unit): U {
        element.init()
        children.add(element)
        return element
    }
}

abstract class ElementWithTextChildren() : ElementWithChildren<Printable>() {
    fun enumerate(init: Enumerate.() -> Unit) = initChild(Enumerate(), init)
    fun itemize(init: Itemize.() -> Unit) = initChild(Itemize(), init)
    fun math(init: CustomTag.() -> Unit) = initChild(CustomTag("math", emptyList()), init)
    fun flushRight(init: CustomTag.() -> Unit) = initChild(CustomTag("flushright", emptyList()), init)
    fun center(init: CustomTag.() -> Unit) = initChild(CustomTag("center", emptyList()), init)
    fun flushLeft(init: CustomTag.() -> Unit) = initChild(CustomTag("flushleft", emptyList()), init)
    fun customTag(name: String,
                  vararg args: Pair<String, String>,
                  init: CustomTag.() -> Unit) = initChild(CustomTag(name, args.toList()), init)

    operator fun String.unaryPlus() {
        children.add(SimpleText(this))
    }
}

class Document() : ElementWithTextChildren() {
    val beforeDoc = mutableListOf<Printable>()

    override fun toTex(): String {
        val result = super.toTex()
        return """
            ${listToTex(beforeDoc)}
            \begin{document}
            $result
            \end{document}
        """.trimIndent()
    }

    fun usepackage(vararg args: String) {
        beforeDoc.add(UsePackage(args.toList()))
    }

    fun documentClass(vararg args: String) {
        beforeDoc.add(DocumentClass(args.toList()))
    }

    override fun toString(): String {
        return toTex()
    }

    fun toOutputStream(stream: PrintStream) {
        stream.println(toTex())
    }

    fun frame(frameTitle: String = "",
              vararg args: Pair<String, String>,
              init: Frame.() -> Unit) = initChild(Frame(frameTitle, args.toList()), init)
}

open class ElementsList(val name: String) : ElementWithChildren<Item>() {
    override fun toTex(): String {
        val inner = super.toTex()
        return """
            \begin{$name}
            $inner
            \end{$name}
        """.trimIndent()
    }

    fun item(init: Item.() -> Unit) = initChild(Item(), init)
}

class Itemize() : ElementsList("itemize")
class Enumerate() : ElementsList("enumerate")

@TexItemMarker
class Item() : ElementWithTextChildren() {
    override fun toTex(): String {
        return "\\item\n" + super.toTex()
    }
}

open class Include(val name: String, args: List<String>) : Printable {
    val others = args.drop(1)
    val first = args[0]

    override fun toTex(): String {
        val additions = if (others.isEmpty()) "" else others.joinToString(", ", prefix = "[", postfix = "]")
        return "\\$name$additions{$first}"
    }
}

class UsePackage(args: List<String>) : Include("usepackage", args)
class DocumentClass(args: List<String>) : Include("documentclass", args)

class Frame(val titile: String, args: List<Pair<String, String>>) : ElementWithTextChildren() {
    val args = if (args.isEmpty()) ""
    else args.joinToString(separator = ", ", prefix = "[", postfix = "]") { it.first + "=" + it.second }

    override fun toTex(): String {
        return """
            \begin{frame}$args{$titile}
                ${super.toTex()}
            \end{frame}
        """.trimIndent()
    }
}

class CustomTag(val name: String, args: List<Pair<String, String>>) : ElementWithTextChildren() {
    val args = if (args.isEmpty())
        ""
    else
        args.joinToString(separator = ", ", prefix = "[", postfix = "]") { it.first + "=" + it.second }

    override fun toTex(): String {
        return """
            \begin{$name}$args
            ${super.toTex()}
            \end{$name}
        """.trimIndent()
    }

}

class SimpleText(val text: String) : Printable {
    override fun toTex(): String {
        return text.trimMargin()
    }
}