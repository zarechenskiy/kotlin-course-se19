package tanvd.itmo.kotlin.tex

import tanvd.itmo.kotlin.tex.schema.*


fun TexDocument.documentClass(klass: String) {
    preAdd(InlineTexCommand("documentclass", listOf(klass)))
}

fun TexDocument.usepackage(vararg params: String) {
    preAdd(InlineTexCommand("usepackage", params.toList()))
}

fun BlockTexCommand.frame(frameTitle: String, vararg arguments: Pair<String, String>, body: BlockTexCommand.() -> Unit) {
    val frame = BlockTexCommand("frame", arguments = arguments.toMap(LinkedHashMap()))
    frame.inline("frametitle", frameTitle)
    frame.body()
    add(frame)
}

fun BlockTexCommand.math(body: BlockTexCommand.() -> Unit) {
    val math = BlockTexCommand("math")
    math.body()
    add(math)
}

class EnumerationBlock(name: String) : BlockTexCommand(name)

fun BlockTexCommand.itemize(body: EnumerationBlock.() -> Unit) {
    val itemize = EnumerationBlock("itemize")
    itemize.body()
    add(itemize)
}

fun BlockTexCommand.enumerate(body: EnumerationBlock.() -> Unit) {
    val itemize = EnumerationBlock("enumerate")
    itemize.body()
    add(itemize)
}

class ItemInline : InlineTexCommand("item")

fun EnumerationBlock.item(body: ItemInline.() -> Unit) {
    val item = ItemInline()
    item.body()
    add(item)
}

fun BlockTexCommand.customTag(name: String, vararg arguments: Pair<String, String>, body: BlockTexCommand.() -> Unit) {
    val item = BlockTexCommand(name, arguments = arguments.toMap(LinkedHashMap()))
    item.body()
    add(item)
}
