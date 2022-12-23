package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

private fun MutableSet<Point>.move(searchDirections: Array<Array<Move>>): Int {
    var moved = 0
    this.filter { it.getAllNeighbors().any { neighbor -> neighbor in this } }
        .mapNotNull { elf ->
            val (_, move, _) = searchDirections.firstOrNull { direction ->
                direction.all { point -> (elf + point) !in this }
            } ?: return@mapNotNull null
            elf to elf + move
        }
        .groupBy({ (_, target) -> target }, { it.first })
        .forEach { (target, contestants) ->
            if (contestants.size == 1) {
                moved++
                remove(contestants[0])
                add(target)
            }
        }
    return moved
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day23 {

    var input: List<String> = emptyList()

    private val directions = arrayOf(
        arrayOf(Move(-1, -1), Move(0, -1), Move(1, -1)),
        arrayOf(Move(-1, 1), Move(0, 1), Move(1, 1)),
        arrayOf(Move(-1, -1), Move(-1, 0), Move(-1, 1)),
        arrayOf(Move(1, -1), Move(1, 0), Move(1, 1)),
    )

    @Setup
    fun setup() {
        input = readInput("Day23")
    }

    private fun parseInput(): MutableSet<Point> {
        val elves = mutableSetOf<Point>()
        for (i in input.indices) for (j in input[i].indices) {
            if (input[i][j] == '#') {
                elves.add(Point(j, i))
            }
        }
        return elves
    }

    @Benchmark
    fun part1(): Int {
        val elves = parseInput()
        repeat(10) { i ->
            val directions = Array(4) { directions[(i + it) % 4] }
            elves.move(directions)
        }

        val m = (elves.minOf { it.y }..elves.maxOf { it.y }).size
        val n = (elves.minOf { it.x }..elves.maxOf { it.x }).size
        return m * n - elves.size
    }

    @Benchmark
    fun part2(): Int {
        val elves = parseInput()
        var round = 0
        var moved = true
        while (moved) {
            val directions = Array(4) { directions[(round + it) % 4] }
            moved = elves.move(directions) > 0
            round++
        }
        return round
    }
}

fun main() {
    val day23 = Day23()

    // test if implementation meets criteria from the description, like:
    day23.input = readInput("Day23_test")
    check(day23.part1() == 110)
    check(day23.part2() == 20)

    day23.input = readInput("Day23")
    println(day23.part1())
    println(day23.part2())
}