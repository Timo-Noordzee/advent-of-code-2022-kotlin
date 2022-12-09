package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import kotlin.math.sign

private operator fun String.component1() = this[0]
private operator fun String.component2() = this.substringAfter(" ").toInt()

private fun Point.calculateMoveTo(head: Point): Move {
    val move = head getMoveTo this
    return if (move.chebyshevDistance > 1) Move(move.dx.sign, move.dy.sign) else Move(0, 0)
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day09 {

    var input: List<String> = emptyList()

    @Setup
    fun setup() {
        input = readInput("Day09")
    }

    private fun solve(numberOrKnots: Int): Int {
        val rope = Array(numberOrKnots) { Point(0, 0) }
        val visited = mutableSetOf(rope.last())

        input.forEach { (direction, count) ->
            val move = when (direction) {
                'L' -> Move(-1, 0)
                'R' -> Move(1, 0)
                'U' -> Move(0, 1)
                'D' -> Move(0, -1)
                else -> error("invalid direction $direction, check input")
            }

            repeat(count) {
                rope[0] = rope.first() + move
                rope.indices.windowed(2, 1) { (head, tail) ->
                    val requiredMove = rope[tail].calculateMoveTo(rope[head])
                    if (requiredMove.chebyshevDistance == 0) return@windowed
                    rope[tail] = rope[tail] + requiredMove
                }
                visited += rope.last()
            }
        }

        return visited.size
    }

    @Benchmark
    fun part1() = solve(2)

    @Benchmark
    fun part2() = solve(10)

}

fun main() {
    val day09 = Day09()

    // test if implementation meets criteria from the description, like:
    day09.input = readInput("Day09_test_1")
    check(day09.part1() == 13)
    day09.input = readInput("Day09_test_2")
    check(day09.part2() == 36)

    day09.input = readInput("Day09")
    println(day09.part1())
    println(day09.part2())
}