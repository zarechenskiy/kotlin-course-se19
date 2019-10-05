package tanvd.itmo.kotlin.tex.schema

import tanvd.itmo.kotlin.tex.Text

open class BlockTexCommand(private val name: String,
                           private val arguments: LinkedHashMap<String, String> = LinkedHashMap(),
                           private val inner: MutableList<TexElement> = ArrayList()) : TexElement {

    override fun render(appendable: Appendable, indentNum: Int) {
        InlineTexCommand("begin", listOf(name), arguments).render(appendable, indentNum)
        inner.forEach { it.render(appendable, indentNum + Text.indentNum) }
        InlineTexCommand("end", listOf(name)).render(appendable, indentNum)
    }

    fun add(element: TexElement) {
        inner.add(element)
    }

    operator fun String.unaryPlus() {
        inner.add(StringTexElement(this.trimMargin()))
    }
}
