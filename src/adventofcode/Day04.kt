package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.*

private infix fun IntRange.fullyContains(other: IntRange): Boolean {
    return this.first >= other.first && this.last <= other.last
}

private infix fun IntRange.overlapsWith(other: IntRange): Boolean {
    return this.first <= other.last && other.first <= this.last
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day04 {
    var input: List<String> = emptyList()

    fun parseInput(input: List<String>) = input.map { pair ->
        pair.split(",").map { range ->
            val nums = range.split("-").map { it.toInt() }
            nums[0].. nums[1]
        }
    }

    @Setup
    fun setup() {
        input = readInput("Day04")
    }

    @Benchmark
    fun part1(): Int = parseInput(input).count { (first, second) ->
        first fullyContains second || second fullyContains first
    }

    @Benchmark
    fun part2(): Int = parseInput(input).count { (first, second) ->
        first overlapsWith second
    }

}

fun main() {
    val day04 = Day04()

    // test if implementation meets criteria from the description, like:
    day04.input = readInput("Day04_test")
    check(day04.part1() == 2)
    check(day04.part2() == 4)

    day04.input = readInput("Day04")
    println(day04.part1())
    println(day04.part2())
}