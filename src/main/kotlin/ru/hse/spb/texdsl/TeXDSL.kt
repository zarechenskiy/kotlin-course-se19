package ru.hse.spb.texdsl

import java.io.StringWriter
import java.io.Writer

interface Element {
    fun render(out: Writer, indent: String = "")
}

class TextElement(private val text: String) : Element {
    override fun render(out: Writer, indent: String) {
        out.write("$indent$text\n")
    }
}

@DslMarker
annotation class TeXElementMarker

@TeXElementMarker
abstract class Block(private val name: String,
                     private val isStatement: Boolean,
                     private vararg val parameters: Pair<String, String>) : Element {
    val children = arrayListOf<Element>()

    protected fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    override fun render(out: Writer, indent: String) {
        if (isStatement) {
            out.write("$indent\\$name\n")
        } else {
            val parametersString =
                if (parameters.isNotEmpty()) parameters.joinToString(prefix = "[", postfix = "]")
                                                        { "${it.first}=${it.second}" }
                else ""
            out.write("$indent\\begin{$name}$parametersString\n")
        }
        for (c in children) {
            c.render(out, "$indent  ")
        }
        if (!isStatement) {
            out.write("$indent\\end{$name}\n")
        }
        out.flush()
    }
}

abstract class BlockWithText(name: String,
                             isStatement: Boolean,
                             vararg parameters: Pair<String, String>) : Block(name, isStatement, *parameters) {
    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }
}

abstract class BlockWithData(name: String,
                             isStatement: Boolean,
                             vararg parameters: Pair<String, String>) : BlockWithText(name, isStatement, *parameters) {
    fun math(init: Math.() -> Unit) = initTag(Math(), init)
    fun enumerate(init: Enumerate.() -> Unit) = initTag(Enumerate(), init)
    fun itemize(init: Itemize.() -> Unit) = initTag(Itemize(), init)
    fun customTag(name: String, vararg parameters: Pair<String, String>, init: CustomBlock.() -> Unit) =
        initTag(CustomBlock(name, *parameters), init)
    fun flushLeft(init: LeftAllignment.() -> Unit) = initTag(LeftAllignment(), init)
    fun flushRight(init: RightAllignment.() -> Unit) = initTag(RightAllignment(), init)
    fun center(init: CenterAllignment.() -> Unit) = initTag(CenterAllignment(), init)
}

class Document : BlockWithData("document", false) {
    private var documentClass: String? = null
    private val packages = linkedMapOf<String, Array<out String>>()

    fun frame(name: String = "",
              vararg parameters: Pair<String, String>,
              init: Frame.() -> Unit) = initTag(Frame(name, *parameters), init)

    fun documentClass(type: String) {
        require(documentClass == null)
        documentClass = type
    }

    fun usePackage(packageName: String, vararg parameters: String) {
        packages[packageName] = parameters
    }

    override fun render(out: Writer, indent: String) {
        require(documentClass != null)
        Statement("documentclass", documentClass!!).render(out, indent)
        for ((name, parameters) in packages) {
            out.write("$indent\\usepackage")
            if (parameters.isNotEmpty()) {
                out.write(parameters.joinToString(prefix = "[", postfix = "]", separator = ","))
            }
            out.write("{$name}\n")
        }
        out.write("\n\n")
        out.flush()

        super.render(out, indent)
    }

    override fun toString(): String {
        val stringWriter = StringWriter()
        render(stringWriter)
        return stringWriter.toString()
    }

}

class Statement(private val name: String, private vararg val parameters: String) : Element {
    override fun render(out: Writer, indent: String) {
        out.write("$indent\\$name${parameters.joinToString(prefix = "{", postfix = "}")}\n")
    }

}

class Frame(name: String, vararg parameters: Pair<String, String>)
    : BlockWithData("frame", false, *parameters) {
    init {
        initTag(Statement("frametitle", name), {})
    }
}

class Math : BlockWithText("math", false)

class LeftAllignment : BlockWithData("flushleft", false)

class RightAllignment : BlockWithData("flushright", false)

class CenterAllignment : BlockWithData("center", false)


class Enumerate : Block("enumerate", false) {
    fun item(init: Item.() -> Unit) = initTag(Item(), init)
}

class Itemize : Block("itemize", false) {
    fun item(init: Item.() -> Unit) = initTag(Item(), init)
}

class Item : BlockWithData("item", true)

class CustomBlock(name: String, vararg parameters: Pair<String, String>)
    : BlockWithData(name, false, *parameters)

fun document(init: Document.() -> Unit): Document {
    val document = Document()
    document.init()
    return document
}



