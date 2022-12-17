@file:Suppress("SameParameterValue")

package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import kotlin.math.max

class Valve(
    val id: String,
    val flowRate: Int,
    val tunnels: List<String>
) {
    val isStart = id == "AA"
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day16 {

    var input: List<String> = emptyList()

    @Setup
    fun setup() {
        input = readInput("Day16")
    }

    private fun solve(position: Int, opened: Int, time: Int, otherPlayers: Int): Int {
        val regex = Regex("""\d+|([A-Z]{2})""")
        val valves = input.map { line ->
            val matches = regex.findAll(line).map { it.value }.toList()
            Valve(
                id = matches[0],
                flowRate = matches[1].toInt(),
                tunnels = matches.drop(2)
            )
        }.sortedWith(compareBy(Valve::isStart, Valve::flowRate)).reversed()

        val size = valves.size
        val indexMap = valves.withIndex().associate { it.value.id to it.index }
        val flowRates = IntArray(size) { valves[it].flowRate }
        val tunnels = Array(size) { valves[it].tunnels.map { id -> indexMap[id] } }
        val dp = IntArray((1 shl (flowRates.count { it > 0 } + 1)) * size * 31 * 2) { -1 }

        fun calculateMaxFlowRate(position: Int, opened: Int, time: Int, otherPlayers: Int): Int {
            if (time == 0) return if (otherPlayers == 0) 0 else calculateMaxFlowRate(0, opened, 26, otherPlayers - 1)

            val key = opened * size * 31 * 2 + position * 31 * 2 + time * 2 + otherPlayers
            if (dp[key] >= 0) return dp[key]

            var ans = 0
            val isClosed = (opened and (1 shl position)) == 0
            if (isClosed && flowRates[position] > 0) {
                val newOpened = opened or (1 shl position)
                ans = max(
                    ans,
                    (time - 1) * flowRates[position] + calculateMaxFlowRate(position, newOpened, time - 1, otherPlayers)
                )
            }

            for (newPosition in tunnels[position]) {
                ans = max(ans, calculateMaxFlowRate(newPosition!!, opened, time - 1, otherPlayers))
            }

            dp[key] = ans
            return ans
        }

        return calculateMaxFlowRate(position, opened, time, otherPlayers)
    }

    @Benchmark
    fun part1(): Int = solve(0, 0, 30, 0)

    @Benchmark
    fun part2(): Int = solve(0, 0, 26, 1)

}

fun main() {
    val day16 = Day16()

    // test if implementation meets criteria from the description, like:
    day16.input = readInput("Day16_test")
    check(day16.part1() == 1651)
    check(day16.part2() == 1707)

    day16.input = readInput("Day16")
    println(day16.part1())
    println(day16.part2())
}