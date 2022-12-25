package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

private class IndexedNumber(val index: Int, val value: Long)

private fun List<IndexedNumber>.getCoordinates(): Long {
    val start = indexOfFirst { it.value == 0L }
    return listOf(1_000, 2_000, 3_000).sumOf { this[(start + it) % size].value }
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day20 {

    var input: List<String> = emptyList()

    @Setup
    fun setup() {
        input = readInput("Day20")
    }

    private fun solve(rounds: Int, decryptionKey: Long) = input.mapIndexed { index, value ->
        IndexedNumber(index, decryptionKey * value.toLong())
    }.toMutableList().apply {
        repeat(rounds) {
            indices.forEach { originalIndex ->
                val index = indexOfFirst { it.index == originalIndex }
                val indexedNumber = removeAt(index)
                add((index + indexedNumber.value).mod(size), indexedNumber)
            }
        }
    }.getCoordinates()

    @Benchmark
    fun part1() = solve(1, 1)

    @Benchmark
    fun part2() = solve(10, 811589153)
}

fun main() {
    val day20 = Day20()

    // test if implementation meets criteria from the description, like:
    day20.input = readInput("Day20_test")
    check(day20.part1() == 3L)
    check(day20.part2() == 1623178306L)

    day20.input = readInput("Day20")
    println(day20.part1())
    println(day20.part2())
}