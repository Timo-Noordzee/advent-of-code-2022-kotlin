package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day06 {

    var input: String = ""

    @Setup
    fun setup(){
        input = readInput("Day06").first()
    }

    private fun solve(n: Int): Int {
        var left = 0
        var right = 0
        val lastSeen = IntArray(26)
        while (right - left < n && right < input.length) {
            val char = input[right]

            val index = lastSeen[char - 'a']
            if (index in left until right) {
                left = index + 1
            }

            lastSeen[char - 'a'] = right
            right++
        }
        return right
    }

    @Benchmark
    fun part1() = solve(4)

    @Benchmark
    fun part2() = solve(14)

}

fun main() {
    val day06 = Day06()

    // test if implementation meets criteria from the description, like:
    day06.input = readInput("Day06_test").first()
    check(day06.part1() == 7)
    check(day06.part2() == 19)

    day06.input = readInput("Day06").first()
    println(day06.part1())
    println(day06.part2())
}