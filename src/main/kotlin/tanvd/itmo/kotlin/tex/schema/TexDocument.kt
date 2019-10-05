package tanvd.itmo.kotlin.tex.schema

class TexDocument(private val preamble: MutableList<TexElement> = ArrayList()) : BlockTexCommand("document") {
    fun preAdd(tag: TexElement) {
        preamble.add(tag)
    }

    override fun render(appendable: Appendable, indentNum: Int) {
        preamble.forEach { it.render(appendable, indentNum) }

        super.render(appendable, indentNum)
    }
}

fun document(body: TexDocument.() -> Unit) = TexDocument().apply(body)
