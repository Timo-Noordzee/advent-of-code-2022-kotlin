package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.absoluteValue

private operator fun List<MatchResult>.component1() = this[0].groupValues[1].toInt()
private operator fun List<MatchResult>.component2() = this[1].groupValues[2].toInt()
private operator fun List<MatchResult>.component3() = this[2].groupValues[1].toInt()
private operator fun List<MatchResult>.component4() = this[3].groupValues[2].toInt()

data class Sensor(
    val x: Int,
    val y: Int,
    val beacon: Point
) {

    private val range = (x - beacon.x).absoluteValue + (y - beacon.y).absoluteValue

    val yRange = (this.y - range)..(this.y + range)

    fun xRangeAt(y: Int): IntRange {
        val yDelta = abs(this.y - y)
        return if (yDelta > range) 0..0 else {
            val halfWidth = range - yDelta
            (this.x - halfWidth)..(this.x + halfWidth)
        }
    }

}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day15 {

    var targetY: Int = 2_000_000
    var max: Int = 40_000_000
    var input: List<String> = emptyList()

    @Setup
    fun setup() {
        input = readInput("Day15")
        targetY = 2_000_000
        max = 4_000_000
    }

    fun parseSensors(): List<Sensor> {
        val regex = Regex("""x=(-?\d+)|y=(-?\d+)""")
        return input.map { line ->
            val (sensorX, sensorY, beaconX, beaconY) = regex.findAll(line).toList()
            Sensor(sensorX, sensorY, Point(beaconX, beaconY))
        }
    }

    @Benchmark
    fun part1(): Int {
        val sensors = parseSensors()

        val numOfBeaconsAtTargetY = sensors.map { it.beacon }.toSet().count { it.y == targetY }
        val impossiblePositions = sensors
            .filter { targetY in it.yRange }
            .map { it.xRangeAt(targetY) }
            .merge()
            .sumOf { it.size }

        return impossiblePositions - numOfBeaconsAtTargetY
    }

    @Benchmark
    fun part2(): Long {
        val sensors = parseSensors()
        for (y in 0..max) {
            val mergedRanges = sensors.filter { y in it.yRange }.map { it.xRangeAt(y) }.merge()
            if (mergedRanges.size > 1) {
                return (mergedRanges.first().last + 1) * 4_000_000L + y
            }
        }
        error("check input")
    }
}

fun main() {
    val day15 = Day15()

    // test if implementation meets criteria from the description, like:
    day15.input = readInput("Day15_test")
    day15.targetY = 10
    day15.max = 20
    check(day15.part1() == 26)
    check(day15.part2() == 56000011L)

    day15.input = readInput("Day15")
    day15.targetY = 2_000_000
    day15.max = 4_000_000
    println(day15.part1())
    println(day15.part2())
}