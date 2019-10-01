package ru.hse.spb.tex.util

import ru.hse.spb.tex.Elements
import ru.hse.spb.tex.Statements


class Item : CommandWithBody<Statements>("item", Statements())
class ItemGenerator(elements: Elements) : CommandGenerator<Statements, Item>(elements::addReadyElement, { Item() })

