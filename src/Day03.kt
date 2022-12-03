import kotlin.system.measureTimeMillis

private operator fun String.component1() = subSequence(0, length / 2)
private operator fun String.component2() = subSequence(length / 2, length)

fun CharSequence.toLong() = this.fold(0L) { total, char -> total or (1L shl char - 'a') }

private infix fun CharSequence.and(s: CharSequence) = this.toLong() and s.toLong()

private infix fun Long.and(s: String) = this and s.toLong()

private fun Long.getPriority(): Int = countTrailingZeroBits().let { bits -> if (bits > 26) bits - 5 else bits + 1 }

fun main() {

    fun part1(input: List<String>): Int = input.sumOf { (firstCompartment, secondCompartment) ->
        val duplicate = firstCompartment and secondCompartment
        duplicate.getPriority()
    }

    fun part2(input: List<String>): Int = input.windowed(3, 3).sumOf { group ->
        val badge = group[0] and group[1] and group[2]
        badge.getPriority()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val time = measureTimeMillis {
        val input = readInput("Day03")
        println(part1(input))
        println(part2(input))
    }
    println("finished in ${time}ms")
}
