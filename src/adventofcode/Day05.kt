package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

typealias CrateStacks = Array<ArrayDeque<Char>>

private fun String.getMoveData(): IntArray = split(" ").let {
    intArrayOf(it[1].toInt(), it[3].toInt() - 1, it[5].toInt() - 1)
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day05 {

    var input: List<String> = emptyList()

    @Setup
    fun setup() {
        input = readInput("Day05")
    }

    private fun solve(moveOperation: CrateStacks.(amount: Int, from: Int, to: Int) -> Unit): String {
        // Start at index 1 and use index - 1 for each step
        // When blank row is reached, current index containers the stack numbers and can easily be skipped
        var index = 1
        val crateStacks: CrateStacks = Array(9) { ArrayDeque() }
        while (input[index].isNotBlank()) {
            // Iterate using stack index, multiplying index by 4 + 1 gives the char position in the string
            for (i in 0 until (input[index - 1].length + 1) / 4) {
                val char = input[index - 1][i * 4 + 1]
                if (char != ' ') {
                    crateStacks[i].add(0, char)
                }
            }
            index++
        }

        // skip row containing stack numbers and set index to the first line containing a move action
        index++

        for (i in index until input.size) {
            val (amount, from, to) = input[i].getMoveData()
            crateStacks.moveOperation(amount, from, to)
        }

        return buildString {
            crateStacks.forEach { stack ->
                if (stack.isNotEmpty()) {
                    append(stack.last())
                }
            }
        }
    }

    @Benchmark
    fun part1(): String {
        return solve { amount, from, to ->
            repeat(amount) {
                this[to].add(this[from].removeLast())
            }
        }
    }

    @Benchmark
    fun part2(): String {
        return solve { amount, from, to ->
            val queue = CharArray(amount)
            repeat(amount) {
                queue[it] = this[from].removeLast()
            }

            for (i in queue.lastIndex downTo 0) {
                this[to].add(queue[i])
            }
        }
    }
}

fun main() {
    val day05 = Day05()

    // test if implementation meets criteria from the description, like:
    day05.input = readInput("Day05_test")
    check(day05.part1() == "CMZ")
    check(day05.part2() == "MCD")

    day05.input = readInput("Day05")
    println(day05.part1())
    println(day05.part2())

}