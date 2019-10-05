package ru.hse.spb

interface Element {
    fun render(builder: StringBuilder, indent: String)
}

class TextElement(val text: String) : Element {
    override fun render(builder: StringBuilder, indent: String) {
        builder.appendln("$indent$text")
    }
}

@DslMarker
annotation class TeXTagMarker

@TeXTagMarker
abstract class TeXTag(protected val name: String, protected val value: String = "", vararg attrs: Pair<String, String>) : Element {
    protected val attributes = hashMapOf(*attrs)

    protected open fun <T : Element> initTag(tag: T, init: T.() -> Unit = { }): T {
        tag.init()
        return tag
    }

    override fun render(builder: StringBuilder, indent: String) {
        with(builder) {
            renderNameBegin(indent)
            renderAttributes()
            renderValue()
            appendln()

            renderBody(indent)

            renderNameEnd(indent)
        }
    }

    protected open fun StringBuilder.renderAttributes() {
        if (attributes.isNotEmpty()) {
            append(attributes.entries.joinToString(", ", "[", "]") { (attr, value) ->
                if (value.isBlank()) attr else "$attr=$value"
            })
        }
    }

    protected open fun StringBuilder.renderValue() {
        if (value.isNotEmpty()) append("{$value}")
    }

    protected open fun StringBuilder.renderBody(indent: String) {}

    protected open fun StringBuilder.renderNameBegin(indent: String) {
        append("$indent\\$name")
    }

    protected open fun StringBuilder.renderNameEnd(indent: String) {}
}

abstract class TeXTagNested(name: String, value: String = "", vararg attrs: Pair<String, String>) : TeXTag(name, value, *attrs) {
    protected val children = arrayListOf<Element>()

    override fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        children.add(super.initTag(tag, init))
        return tag
    }

    override fun StringBuilder.renderBody(indent: String) {
        children.forEach { it.render(this, "$indent  ") }
    }

    override fun StringBuilder.renderNameBegin(indent: String) {
        append("$indent\\begin{$name}")
    }

    override fun StringBuilder.renderNameEnd(indent: String) {
        appendln("$indent\\end{$name}")
    }


    fun customTag(name: String, vararg attrs: Pair<String, String>, init: TeXTagNestedWithText.() -> Unit) {
        initTag(object : TeXTagNestedWithText(name) {
            init {
                attrs.forEach { (name, value) -> attributes[name] = value }
            }
        }, init)
    }
}

abstract class TeXTagNestedWithText(name: String, value: String = "", vararg attrs: Pair<String, String>) : TeXTagNested(name, value, *attrs) {
    open operator fun String.unaryPlus() {
        children.add(TextElement(this.trimIndent()))
    }

    fun center() = initTag(Center())
    fun right() = initTag(Right())
    fun left() = initTag(Left())

    fun math(init: Math.() -> Unit) = initTag(Math(), init)

    fun itemize(init: Itemize.() -> Unit) = initTag(Itemize(), init)
    fun enumerate(init: Enumerate.() -> Unit) = initTag(Enumerate(), init)
}

class Center : TeXTag("centering")
class Right : TeXTag("raggedleft")
class Left : TeXTag("raggedright")

class Math : TeXTagNested("equation") {
    operator fun String.unaryPlus() {
        children.add(TextElement(this.trimIndent()))
    }
}

class Item : TeXTagNestedWithText("item") {
    override fun StringBuilder.renderNameBegin(indent: String) {
        append("$indent\\$name")
    }

    override fun StringBuilder.renderNameEnd(indent: String) {}
}

class Itemize : TeXTagNested("itemize") {
    fun item(init: Item.() -> Unit) = initTag(Item(), init)
}

class Enumerate : TeXTagNested("enumerate") {
    fun item(init: Item.() -> Unit) = initTag(Item(), init)
}

class FrameTitle(title: String) : TeXTag("frametitle", title)
class FrameSubtitle(subtitle: String) : TeXTag("framesubtitle", subtitle)
class Frame(frameTitle: String, frameSubtitle: String = "", vararg attrs: Pair<String, String>) : TeXTagNestedWithText("frame", attrs = *attrs) {
    init {
        initTag(FrameTitle(frameTitle))
        if (frameSubtitle.isNotBlank()) initTag(FrameSubtitle(frameSubtitle))
    }
}

class DocumentClass(clas: String, vararg attrs: Pair<String, String>) : TeXTag("documentclass", clas, *attrs)
class UsePackage(pkg: String, vararg attrs: Pair<String, String>) : TeXTag("usepackage", pkg, *attrs)
class Document : TeXTagNestedWithText("document") {
    fun documentClass(tag: String, vararg attrs: Pair<String, String>) = initTag(DocumentClass(tag, *attrs))
    fun usepackage(vararg pkgs: String) = pkgs.forEach { initTag(UsePackage(it)) }

    fun frame(frameTitle: String, frameSubtitle: String = "", vararg attrs: Pair<String, String>, init: Frame.() -> Unit) = initTag(Frame(frameTitle, frameSubtitle, *attrs), init)

    override fun toString(): String {
        val builder = StringBuilder()
        render(builder, "")
        return builder.toString()
    }
}

fun document(init: Document.() -> Unit): String {
    val doc = Document()
    doc.init()
    return doc.toString()
}
