package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import kotlin.math.*

private fun String.fromSNAFU() = indices.fold(0L) { num, index ->
    val multiplier = 5.0.pow(lastIndex - index).toLong()
    num + when (val char = this[index]) {
        '-' -> -multiplier * 1
        '=' -> -multiplier * 2
        else -> multiplier * char.digitToInt()
    }
}

private fun Long.toSNAFU(): String {
    var remainder = this
    val count = log(this.toDouble(), 5.0).toInt()
    return buildString {
        (count.downTo(0)).forEach { power ->
            when (remainder) {
                0L -> append('0')
                else -> {
                    val amount = round(remainder / 5.0.pow(power)).toInt()
                    remainder -= amount * 5.0.pow(power).toLong()
                    append(
                        when (amount) {
                            2 -> '2'
                            1 -> '1'
                            0 -> '0'
                            -1 -> '-'
                            -2 -> '='
                            else -> error("check input")
                        }
                    )
                }
            }
        }
    }
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day25 {

    var input: List<String> = emptyList()

    @Setup
    fun setup() {
        input = readInput("Day25")
    }

    @Benchmark
    fun part1(): String = input.sumOf { it.fromSNAFU() }.toSNAFU()
}

fun main() {
    val day25 = Day25()

    // test if implementation meets criteria from the description, like:
    day25.input = readInput("Day25_test")
    check(day25.part1() == "2=-1=0")

    day25.input = readInput("Day25")
    println(day25.part1())
}