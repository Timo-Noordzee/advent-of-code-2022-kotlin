package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

private data class GeodeState(
    val robots: IntArray,
    val ore: Int,
    val clay: Int,
    val obsidian: Int,
    val geode: Int,
    val time: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GeodeState

        if (!robots.contentEquals(other.robots)) return false
        if (ore != other.ore) return false
        if (clay != other.clay) return false
        if (obsidian != other.obsidian) return false
        if (geode != other.geode) return false
        if (time != other.time) return false

        return true
    }

    override fun hashCode(): Int {
        var result = robots.contentHashCode()
        result = 31 * result + ore
        result = 31 * result + clay
        result = 31 * result + obsidian
        result = 31 * result + geode
        result = 31 * result + time
        return result
    }
}

private operator fun List<Int>.component2() = this.drop(1).toIntArray()

private fun IntArray.withNewRobot(robot: Int) = clone().apply { this[robot] = this[robot] + 1 }

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day19 {

    var input: List<String> = emptyList()
    private val memo = mutableMapOf<GeodeState, Int>()

    @Setup
    fun setup() {
        input = readInput("Day19")
    }

    private fun calculateMaxGeodes(
        costs: IntArray,
        maxOreCost: Int,
        state: GeodeState,
    ): Int = memo[state] ?: kotlin.run {
        val (robots, ore, clay, obsidian, geode, time) = state
        if (time == 0) return geode

        val maxOreSpent = maxOreCost * time
        val maxClaySpent = costs[3] * time
        val maxObsidianSpent = costs[5] * time

        var max = geode
        if (ore >= costs[4] && obsidian >= costs[5]) {
            max = max.coerceAtLeast(
                calculateMaxGeodes(
                    costs,
                    maxOreCost,
                    state.copy(
                        robots = robots.withNewRobot(3),
                        ore = maxOreSpent.coerceAtMost(ore + robots[0] - costs[4]),
                        clay = maxClaySpent.coerceAtMost(clay + robots[1]),
                        obsidian = maxObsidianSpent.coerceAtMost(obsidian + robots[2] - costs[5]),
                        geode = geode + robots[3],
                        time = time - 1
                    )
                )
            )
        } else {
            if (costs[5] > robots[2] && ore >= costs[2] && clay >= costs[3]) {
                if (robots[2] * time + obsidian < time * costs[5]) {
                    max = max.coerceAtLeast(
                        calculateMaxGeodes(
                            costs,
                            maxOreCost,
                            state.copy(
                                robots = robots.withNewRobot(2),
                                ore = maxOreSpent.coerceAtMost(ore + robots[0] - costs[2]),
                                clay = maxClaySpent.coerceAtMost(clay + robots[1] - costs[3]),
                                obsidian = maxObsidianSpent.coerceAtMost(obsidian + robots[2]),
                                geode = geode + robots[3],
                                time = time - 1
                            )
                        )
                    )
                }
            }

            if (costs[3] > robots[1] && ore >= costs[1]) {
                if (robots[1] * time + clay < time * costs[3]) {
                    max = max.coerceAtLeast(
                        calculateMaxGeodes(
                            costs,
                            maxOreCost,
                            state.copy(
                                robots = robots.withNewRobot(1),
                                ore = maxOreSpent.coerceAtMost(ore + robots[0] - costs[1]),
                                clay = maxClaySpent.coerceAtMost(clay + robots[1]),
                                obsidian = maxObsidianSpent.coerceAtMost(obsidian + robots[2]),
                                geode = geode + robots[3],
                                time = time - 1
                            )
                        )
                    )
                }
            }

            if (maxOreCost > robots[0] && ore >= costs[0]) {
                if (robots[0] * time + ore < time * maxOreCost) {
                    max = max.coerceAtLeast(
                        calculateMaxGeodes(
                            costs,
                            maxOreCost,
                            state.copy(
                                robots = robots.withNewRobot(0),
                                ore = maxOreSpent.coerceAtMost(ore + robots[0] - costs[0]),
                                clay = maxClaySpent.coerceAtMost(clay + robots[1]),
                                obsidian = maxObsidianSpent.coerceAtMost(obsidian + robots[2]),
                                geode = geode + robots[3],
                                time = time - 1
                            )
                        )
                    )
                }
            }

            max = max.coerceAtLeast(
                calculateMaxGeodes(
                    costs,
                    maxOreCost,
                    state.copy(
                        robots = robots,
                        ore = maxOreSpent.coerceAtMost(ore + robots[0]),
                        clay = maxClaySpent.coerceAtMost(clay + robots[1]),
                        obsidian = maxObsidianSpent.coerceAtMost(obsidian + robots[2]),
                        geode = geode + robots[3],
                        time = time - 1
                    )
                )
            )
        }

        memo[state] = max
        max
    }

    private fun getMaxGeodesPerBlueprint(blueprints: List<String>, time: Int): List<Int> {
        val regex = Regex("""\d+""")
        return blueprints.map { blueprint ->
            memo.clear()
            val costs = regex.findAll(blueprint).map { it.value.toInt() }.toList().drop(1).toIntArray()
            val maxOreCost = maxOf(costs[0], costs[1], costs[2], costs[4])
            val geodes = calculateMaxGeodes(costs, maxOreCost, GeodeState(intArrayOf(1, 0, 0, 0), 0, 0, 0, 0, time))
            println("geodes: $geodes")
            geodes
        }
    }

    @Benchmark
    fun part1(): Int = getMaxGeodesPerBlueprint(input, 24).withIndex().sumOf { (index, geodes) -> (index + 1) * geodes }

    @Benchmark
    fun part2(): Int = getMaxGeodesPerBlueprint(input.take(3), 32).reduce { a, b -> a * b }

}

fun main() {
    val day19 = Day19()

    // test if implementation meets criteria from the description, like:
    day19.input = readInput("Day19_test")
    check(day19.part1() == 33)
    check(day19.part2() == 3472)

    day19.input = readInput("Day19")
    println(day19.part1())
    println(day19.part2())
}