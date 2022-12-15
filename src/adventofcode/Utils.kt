package adventofcode

import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src/adventofcode", "$name.txt")
    .readLines()

/**
 * Converts string to adventofcode.adventofcode.md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

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