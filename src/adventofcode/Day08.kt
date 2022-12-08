package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

/**
 * @param si the starting value for i
 * @param sj the starting value for j
 * @param di the delta value for i
 * @param dj the delta value for j
 */
fun Array<IntArray>.vd(si: Int, sj: Int, di: Int, dj: Int): Int {
    val height = this[si][sj]
    var i = si + di
    var j = sj + dj
    while (i in indices && j in 0 until this[0].size) {
        if (this[i][j] >= height) return (i - si).absoluteValue + (j - sj).absoluteValue
        i += di
        j += dj
    }
    return (i - si - di).absoluteValue + (j - sj - dj).absoluteValue
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day08 {

    var input: List<String> = emptyList()

    @Setup
    fun setup() {
        input = readInput("Day08")
    }

    private fun parseTrees() = input.map { line -> line.map { char -> char - '0' }.toIntArray() }.toTypedArray()

    @Benchmark
    fun part1(): Int {
        val grid = parseTrees()
        val visible = Array(grid.size) { Array(grid[0].size) { false } }

        var maxHeight: Int

        // Left to right
        for (i in grid.indices) {
            maxHeight = -1
            for (j in 0 until grid.first().lastIndex) {
                val height = grid[i][j]
                if (height > maxHeight) {
                    maxHeight = height
                    visible[i][j] = true
                }
            }
        }

        // Right to left
        for (i in grid.indices) {
            maxHeight = -1
            for (j in grid.first().lastIndex downTo 1) {
                val height = grid[i][j]
                if (height > maxHeight) {
                    maxHeight = height
                    visible[i][j] = true
                }
            }
        }

        // Top to bottom
        for (j in grid[0].indices) {
            maxHeight = -1
            for (i in 0 until grid.lastIndex) {
                val height = grid[i][j]
                if (height > maxHeight) {
                    maxHeight = height
                    visible[i][j] = true
                }
            }
        }

        // Bottom to top
        for (j in grid[0].indices) {
            maxHeight = -1
            for (i in grid.lastIndex downTo 1) {
                val height = grid[i][j]
                if (height > maxHeight) {
                    maxHeight = height
                    visible[i][j] = true
                }
            }
        }

        return visible.sumOf { row -> row.count { it } }
    }

    @Benchmark
    fun part2(): Int = with(parseTrees()) {
        (1 until lastIndex).maxOf { i ->
            (1 until this[i].lastIndex).maxOf { j ->
                vd(i, j, 1, 0) * vd(i, j, -1, 0) * vd(i, j, 0, 1) * vd(i, j, 0, -1)
            }
        }
    }
}

fun main() {
    val day08 = Day08()

    // test if implementation meets criteria from the description, like:
    day08.input = readInput("Day08_test")
    check(day08.part1() == 21)
    check(day08.part2() == 8)

    day08.input = readInput("Day08")
    println(day08.part1())
    println(day08.part2())
}