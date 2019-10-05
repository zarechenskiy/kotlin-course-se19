package tanvd.itmo.kotlin.tex.schema

import java.io.OutputStream

@DslMarker
annotation class TexTag

@TexTag
interface TexElement {
    fun render(appendable: Appendable, indentNum: Int = 0)
    fun render() = StringBuilder().apply { render(this) }.toString()
    fun toOutputStream(stream: OutputStream) = render(stream.writer())
}
