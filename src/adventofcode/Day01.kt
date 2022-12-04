package adventofcode

import java.util.*
import kotlin.system.measureTimeMillis

fun main() {

    fun getSumOfTopN(input: List<String>, n: Int): Int {
        var elfCount = 0
        val topElves = PriorityQueue<Int>(n + 1)

        // Add blank line to input so the last elf will be included too
        (input + "").forEach { line ->
            if (line.isBlank()) {
                topElves.add(elfCount)
                elfCount = 0

                if (topElves.size > n) {
                    topElves.poll()
                }
            } else {
                elfCount += line.toInt()
            }
        }

        return topElves.sum()
    }

    fun part1(input: List<String>): Int {
        return getSumOfTopN(input, 1)
    }

    fun part2(input: List<String>): Int {
        return getSumOfTopN(input, 3)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val time = measureTimeMillis {
        val input = readInput("Day01")
        println(part1(input))
        println(part2(input))
    }
    println("finished in ${time}ms")
}
