package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day10 {

    var input: List<String> = emptyList()

    @Setup
    fun setup() {
        input = readInput("Day10")
    }

    private fun getRegisterValues() = buildList {
        var x = 1
        add(x)
        input.forEach { line ->
            add(x)
            if (line[0] != 'n') {
                x += line.substringAfter(' ').toInt()
                add(x)
            }
        }
    }

    @Benchmark
    fun part1() = getRegisterValues().let { registerValues ->
        (20..220 step 40).sumOf { cycle ->
            cycle * registerValues[cycle - 1]
        }
    }

    @Benchmark
    fun part2() = getRegisterValues().windowed(40, 40, false).joinToString("\n") { values ->
        buildString {
            repeat(values.size) { cycle ->
                if (abs(cycle - values[cycle]) < 2) append("#") else append(".")
            }
        }
    }
}

fun main() {
    val day10 = Day10()

    // test if implementation meets criteria from the description, like:
    day10.input = readInput("Day10_test")
    check(day10.part1() == 13140)

    day10.input = readInput("Day10")
    println(day10.part1())
    println(day10.part2())
}