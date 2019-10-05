package tanvd.itmo.kotlin.tex.schema

import tanvd.itmo.kotlin.tex.Text

open class InlineTexCommand(private val name: String,
                            private val params: List<String> = emptyList(),
                            private val arguments: LinkedHashMap<String, String> = LinkedHashMap(),
                            private val inner: MutableList<StringTexElement> = ArrayList()) : TexElement {
    override fun render(appendable: Appendable, indentNum: Int) {
        appendable.append(Text.indent(indentNum))
        appendable.append("\\$name")
        if (params.isNotEmpty()) {
            appendable.append("{${params.joinToString()}}")
        }
        if (arguments.isNotEmpty()) {
            appendable.append("[${arguments.entries.joinToString { "${it.key} = ${it.value}" }}]")
        }
        appendable.append("\n")
        inner.forEach { it.render(appendable, indentNum + Text.indentNum) }
    }

    operator fun String.unaryPlus() {
        inner.add(StringTexElement(this.trimMargin()))
    }
}

fun BlockTexCommand.inline(name: String, params: List<String>) {
    add(InlineTexCommand(name, params))
}

fun BlockTexCommand.inline(name: String, vararg params: String) = inline(name, params.toList())
