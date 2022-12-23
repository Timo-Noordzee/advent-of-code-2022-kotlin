package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

private operator fun IntRange.component1() = first

private operator fun IntRange.component2() = last

private enum class Facing(val value: Int, val move: Move) {
    RIGHT(0, Move(1, 0)),
    DOWN(1, Move(0, 1)),
    LEFT(2, Move(-1, 0)),
    UP(3, Move(0, -1));

    companion object {
        fun from(value: Int) = values().first { it.value == value }
    }
}

private data class Jump(val x: Int, val y: Int, val facing: Facing)

private class Board(
    val grid: Array<CharArray>,
    val jumps: Map<Jump, Point>
) {

    var facing = Facing.RIGHT
    var position = kotlin.run {
        val x = grid[0].indexOfFirst { it == '.' }
        val y = grid.indices.first { i -> grid[i][x] == '.' }
        Point(x, y)
    }

    fun move(steps: Int) {
        repeat(steps) {
            val nextPosition = (position + facing.move).let { jumps[Jump(it.x, it.y, facing)] ?: it }
            if (grid[nextPosition.y][nextPosition.x] != '#') {
                position = nextPosition
            } else {
                return
            }
        }
    }

    fun rotate(direction: Char) {
        facing = when (direction) {
            'L' -> if (facing == Facing.RIGHT) Facing.UP else Facing.from(facing.value - 1)
            'R' -> Facing.from((facing.value + 1) % Facing.values().size)
            else -> error("check input")
        }
    }

    companion object {
        fun fromInput(input: List<String>): Board {
            val m = input.size
            val n = input.maxOf { it.length }

            val xRanges = Array(m) { i ->
                input[i].indexOfFirst { it != ' ' } until input[i].length
            }

            val yRanges = Array(n) { j ->
                val min = (0 until m).indexOfFirst { i -> input[i].getOrElse(j) { ' ' } != ' ' }
                val max = (0 until m).indexOfLast { i -> input[i].getOrElse(j) { ' ' } != ' ' }
                min..max
            }

            val grid = Array(m) { CharArray(n) { ' ' } }
            for (i in 0 until m) {
                val line = input[i]
                for (j in line.indices) {
                    grid[i][j] = line[j]
                }
            }

            val jumps = buildMap {
                xRanges.forEachIndexed { y, (start, end) ->
                    put(Jump(start - 1, y, Facing.LEFT), Point(end, y))
                    put(Jump(end + 1, y, Facing.RIGHT), Point(start, y))
                }
                yRanges.forEachIndexed { x, (start, end) ->
                    put(Jump(x, start - 1, Facing.UP), Point(x, end))
                    put(Jump(x, end + 1, Facing.DOWN), Point(x, start))
                }
            }

            return Board(
                grid = grid,
                jumps = jumps
            )
        }
    }
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day22 {

    var input: List<String> = emptyList()

    @Setup
    fun setup() {
        input = readInput("Day22")
    }

    @Benchmark
    fun part1(): Int {
        val board = Board.fromInput(input.dropLast(2))

        val regex = Regex("""\d+|\w""")
        val matches = regex.findAll(input.last()).map { it.value }.toList()

        matches.forEachIndexed { index, value ->
            if (index % 2 == 0) {
                board.move(value.toInt())
            } else {
                board.rotate(value[0])
            }
        }

        val (x, y) = board.position
        return (y + 1) * 1_000 + (x + 1) * 4 + board.facing.value
    }

    @Benchmark
    fun part2(): Int {
        TODO("Not yet implemented")
    }

}

fun main() {
    val day22 = Day22()

    // test if implementation meets criteria from the description, like:
    day22.input = readInput("Day22_test")
    check(day22.part1() == 6032)
//    check(day22.part2() == 0)

    day22.input = readInput("Day22")
    println(day22.part1())
//    println(day22.part2())
}