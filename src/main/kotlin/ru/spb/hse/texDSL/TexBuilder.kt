package ru.spb.hse.texDSL

import java.lang.StringBuilder

interface Element {
    fun render(): String
}

abstract class Tag(private val name: String) : Element {
    protected val body = StringBuilder()
    protected val parameters: MutableList<Pair<String, String>> = mutableListOf()

    fun frame(frameTitle: String, vararg parameters: Pair<String, String>, builder: Frame.() -> Unit) {
        body.appendln(Frame(frameTitle, parameters.toList()).apply(builder).render())
    }

    fun itemize(builder: TexList.() -> Unit) {
        body.appendln(TexList(isEnumerated = false).apply(builder).render())
    }

    fun enumerate(builder: TexList.() -> Unit) {
        body.appendln(TexList(isEnumerated = true).apply(builder).render())
    }

    fun math(builder: Math.() -> Unit) {
        body.appendln(Math().apply(builder).render())
    }

    fun alignment(builder: Alignment.() -> Unit) {
        body.appendln(Alignment().apply(builder).render())
    }

    fun customTag(name: String, vararg parameters: Pair<String, String>, builder: CustomTag.() -> Unit) {
        body.appendln(CustomTag(name, parameters.toList()).apply(builder).render())
    }

    operator fun String.unaryPlus() {
        body.appendln(this)
    }

    override fun render(): String {
        val stringBuilder = StringBuilder()
        val parametersString = if (parameters.isEmpty()) {
            ""
        } else {
            "[${parameters.joinToString(separator = ",") { "${it.first}=${it.second}" }}]"
        }
        stringBuilder.appendln("\\begin{$name}$parametersString")
        stringBuilder.append(body)
        stringBuilder.append("\\end{$name}")
        return stringBuilder.toString()
    }
}

class Frame(frameTitle: String, parameters: List<Pair<String, String>>) : Tag("frame") {
    init {
        body.appendln("\\frametitle{$frameTitle}")
        this.parameters.addAll(parameters)
    }
}

@ItemMarker
class TexList(isEnumerated: Boolean) :
    Tag(if (isEnumerated) "enumerate" else "itemize") {

    fun item(builder: Item.() -> Unit) {
        body.append(Item().apply(builder).render())
    }
}

class Math : Tag("math")

class Alignment : Tag("align*")

@DslMarker
annotation class ItemMarker

@ItemMarker
class Item : Element {
    private val body = StringBuilder()

    override fun render(): String {
        if (body.isEmpty()) {
            body.appendln()
        }
        return "\\item $body"
    }

    operator fun String.unaryPlus() {
        body.appendln(this)
    }
}

class CustomTag(name: String, parameters: List<Pair<String, String>>) : Tag(name) {
    init {
        this.parameters.addAll(parameters)
    }
}

class Document : Tag("document") {
    private data class Package(val name: String, val parameters: List<String>) {
        override fun toString(): String {
            if (parameters.isEmpty()) {
                return "{$name}"
            }
            return "[${parameters.joinToString(separator = ",")}]{$name}"
        }
    }

    private var className: String? = null
    private val packages: MutableList<Package> = mutableListOf()

    override fun render(): String {
        val stringBuilder = StringBuilder()
        className?.let { stringBuilder.appendln("\\documentclass{$className}") }
        packages.forEach { stringBuilder.appendln("\\usepackage$it") }
        stringBuilder.append(super.render())
        return stringBuilder.toString()
    }

    fun documentClass(className: String) {
        this.className = className
    }

    fun usePackage(packageName: String, vararg parameters: String) {
        packages.add(Package(packageName, parameters.toList()))
    }
}

fun document(builder: (Document.() -> Unit)) = Document().apply(builder)
