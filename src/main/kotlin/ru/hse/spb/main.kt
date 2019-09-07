package ru.hse.spb

import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random

class Treap {
    private var root: TreapNode? = null
    private var rightmostNode: TreapNode? = null

    fun put(newKey: Int) {
        val newNode = TreapNode(newKey, null)

        if (root == null) {
            root = newNode
            rightmostNode = root
            return
        }

        when {
            root!!.priority <= newNode.priority -> {
                newNode.leftChild = root
                root = newNode
                rightmostNode = root
            }
            rightmostNode!!.priority >= newNode.priority -> {
                rightmostNode!!.rightChild = newNode
                rightmostNode = newNode
            }
            else -> {
                var curNode = rightmostNode
                while (curNode!!.priority < newNode.priority) {
                    curNode = curNode.parent
                }

                newNode.leftChild = curNode.rightChild
                curNode.rightChild = newNode

                rightmostNode = newNode
            }
        }

        var curNode: TreapNode? = newNode
        while (curNode != root) {
            curNode?.updateSize()
            curNode = curNode!!.parent
        }

        curNode?.updateSize()
    }

    fun delete(delKey: Int): Int {
        val (left, right) = root!!.split(delKey)
        val (toDelete, finalRight) = right?.split(1) ?: Pair(null, null)

        root = TreapNode.merge(left, finalRight)

        var curNode = root

        while (curNode?.rightChild != null) {
            curNode = curNode.rightChild
        }

        rightmostNode = curNode
        return toDelete?.key ?: -1
    }

    fun get(getKey: Int): Int {
        val (left, right) = root!!.split(getKey)
        val (res, rightmost) = right!!.split(1)
        root = TreapNode.merge(left, TreapNode.merge(res, rightmost))

        return res!!.key
    }

    class TreapNode(internal val key: Int, internal var parent: TreapNode? = null) {
        internal val priority: Long = Random.nextLong()

        var leftChild: TreapNode? = null
            set(value) {
                field = value
                value?.parent = this
            }
        var rightChild: TreapNode? = null
            set(value) {
                field = value
                value?.parent = this
            }
        private var subtreeSize: Int = 1

        companion object {
            fun merge(left: TreapNode?, right: TreapNode?): TreapNode? {
                if (left == null) {
                    right?.parent = null
                    return right
                }

                if (right == null) {
                    left.parent = null
                    return left
                }

                return if (left.priority > right.priority) {
                    val merged = merge(left.rightChild, right)
                    left.rightChild = merged
                    left.parent = null
                    left.updateSize()
                    left
                } else {
                    val merged = merge(left, right.leftChild)
                    right.leftChild = merged
                    right.parent = null
                    right.updateSize()
                    right
                }
            }
        }

        internal fun updateSize() {
            val leftSize = if (leftChild == null) 0 else leftChild!!.subtreeSize
            val rightSize = if (rightChild == null) 0 else rightChild!!.subtreeSize

            subtreeSize = 1 + leftSize + rightSize
        }

        internal fun split(splitKey: Int, add: Int = 0): Pair<TreapNode?, TreapNode?> {
            val currentKey = add + if (leftChild == null) 0 else leftChild!!.subtreeSize
            val resNode = this

            if (splitKey <= currentKey) {
                if (leftChild == null) {
                    this.parent = null
                    return Pair(null, this)
                }

                val (left, right) = leftChild!!.split(splitKey, add)

                resNode.leftChild = right
                resNode.rightChild = rightChild

                resNode.updateSize()

                left?.parent = null
                return Pair(left, resNode)
            } else {
                if (rightChild == null) {
                    this.parent = null
                    return Pair(this, null)
                }

                val (left, right) = rightChild!!.split(splitKey, add + 1 +
                        if (leftChild == null) 0 else leftChild!!.subtreeSize)

                resNode.leftChild = leftChild
                resNode.rightChild = left

                resNode.updateSize()

                right?.parent = null
                return Pair(resNode, right)
            }
        }
    }
}

class CompetitiveIO {
    companion object {
        fun readLn() = readLine()!!
        fun readInt() = readLn().toInt()
        private fun readStrings() = readLn().split(" ")
        fun readCharAndInt(): Pair<Int, Char> {
            val (int, char) = readStrings()
            return Pair(int.toInt(), char[0])
        }
    }
}

fun solveProblem(name: String, k: Int, deletes: List<Pair<Int, Char>>): String {
    val toTakeLetters = arrayListOf<Boolean>()
    val letterToTree = HashMap<Char, Treap>()

    for (i in 0 until k) {
        for (j in 0 until name.length) {
            toTakeLetters.add(true)
            val char = name[j]
            val key = i * name.length + j

            if (!letterToTree.containsKey(char)) {
                letterToTree[char] = Treap()
            }

            letterToTree[char]!!.put(key)
        }
    }

    for ((ind, char) in deletes) {
        val key = letterToTree[char]!!.delete(ind - 1)

        if (key >= 0) {
            toTakeLetters[key] = false
        }
    }

    val result = StringBuilder()

    for (i in 0 until k) {
        for (j in 0 until name.length) {
            val char = name[j]
            val key = i * name.length + j

            if (toTakeLetters[key]) {
               result.append(char)
            }
        }
    }

    return result.toString()
}

fun main() {
    val k = CompetitiveIO.readInt()
    val name = CompetitiveIO.readLn()

    val n = CompetitiveIO.readInt()
    val deletes = LinkedList<Pair<Int, Char>>()

    for (i in 0 until n) {
        deletes.add(CompetitiveIO.readCharAndInt())
    }

    print(solveProblem(name, k, deletes))
}