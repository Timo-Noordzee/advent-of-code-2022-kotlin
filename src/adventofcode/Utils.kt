package adventofcode

import java.io.File

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src/adventofcode", "$name.txt")
    .readLines()

val IntRange.size get() = (last - first + 1).coerceAtLeast(0)

fun Iterable<IntRange>.merge(): List<IntRange> {
    val sorted = this.filter { !it.isEmpty() }.sortedBy { it.first }
    sorted.isNotEmpty() || return emptyList()

    val stack = ArrayDeque<IntRange>()
    stack.add(sorted.first())
    sorted.drop(1).forEach { current ->
        if (current.last <= stack.last().last) {
            // ignore as it's completely within
        } else if (current.first > stack.last().last + 1) {
            // it's completely out and after the last
            stack.add(current)
        } else {
            // they overlap
            stack.add(stack.removeLast().first..current.last)
        }
    }
    return stack
}