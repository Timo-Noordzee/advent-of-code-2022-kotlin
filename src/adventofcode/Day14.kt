package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

private val Point.down get() = Point(x, y + 1)
private val Point.up get() = Point(x, y - 1)

private class Terrain14(
    private val grid: Array<CharArray>,
    private val source: Point,
    private val m: Int,
    private val n: Int,
) {

    override fun toString(): String = grid.joinToString("\n") { it.joinToString("") }

    fun solve(): Int {
        var amountOfSandGrains = 0
        var grainOfSand = Point(source.x, source.y).down
        source@ while (true) {
            // Move down until current position isn't air
            while (isAir(grainOfSand)) {
                val down = grainOfSand.down
                if (down.y >= m) break@source

                grainOfSand = grainOfSand.down
            }

            // If the left position is outside the grid break
            val left = grainOfSand.left
            if (left.x < 0) break@source

            // Check if the grain of sand can be moved one to the left or one to the right
            // If this isn't the case, the current grain of sand has come to rest.
            grainOfSand = if (isAir(left)) left else {
                val right = grainOfSand.right
                if (right.x >= n) break@source

                // If right is air, the grain of sand can move one to the right
                // otherwise the current grain of sand has come to rest.
                if (isAir(right)) right else {
                    amountOfSandGrains++
                    placeSand(grainOfSand.up)
                    if (grainOfSand.up == source) break@source

                    Point(source.x, source.y).down
                }
            }

            // current grain of sand has either move one to the left or the right
            // the next step is to move it down by one
            val down = grainOfSand.down
            if (down.y >= m) break@source
        }
        return amountOfSandGrains
    }

    fun placeSand(point: Point) {
        grid[point.y][point.x] = 'o'
    }

    fun isAir(point: Point) = grid[point.y][point.x] == '.'

    companion object {
        fun fromInput(input: List<String>, isVoid: Boolean): Terrain14 {
            val paths = input.map { line ->
                line.split(" -> ").map { Point(it) }
            }

            // Calculate the minimum required size to fit all the points (paths)
            var maxX = 500
            var maxY = 0

            paths.forEach { path ->
                path.forEach { point ->
                    maxX = maxX.coerceAtLeast(point.x)
                    maxY = maxY.coerceAtLeast(point.y)
                }
            }

            val m = maxY + if (isVoid) 1 else 3
            val n = maxX + m

            val grid = Array(m) { CharArray(n) { '.' } }

            paths.forEach { path ->
                path.zipWithNext().forEach { (from, to) ->
                    (from lineTo to).forEach { point ->
                        grid[point.y][point.x] = '#'
                    }
                }
            }
            grid[0][500] = '+'

            if (!isVoid) {
                grid[grid.lastIndex].fill('#')
            }


            return Terrain14(
                grid = grid,
                source = Point(500, 0),
                m = m,
                n = n
            )
        }
    }
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day14 {

    var input: List<String> = emptyList()

    @Setup
    fun setup() {
        input = readInput("Day14")
    }

    @Benchmark
    fun part1() = Terrain14.fromInput(input, true).solve()

    @Benchmark
    fun part2() = Terrain14.fromInput(input, false).solve()

}

fun main() {
    val day14 = Day14()

    // test if implementation meets criteria from the description, like:
    day14.input = readInput("Day14_test")
    check(day14.part1() == 24)
    check(day14.part2() == 93)

    day14.input = readInput("Day14")
    println(day14.part1())
    println(day14.part2())
}