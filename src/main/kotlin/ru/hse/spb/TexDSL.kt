package ru.hse.spb

@DslMarker
annotation class TeXTagMarker

@TeXTagMarker
interface Element {
    fun render(builder: StringBuilder, ident: String)
}

abstract class Tag(private val name: String,
                   private val option: Pair<String, String>? = null,
                   private val options: List<Pair<String, String> > = emptyList()) : Element {

    val children = arrayListOf<Element>()

    protected fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    override fun render(builder: StringBuilder, ident: String) {
        builder.append("$ident\\begin{$name}${renderAttributes()}\n")
        builder.append(if (option == null) "" else "$ident  ${renderOption()}")
        children.forEach { it.render(builder, "$ident  ") }
        builder.append("$ident\\end{$name}\n")
    }

    private fun renderOption(): String = if (option != null) "\\${option.first}{${option.second}}\n" else ""

    private fun renderAttributes(): String = if (options.isNotEmpty()) options.joinToString(",", "[", "]") { (key, value) -> "$key=$value" } else ""

    operator fun String.unaryPlus() {
        children.add(Text(this))
    }

    fun frame(frameTitle: String? = null, vararg options: Pair<String, String>, init: Frame.() -> Unit) = initTag(Frame(frameTitle, options.toList()), init)

    fun itemize(init: Itemize.() -> Unit) = initTag(Itemize(), init)

    fun enumerate(init: Enumerate.() -> Unit) = initTag(Enumerate(), init)

    fun math(init: Math.() -> Unit) = initTag(Math(), init)

    fun alignment(align: String, init: Alignment.() -> Unit) = initTag(Alignment(align), init)

    fun customTag(name: String, vararg options: Pair<String, String>, init: CustomTag.() -> Unit) = initTag(CustomTag(name, options.toList()), init)

    fun customMacro(name: String, option: String? = null, options: List<String> = emptyList(), init: CustomMacro.() -> Unit) = initTag(CustomMacro(name, option, options), init)
}

abstract class Macro(val name: String, val option: String? = null, val options: List<String> = emptyList()) : Element {
    override fun render(builder: StringBuilder, ident: String) {
        builder.append("$ident\\$name${renderOptions()}${if (option != null) "{$option}" else ""}\n")
    }

    private fun renderOptions(): String = if (options.isEmpty()) "" else options.joinToString(",", "[", "]")
}

class Text(val text: String) : Element {
    override fun render(builder: StringBuilder, ident: String) {
        builder.append("$ident$text\n")
    }
}

class Item : Tag("item") {
    override fun render(builder: StringBuilder, ident: String) {
        builder.append("$ident\\item\n")
        for (c in children) {
            c.render(builder, "$ident  ")
        }
    }
}

class Frame(frameTitle: String?, options: List<Pair<String, String>>) : Tag("frame", if (frameTitle != null) Pair("frametitle", frameTitle) else null, options)

class Itemize : Tag("itemize") {
    fun item(init: Item.() -> Unit) = initTag(Item(), init)
}

class Enumerate : Tag("enumerate") {
    fun item(init: Item.() -> Unit) = initTag(Item(), init)
}

class Alignment(align: String) : Tag(align)

class CustomTag(name: String, options: List<Pair<String, String>>) : Tag(name, options=options)

class CustomMacro(name: String, option: String?, options: List<String>): Macro(name, option, options)

class DocumentClass(documentClass: String, options: List<String>) : Macro("documentclass", documentClass, options)

class Usepackage(package_: String, options: List<String>) : Macro("usepackage", package_, options)

class Math : Element {
    private val children = arrayListOf<Text>()

    operator fun String.unaryPlus() {
        children.add(Text(this))
    }

    override fun render(builder: StringBuilder, ident: String) {
        for (text in children) {
            builder.append("$ident$${text.text}$\\\\\n")
        }
    }

}

class Document : Tag("document") {

    private val preamble = arrayListOf<Element>()

    override fun render(builder: StringBuilder, ident: String) {
        preamble.forEach { it.render(builder, ident) }
        builder.append("$ident\\begin{document}\n")
        children.forEach { it.render(builder, "$ident  ") }
        builder.append("$ident\\end{document}\n")
    }

    override fun toString(): String {
        val builder = java.lang.StringBuilder()
        render(builder, "")
        return builder.toString()
    }

    private fun <T : Macro> initPreambleTag(tag: T): T {
        preamble.add(tag)
        return tag
    }

    fun documentclass(documentClass: String, options: List<String> = emptyList()) = initPreambleTag(DocumentClass(documentClass, options))

    fun usepackage(package_: String, vararg options: String) = initPreambleTag(Usepackage(package_, options.toList()))
}

fun document(init: Document.() -> Unit) : Document {
    val document = Document()
    document.init()
    return document
}