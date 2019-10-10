package ru.hse.spb

import java.io.OutputStream

@DslMarker
annotation class TexDSL

@TexDSL
abstract class Tex(private val builder: (String) -> Unit) {
    fun add(element: String) {
        builder(element)
    }
}

abstract class TexBody(private val builder: (String) -> Unit) : Tex(builder) {
    // There's no clear way to detect when a body of the document is started
    // and not to store the whole tree at the same time.
    // So this flag detects when the first `body` command
    private var isPreamble = true

    fun check() {
        if (!isPreamble)
            builder("\\end{document}")
    }

    operator fun String.unaryPlus() = add(this.trimMargin())

    fun frame(frameTitle: String, vararg params: Pair<String, String>, init: TexBody.() -> Unit) = element {
        val tag = CustomTag("frame", params)
        add(tag.begin())
        add("\\frametitle{$frameTitle}")
        init()
        add(tag.end())
    }

    fun customTag(name: String, vararg params: Pair<String, String>, init: TexBody.() -> Unit) = element {
        val tag = CustomTag(name, params)
        add(tag.begin())
        init()
        add(tag.end())
    }

    fun center(init: TexBody.() -> Unit) = element {
        val tag = CustomTag("center", emptyArray())
        add(tag.begin())
        init()
        add(tag.end())
    }

    fun flushright(init: TexBody.() -> Unit) = element {
        val tag = CustomTag("flushright", emptyArray())
        add(tag.begin())
        init()
        add(tag.end())
    }

    fun math(str: String) = element {
        add("\\begin{math}")
        add(str)
        add("\\end{math}")
    }

    fun itemize(init: TexList.() -> Unit) = element {
        val tl = TexList(builder)
        add("\\begin{itemize}")
        tl.init()
        add("\\end{itemize}")
    }

    fun enumerate(init: TexList.() -> Unit) = element {
        val tl = TexList(builder)
        add("\\begin{enumerate}")
        tl.init()
        add("\\end{enumerate}")
    }

    private fun element(func: TexBody.() -> Unit) {
        if (isPreamble) {
            isPreamble = false
            add("\\begin{document}")
        }
        func()
    }

}

class TexDocument(init: TexDocument.() -> Unit,
                  private val builder: (String) -> Unit) : TexBody(builder) {
    init {
        init()
        // Just to add `\end{document}` if necessary.
        check()
    }

    fun documentClass(cls: String) = add(Class(cls, builder).toString())
    fun usePackage(vararg packages: String) = add(UsePackage(packages, builder).toString())
}

class Class(private val cls: String,
            builder: (String) -> Unit) : Tex(builder) {
    override fun toString(): String = "\\documentclass{$cls}"
}

class UsePackage(private val packages: Array<out String>,
                 builder: (String) -> Unit) : Tex(builder) {
    override fun toString(): String = packages.joinToString("\n") { pkg -> "\\usepackage{$pkg}" }
}

class CustomTag(private val name: String,
                private val params: Array<out Pair<String, String>>) {

    fun begin(): String = "\\begin{$name}${renderParams(params)}"

    fun end(): String = "\\end{$name}"

    private fun renderParams(params: Array<out Pair<String, String>>): String {
        if (params.isEmpty())
            return ""
        return params.joinToString(",", "[", "]") { (param, value) -> "$param=$value" }
    }
}

class TexList(builder: (String) -> Unit) : TexBody(builder) {
    fun item(init: TexList.() -> Unit) {
        add("\\item")
        init()
    }
}

class TexOutput(private val init: TexDocument.() -> Unit) {
    fun toOutputStream(out: OutputStream) {
        TexDocument(init) { out.write(it.toByteArray()) }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        TexDocument(init) { builder.appendln(it) }
        return builder.toString().trimIndent()
    }
}

fun document(init: TexDocument.() -> Unit) = TexOutput(init)
