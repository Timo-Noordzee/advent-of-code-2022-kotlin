package adventofcode

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Setup

private operator fun Valley.contains(point: Point): Boolean = point.x in xBound && point.y in yBound

private data class Valley(val xBound: IntRange, val yBound: IntRange, val blizzards: Set<Blizzard>) {

    val blocked = blizzards.map { it.position }.toSet()

    fun isWalkable(point: Point) = point in this && point !in blocked

    fun blizzardAt(point: Point) = point in blocked

    fun atNextMinute() = copy(
        blizzards = blizzards.map { it.move(xBound, yBound) }.toSet(),
    )

    companion object {
        fun fromInput(input: List<String>) = Valley(
            xBound = 1 until input[0].lastIndex,
            yBound = 1 until input.lastIndex,
            blizzards = buildSet {
                for (y in input.indices) for (x in input[y].indices) {
                    when (input[y][x]) {
                        '^' -> add(Blizzard(Point(x, y), Move(0, -1)))
                        '>' -> add(Blizzard(Point(x, y), Move(1, 0)))
                        'v' -> add(Blizzard(Point(x, y), Move(0, 1)))
                        '<' -> add(Blizzard(Point(x, y), Move(-1, 0)))
                    }
                }
            }
        )
    }
}

private data class Blizzard(val position: Point, val direction: Move) {
    fun move(xBound: IntRange, yBound: IntRange): Blizzard {
        val (x, y) = position + direction
        return copy(
            position = when {
                x < xBound.first -> Point(xBound.last, y)
                x > xBound.last -> Point(xBound.first, y)
                y < yBound.first -> Point(x, yBound.last)
                y > yBound.last -> Point(x, yBound.first)
                else -> Point(x, y)
            }
        )
    }
}

private data class Step(val minute: Int, val location: Point) {
    fun next(newLocation: Point = location) = Step(minute + 1, newLocation)
}

class Day24 {

    var input: List<String> = emptyList()

    @Setup
    fun setup() {
        input = readInput("Day24")
    }

    private fun solve(
        initialValley: Valley,
        start: Point,
        targets: ArrayDeque<Point>,
    ): Int {
        val memo = mutableMapOf(0 to initialValley)
        val queue = ArrayDeque<Step>().apply { add(Step(0, start)) }
        val seen = mutableSetOf<Step>()
        var target = targets.removeFirst()

        while (queue.isNotEmpty()) {
            val step = queue.removeFirst()
            if (seen.add(step)) {
                val (minute, position) = step
                val valley = memo.computeIfAbsent(minute + 1) { key ->
                    memo.getValue(key - 1).atNextMinute()
                }

                if (!valley.blizzardAt(position)) queue.add(step.next())

                val neighbors = position.getNeighbors()

                if (target in neighbors) {
                    val newTarget = targets.removeFirstOrNull() ?: return minute + 1
                    queue.clear()
                    queue.add(step.next(target))
                    target = newTarget
                    continue
                }

                neighbors.filter { neighbor -> valley.isWalkable(neighbor) }.forEach {
                    queue.add(step.next(it))
                }
            }
        }

        error("no path found, check input")
    }

    @Benchmark
    fun part1(): Int {
        val valley = Valley.fromInput(input)
        val start = Point(input.first().indexOfFirst { it == '.' }, 0)
        val exit = Point(input.last().indexOfFirst { it == '.' }, input.lastIndex)
        return solve(valley, start, ArrayDeque(listOf(exit)))
    }

    @Benchmark
    fun part2(): Int {
        val valley = Valley.fromInput(input)
        val start = Point(input.first().indexOfFirst { it == '.' }, 0)
        val exit = Point(input.last().indexOfFirst { it == '.' }, input.lastIndex)
        return solve(valley, start, ArrayDeque(listOf(exit, start, exit)))
    }
}

fun main() {
    val day24 = Day24()

    // test if implementation meets criteria from the description, like:
    day24.input = readInput("Day24_test")
    check(day24.part1() == 18)
    check(day24.part2() == 54)

    day24.input = readInput("Day24")
    println(day24.part1())
    println(day24.part2())
}