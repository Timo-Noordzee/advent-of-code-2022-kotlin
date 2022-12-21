package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

private const val ROOT = "root"
private const val HUMAN = "humn"

private operator fun List<String>.component2() = this[1][0]

private fun MutableMap<String, String>.compute(monkeyId: String): Long? {
    val monkeyValue = getValue(monkeyId)
    if (monkeyValue.isEmpty()) return null
    if (monkeyValue[0].isDigit()) return monkeyValue.toInt().toLong()

    val (left, operator, right) = monkeyValue.split(' ')
    val a = compute(left)?.also { this[left] = it.toString() }
    val b = compute(right)?.also { this[right] = it.toString() }
    if (a == null || b == null) {
        this[monkeyId] = "${a ?: left} $operator ${b ?: right}"
        return null
    }

    val value = when (operator) {
        '+' -> a + b
        '-' -> a - b
        '*' -> a * b
        '/' -> a / b
        else -> error("unknown operator")
    }
    this[monkeyId] = value.toString()
    return value
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day21 {

    var input: List<String> = emptyList()

    @Setup
    fun setup() {
        input = readInput("Day21")
    }

    private fun parseMonkeys(): MutableMap<String, String> {
        val monkeys = mutableMapOf<String, String>()
        input.forEach { line -> monkeys[line.substringBefore(':')] = line.substringAfter(' ') }
        return monkeys
    }

    @Benchmark
    fun part1(): Long = parseMonkeys().compute(ROOT)!!

    @Benchmark
    fun part2(): Long {
        val monkeys = parseMonkeys()
        monkeys[HUMAN] = ""
        monkeys[ROOT] = monkeys.getValue(ROOT).split(' ').let { (left, _, right) ->
            "$left = $right"
        }

        // Precompute most values so only the unknown path to humn remains
        monkeys.compute(ROOT)

        var ans = 0L
        var current = ROOT

        // Perform the reversed operations starting from root until humn has been reached.
        while (current != HUMAN) {
            val (left, operator, right) = monkeys.getValue(current).split(' ')
            ans = when {
                left.isNumeric() -> {
                    current = right
                    val value = left.toLong()
                    when (operator) {
                        '=' -> value
                        '+' -> ans - value
                        '-' -> value - ans
                        '*' -> ans / value
                        '/' -> value / ans
                        else -> error("unknown operator")
                    }
                }
                right.isNumeric() -> {
                    current = left
                    val value = right.toLong()
                    when (operator) {
                        '=' -> value
                        '+' -> ans - value
                        '-' -> ans + value
                        '*' -> ans / value
                        '/' -> ans * value
                        else -> error("unknown operator")
                    }
                }
                else -> error("neither left nor right is numeric")
            }
        }

        return ans
    }

}

fun main() {
    val day21 = Day21()

    // test if implementation meets criteria from the description, like:
    day21.input = readInput("Day21_test")
    check(day21.part1() == 152L)
    check(day21.part2() == 301L)

    day21.input = readInput("Day21")
    println(day21.part1())
    println(day21.part2()) // 3952673930912
}