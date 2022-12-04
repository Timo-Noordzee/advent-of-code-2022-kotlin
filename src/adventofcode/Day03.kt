package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

private operator fun String.component1() = subSequence(0, length / 2)
private operator fun String.component2() = subSequence(length / 2, length)

fun CharSequence.toLong() = this.fold(0L) { total, char -> total or (1L shl char - 'a') }

private infix fun CharSequence.and(s: CharSequence) = this.toLong() and s.toLong()

private infix fun Long.and(s: String) = this and s.toLong()

private fun Long.getPriority(): Int = countTrailingZeroBits().let { bits -> if (bits > 26) bits - 5 else bits + 1 }

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day03 {
    var input: List<String> = emptyList()

    @Setup
    fun setup() {
        input = readInput("Day03")
    }

    @Benchmark
    fun part1(): Int = input.sumOf { (firstCompartment, secondCompartment) ->
        val duplicate = firstCompartment and secondCompartment
        duplicate.getPriority()
    }

    @Benchmark
    fun part2(): Int = input.windowed(3, 3).sumOf { group ->
        val badge = group[0] and group[1] and group[2]
        badge.getPriority()
    }
}

fun main() {
    val day03 = Day03()

    // test if implementation meets criteria from the description, like:
    day03.input = readInput("Day03_test")
    check(day03.part1() == 157)
    check(day03.part2() == 70)

    day03.input = readInput("Day03")
    println(day03.part1())
    println(day03.part2())
}
