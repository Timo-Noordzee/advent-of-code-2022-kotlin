package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day07 {

    var input: List<String> = emptyList()

    @Setup
    fun setup() {
        input = readInput("Day07")
    }

    fun parseInput(): Map<String, Int> = buildMap {
        put("", 0)
        val path = ArrayDeque<String>()
        input.forEach { line ->
            if (line.startsWith("$ cd")) {
                when (val dir = line.split(" ")[2]) {
                    "/" -> path.clear()
                    ".." -> path.removeLast()
                    else -> if (path.isEmpty()) path.add(dir) else path.add("${path.last()}/$dir")
                }
            } else if (!line.startsWith("$") && line[0].isDigit()) {
                val size = line.substringBefore(" ").toInt()
                path.forEach { subPath ->
                    put(subPath, getOrElse(subPath) { 0 } + size)
                }
                this[""] = getValue("") + size
            }
        }
    }

    @Benchmark
    fun part1(): Int = parseInput().values.sumOf { size -> if (size <= 100_000) size else 0 }

    @Benchmark
    fun part2(): Int {
        val sizes = parseInput()
        val missingSpace = 30_000_000 - (70_000_000 - sizes.getValue(""))
        return sizes.values.minOf { size -> if (size >= missingSpace) size else Int.MAX_VALUE }
    }
}

fun main() {
    val day07 = Day07()

    // test if implementation meets criteria from the description, like:
    day07.input = readInput("Day07_test")
    check(day07.part1() == 95437)
    check(day07.part2() == 24933642)

    day07.input = readInput("Day07")
    println(day07.part1())
    println(day07.part2())
}