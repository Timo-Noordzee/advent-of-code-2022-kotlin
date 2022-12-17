package adventofcode

import org.openjdk.jmh.annotations.Benchmark
import kotlin.math.max

class Valve(
    val id: String,
    val flowRate: Int,
    val tunnels: List<String>
) {
    val isStart = id == "AA"
}

class Day16(input: List<String>) {

    private val regex = Regex("""\d+|([A-Z]{2})""")
    private val valves = input.map { line ->
        val matches = regex.findAll(line).map { it.value }.toList()
        Valve(
            id = matches[0],
            flowRate = matches[1].toInt(),
            tunnels = matches.drop(2)
        )
    }.sortedWith(compareBy(Valve::isStart, Valve::flowRate)).reversed()

    private val size = valves.size
    private val indexMap = valves.withIndex().associate { it.value.id to it.index }
    private val flowRates = IntArray(size) { valves[it].flowRate }
    private val tunnels = Array(size) { valves[it].tunnels.map { id -> indexMap[id] } }
    private val dp = IntArray((1 shl (flowRates.count { it > 0 } + 1)) * size * 31 * 2) { -1 }

    private fun calculateMaxFlowRate(position: Int, opened: Int, time: Int, otherPlayers: Int): Int {
        if (time == 0) return if (otherPlayers == 0) 0 else calculateMaxFlowRate(0, opened, 26, otherPlayers - 1)

        val key = opened * size * 31 * 2 + position * 31 * 2 + time * 2 + otherPlayers
        if (dp[key] >= 0) return dp[key]

        var ans = 0
        val isClosed = (opened and (1 shl position)) == 0
        if (isClosed && flowRates[position] > 0) {
            val newOpened = opened or (1 shl position)
            ans = max(ans, (time - 1) * flowRates[position] + calculateMaxFlowRate(position, newOpened, time - 1, otherPlayers))
        }

        for (newPosition in tunnels[position]) {
            ans = max(ans, calculateMaxFlowRate(newPosition!!, opened, time - 1, otherPlayers))
        }

        dp[key] = ans
        return ans
    }

    @Benchmark
    fun part1(): Int = calculateMaxFlowRate(0, 0, 30, 0)

    @Benchmark
    fun part2(): Int = calculateMaxFlowRate(0, 0, 26, 1)

}

fun main() {

    // test if implementation meets criteria from the description, like:
    var day16Test = Day16(readInput("Day16_test"))
    check(day16Test.part1() == 1651)
    day16Test = Day16(readInput("Day16_test"))
    check(day16Test.part2() == 1707)

    var day16 = Day16(readInput("Day16"))
    println(day16.part1())
    day16 = Day16(readInput("Day16"))
    println(day16.part2())

}