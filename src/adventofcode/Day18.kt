package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

private operator fun <E> List<E>.component6() = this[5]

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day18 {

    var input: List<String> = emptyList()

    private val directions = listOf(
        Move3D(1, 0, 0),
        Move3D(-1, 0, 0),
        Move3D(0, 0, 1),
        Move3D(0, 0, -1),
        Move3D(0, 1, 0),
        Move3D(0, -1, 0),
    )

    @Setup
    fun setup() {
        input = readInput("Day18")
    }

    @Benchmark
    fun part1(): Int {
        val droplets = buildSet { input.forEach { add(Point3D(it)) } }
        return droplets.fold(0) { surfaceArea, point ->
            surfaceArea + directions.count { point + it !in droplets }
        }
    }

    @Benchmark
    fun part2(): Int {
        var (minX, maxX, minY, maxY, minZ, maxZ) = listOf(0, 0, 0, 0, 0, 0)
        val droplets = buildSet {
            input.forEach {
                val point = Point3D(it)
                minX = minX.coerceAtMost(point.x)
                maxX = maxX.coerceAtLeast(point.x)
                minY = minY.coerceAtMost(point.y)
                maxY = maxY.coerceAtLeast(point.y)
                minZ = minZ.coerceAtMost(point.z)
                maxZ = maxZ.coerceAtLeast(point.z)
                add(point)
            }
        }

        val xRange = (minX - 1)..(maxX + 1)
        val yRange = (minY - 1)..(maxY + 1)
        val zRange = (minZ - 1)..(maxZ + 1)
        val queue = ArrayDeque(xRange.flatMap { x -> zRange.map { z -> Point3D(x, maxY + 1, z) } })

        val air = buildSet {
            while (queue.isNotEmpty()) {
                val point = queue.removeFirst()
                if (add(point)) {
                    directions.map { point + it }.forEach { neighbor ->
                        if (neighbor !in droplets && neighbor.x in xRange && neighbor.y in yRange && neighbor.z in zRange) {
                            queue.add(neighbor)
                        }
                    }
                }
            }
        }

        return droplets.fold(0) { surfaceArea, point ->
            surfaceArea + directions.count { point + it in air }
        }
    }
}

fun main() {
    val day18 = Day18()

    // test if implementation meets criteria from the description, like:
    day18.input = readInput("Day18_test")
    check(day18.part1() == 64)
    check(day18.part2() == 58)

    day18.input = readInput("Day18")
    println(day18.part1())
    println(day18.part2())
}