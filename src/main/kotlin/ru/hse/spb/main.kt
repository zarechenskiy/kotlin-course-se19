package ru.hse.spb

import kotlin.random.Random

private fun read() = readLine()!!
private fun readInt() = read().toInt()
private fun readStrings() = read().split(' ')

data class TreapNode(val value: Int, val priority: Int = Random.nextInt()) {
    var left: TreapNode? = null
    var right: TreapNode? = null
    var size: Int = 1

    fun update(): TreapNode {
        size = 1 + left.size + right.size
        return this
    }
}

val TreapNode?.size: Int
    get() = this?.size ?: 0

fun TreapNode?.split(position: Int): Pair<TreapNode?, TreapNode?> = when {
    this == null -> null to null
    left.size < position -> {
        val (less, greater) = right.split(position - left.size - 1)
        right = less
        this.update() to greater
    }
    else -> {
        val (less, greater) = left.split(position)
        left = greater
        less to this.update()
    }
}

fun merge(node1: TreapNode?, node2: TreapNode?): TreapNode? = when {
    node1 == null || node2 == null -> node1 ?: node2
    node1.priority > node2.priority -> {
        node1.right = merge(node1.right, node2)
        node1.update()
    }
    else -> {
        node2.left = merge(node1, node2.left)
        node2.update()
    }
}

class Treap {
    private var root: TreapNode? = null

    fun insert(value: Int) {
        root = merge(root, TreapNode(value))
    }

    fun remove(position: Int): Int? {
        val (left, other) = root.split(position)
        val (removed, right) = other.split(position - left.size + 1)
        root = merge(left, right)
        return removed?.value
    }
}

fun main() {
    val k = readInt()
    val s = read()
    val n = readInt()

    val lettersWithPositions = HashMap<Char, Treap>()
    s.repeat(k).asSequence().forEachIndexed { index, char ->
        lettersWithPositions.getOrPut(char, ::Treap).insert(index)
    }

    val removedLettersPositions = HashSet<Int>()
    for (iterator in 1..n) {
        val (position, char) = readStrings().zipWithNext { p, c -> p.toInt() to c.first() }.first()
        lettersWithPositions[char]?.remove(position - 1)?.let { removedLettersPositions.add(it) }
    }

    s.repeat(k).asSequence().filterIndexed { index, _ -> index !in removedLettersPositions }.forEach(::print)
    println()
}
