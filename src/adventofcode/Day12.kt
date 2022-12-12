package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayDeque

private typealias Grid = Array<IntArray>
private operator fun Array<IntArray>.get(point: Point): Int = this[point.y][point.x]

private class Terrain(
    val start: Point,
    val end: Point,
    private val m: Int,
    private val n: Int,
    private val grid: Grid,
) {

    // Solve using BFS
    fun solve(isDestination: Terrain.(point: Point) -> Boolean): Int {
        val queue = ArrayDeque<Point>().apply { add(end) }
        val visited = BooleanArray(m * n).apply { this[end.getIndex(n)] = true }
        val steps = IntArray(m * n).apply { this[end.getIndex(n)] = 0 }

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (this.isDestination(current)) return steps[current.getIndex(n)]

            current.getNeighbors().forEach { point ->
                val index = point.getIndex(n)
                if (point in this && !visited[index] && grid[current] - grid[point] < 2) {
                    visited[index] = true
                    steps[index] = steps[current.getIndex(n)] + 1
                    queue.add(point)
                }
            }
        }

        error("no path found, check your input")
    }

    operator fun contains(point: Point) = point.y in grid.indices && point.x in grid[0].indices

    operator fun get(y: Int) = grid[y]

    companion object {
        fun fromInput(input: List<String>): Terrain {
            var start = Point(0, 0)
            var end = Point(0, 0)

            val m = input.size
            val n = input[0].length
            val grid: Grid = Array(m) { IntArray(n) }
            for (i in input.indices) {
                for (j in 0 until n) {
                    when (val char = input[i][j]) {
                        'S' -> {
                            start = Point(j, i)
                            grid[i][j] = 0
                        }
                        'E' -> {
                            end = Point(j, i)
                            grid[i][j] = 'z' - 'a'
                        }
                        else -> grid[i][j] = char - 'a'
                    }
                }
            }

            return Terrain(start, end, m, n, grid)
        }
    }
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day12 {

    var input: List<String> = emptyList()

    @Setup
    fun setup() {
        input = readInput("Day12")
    }

    @Benchmark
    fun part1() = Terrain.fromInput(input).solve { point -> point atSamePositionAs start }

    @Benchmark
    fun part2() = Terrain.fromInput(input).solve { point -> this[point.y][point.x] == 0 }

}

fun main() {
    val day12 = Day12()

    // test if implementation meets criteria from the description, like:
    day12.input = readInput("Day12_test")
    check(day12.part1() == 31)
    check(day12.part2() == 29)

    day12.input = readInput("Day12")
    println(day12.part1())
    println(day12.part2())
}