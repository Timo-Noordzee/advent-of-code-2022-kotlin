package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

private operator fun Monkey.times(other: Monkey) = amountOfInspections.toLong() * other.amountOfInspections

private data class Monkey(
    private var items: ArrayDeque<Long>,
    private val inspect: (item: Long) -> Long,
    val divisor: Int,
    private val monkeyWhenTrue: Int,
    private val monkeyWhenFalse: Int,
    var amountOfInspections: Int = 0,
) {
    fun addItem(item: Long) = items.add(item)

    fun takeTurn(lowerWorryLevel: Boolean, mod: Int = 0): List<Pair<Long, Int>> = buildList {
        while (items.isNotEmpty()) {
            amountOfInspections++
            val item = items.removeFirst()
            val worryLevel = if (lowerWorryLevel) inspect(item % mod) / 3 else inspect(item % mod)
            add(worryLevel to if (worryLevel % divisor == 0L) monkeyWhenTrue else monkeyWhenFalse)
        }
    }

    companion object {
        fun fromInput(input: List<String>): Monkey = Monkey(
            items = ArrayDeque(input[1].substringAfter(": ").split(',').map { it.trim().toInt().toLong() }),
            inspect = input[2].substringAfter("= ").split(" ").let { (a, operator, b) ->
                { old ->
                    val left = if (a == "old") old else a.toInt().toLong()
                    val right = if (b == "old") old else b.toInt().toLong()
                    when (operator[0]) {
                        '+' -> left + right
                        '*' -> left * right
                        else -> error("check input")
                    }
                }
            },
            divisor = input[3].substringAfterLast(' ').toInt(),
            monkeyWhenTrue = input[4].substringAfterLast(' ').toInt(),
            monkeyWhenFalse = input[5].substringAfterLast(' ').toInt()
        )
    }
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day11 {

    var input: List<String> = emptyList()

    @Setup
    fun setup() {
        input = readInput("Day11")
    }

    private fun solve(numberOfSimulations: Int, doesWorryLevelLower: Boolean): Long {
        val monkeys = input.chunked(7).map { Monkey.fromInput(it) }
        val commonDivisor = monkeys.map { it.divisor }.reduce { a, b -> a * b }

        repeat(numberOfSimulations) {
            monkeys.forEach { monkey ->
                monkey.takeTurn(doesWorryLevelLower, commonDivisor).forEach { (item, nextMonkey) ->
                    monkeys[nextMonkey].addItem(item)
                }
            }
        }

        val (mostActive, secondMostActive) = monkeys.sortedBy { monkey -> -monkey.amountOfInspections }
        return mostActive * secondMostActive
    }

    @Benchmark
    fun part1() = solve(20, true)

    @Benchmark
    fun part2(): Long = solve(10_000, false)
}

fun main() {
    val day11 = Day11()

    // test if implementation meets criteria from the description, like:
    day11.input = readInput("Day11_test")
    check(day11.part1() == 10605L)
    check(day11.part2() == 2713310158)

    day11.input = readInput("Day11")
    println(day11.part1())
    println(day11.part2())
}