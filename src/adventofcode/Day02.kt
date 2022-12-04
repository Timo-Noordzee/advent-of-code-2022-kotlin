package adventofcode

import kotlin.system.measureTimeMillis

private operator fun String.component1() = this[0]
private operator fun String.component2() = this[1]
private operator fun String.component3() = this[2]

fun main() {

    fun part1(input: List<String>) = input.sumOf { (theirShape, _, yourShape) ->
        val shapeScore = yourShape - 'W'
        val outcome = (yourShape - theirShape - 1) % 3 // 0 = lost, 1 = draw, 2 = won
        shapeScore + outcome * 3
    }

    fun part2(input: List<String>) = input.sumOf { (theirShape, _, outcome) ->
        val resultScore = (outcome - 'X') * 3
        val shapeScore = (theirShape - 'A' + (outcome - 'V')) % 3 + 1
        resultScore + shapeScore
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val time = measureTimeMillis {
        val input = readInput("Day02")
        println(part1(input))
        println(part2(input))
    }
    println("finished in ${time}ms")
}