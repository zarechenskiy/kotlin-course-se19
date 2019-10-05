package tanvd.itmo.kotlin.tex.schema

import tanvd.itmo.kotlin.tex.Text

class StringTexElement(private val text: String) : TexElement {
    override fun render(appendable: Appendable, indentNum: Int) {
        for (line in text.split("\n")) {
            appendable.append(Text.indent(indentNum))
            appendable.append(line)
            appendable.append("\n")
        }
    }
}
