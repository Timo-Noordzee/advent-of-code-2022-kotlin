package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

private sealed interface Packet : Comparable<Packet> {

    class IntPacket(val value: Int) : Packet {
        override fun compareTo(other: Packet) = when (other) {
            is IntPacket -> value.compareTo(other.value)
            is ListPacket -> ListPacket(this).compareTo(other)
        }
    }

    class ListPacket(val packets: List<Packet>) : Packet {

        constructor(value: Packet) : this(listOf(value))

        val size get() = packets.size

        private operator fun get(i: Int) = packets[i]

        override fun compareTo(other: Packet): Int {
            val left = this
            val right = if (other is ListPacket) other else ListPacket(other)

            return when (other) {
                is IntPacket -> compareTo(ListPacket(listOf(other)))
                is ListPacket -> {
                    for (i in 0 until left.size.coerceAtMost(right.size)) {
                        val result = left[i].compareTo(right[i])
                        if (result != 0) return result
                    }
                    return left.size.compareTo(right.size)
                }
            }
        }
    }

    companion object {

        fun fromInput(input: String): Packet {
            val numberBuilder = StringBuilder(2)
            val stack = ArrayDeque<Packet?>()

            input.forEach { char ->
                when (char) {
                    '[' -> stack.add(null)
                    ',' -> {
                        if (numberBuilder.isNotEmpty()) {
                            stack.add(IntPacket(numberBuilder.toString().toInt()))
                            numberBuilder.clear()
                        }
                    }
                    ']' -> {
                        if (numberBuilder.isNotEmpty()) {
                            stack.add(IntPacket(numberBuilder.toString().toInt()))
                            numberBuilder.clear()
                        }

                        val subPackets = buildList {
                            var current = stack.removeLast()
                            while (current != null) {
                                add(0, current)
                                current = stack.removeLast()
                            }
                        }

                        stack.add(ListPacket(subPackets))
                    }
                    else -> numberBuilder.append(char)
                }
            }

            return checkNotNull(stack.single())
        }
    }
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day13 {

    var input: List<String> = emptyList()

    @Setup
    fun setup() {
        input = readInput("Day13")
    }

    private fun parseInput() = input.filter { it.isNotEmpty() }.map { Packet.fromInput(it) }

    @Benchmark
    fun part1(): Int = parseInput().chunked(2).withIndex().sumOf { (index, pair) ->
        val (left, right) = pair
        val result = left.compareTo(right)
        if (result == -1) index + 1 else 0
    }

    @Benchmark
    fun part2(): Int {
        val divider1 = Packet.ListPacket(Packet.ListPacket(Packet.IntPacket(2)))
        val divider2 = Packet.ListPacket(Packet.ListPacket(Packet.IntPacket(6)))
        val packets = (parseInput() + listOf(divider1, divider2)).sorted()
        return (packets.binarySearch(divider1) + 1) * (packets.binarySearch(divider2) + 1)
    }

}

fun main() {
    val day13 = Day13()

    // test if implementation meets criteria from the description, like:
    day13.input = readInput("Day13_test")
    check(day13.part1() == 13)
    check(day13.part2() == 140)

    day13.input = readInput("Day13")
    println(day13.part1())
    println(day13.part2())

}