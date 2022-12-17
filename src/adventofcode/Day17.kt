package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

private infix fun Rock.collidesWith(rocks: Set<Point>): Boolean = this.rocks.any { it in rocks }

class Rock(
    val shapeId: Int,
    origin: Point,
    moves: List<Move>
) {
    private var previousMove: Move = Move(0, 0)
    private var previous = moves.map { move -> origin + move }

    var rocks = previous
    var leftBound = rocks.minOf { it.x }
    var rightBound = rocks.maxOf { it.x }

    private fun move(move: Move): List<Point> {
        previousMove = move
        previous = rocks
        rocks = rocks.map { it + move }
        leftBound += move.dx
        rightBound += move.dx
        return rocks
    }

    fun moveLeft() = move(Move(-1, 0))

    fun moveRight() = move(Move(1, 0))

    fun moveDown() = move(Move(0, -1))

    fun revert() {
        rocks = previous
        leftBound -= previousMove.dx
        rightBound -= previousMove.dx
    }
}

class RockFactory {
    private var index = 0
    fun next(height: Int): Rock {
        val origin = Point(2, height)
        return when (index) {
            0 -> Rock(index, origin, listOf(Move(0, 0), Move(1, 0), Move(2, 0), Move(3, 0)))
            1 -> Rock(index, origin, listOf(Move(0, 1), Move(1, 0), Move(1, 1), Move(1, 2), Move(2, 1)))
            2 -> Rock(index, origin, listOf(Move(0, 0), Move(1, 0), Move(2, 0), Move(2, 1), Move(2, 2)))
            3 -> Rock(index, origin, listOf(Move(0, 0), Move(0, 1), Move(0, 2), Move(0, 3)))
            4 -> Rock(index, origin, listOf(Move(0, 0), Move(0, 1), Move(1, 0), Move(1, 1)))
            else -> error("no rock found")
        }.also { index = (index + 1) % 5 }
    }
}

class JetIterator(private val input: String) {
    var index = 0
    fun next(): Char {
        return input[index].also {
            index = (index + 1) % input.length
        }
    }
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day17 {

    var input: String = ""
    private val rocks = mutableSetOf<Point>()

    @Setup
    fun setup() {
        input = readInput("Day17").first()
    }

    private fun dropRock(rock: Rock, jetIterator: JetIterator): Int {
        // Keep performing moves as long as rock can fall down
        while (true) {
            // Move rock either left or right depending on next jet in input
            when (jetIterator.next()) {
                '>' -> rock.moveRight()
                '<' -> rock.moveLeft()
            }

            // If rock formation collides with another rock formation, undo move
            if (rock.leftBound < 0 || rock.rightBound > 6 || rock collidesWith rocks) rock.revert()

            rock.moveDown()

            // Rock collided with another rock (or floor), so it can't move down any further
            if (rock collidesWith rocks) {
                // Undo moving down and break from loop, rock is now at lowest possible position
                rock.revert()
                break
            }
        }

        rocks.addAll(rock.rocks)
        return rock.rocks.maxOf { it.y }
    }

    fun solve(totalRounds: Long): Long {
        rocks.clear()
        rocks.addAll((0..6).map { x -> Point(x, 0) })

        val rockFactory = RockFactory()
        val jetIterator = JetIterator(input)

        var currentRound = 0L
        var highestPoint = 0

        var patternStartKey = ""
        var patternLength = 0
        var heightAtPatternStart = 0
        val patterns = mutableSetOf<String>()

        while (currentRound < totalRounds) {
            val rock = rockFactory.next(highestPoint + 4)
            val heightDiff = (dropRock(rock, jetIterator) - highestPoint).coerceAtLeast(0)
            highestPoint += heightDiff

            currentRound++

            // Combine type of rock, index in input and height difference to key.
            // The key is used to detect a repeating pattern
            val patternKey = "${rock.shapeId}-${jetIterator.index}-$heightDiff"

            // If the key hasn't been seen before, it can't be part of the current pattern
            // Add unknown key to list and reset pattern streak
            if (patternKey !in patterns) {
                patternLength = 0
                patterns.add(patternKey)
                continue
            }

            if (patternLength == 0) {
                // The current key marks the start of a possible pattern (previous key was unknown).
                // The current highestPoint is saved so the height gain for the pattern can be calculated later.
                patternStartKey = patternKey
                heightAtPatternStart = highestPoint
            } else if (patternKey == patternStartKey) {
                val heightGain = highestPoint - heightAtPatternStart

                // Perform x rounds until the remaining amount of rounds can be divided by the pattern length.
                // For example, if the pattern length is 5 with 23 rounds remaining drop 3 more rocks
                // 23 can't be divided by 5, but 20 (23 - 3) can.
                repeat(((totalRounds - currentRound) % patternLength).toInt()) {
                    val nextRock = rockFactory.next(highestPoint + 4)
                    highestPoint += (dropRock(nextRock, jetIterator) - highestPoint).coerceAtLeast(0)
                    currentRound++
                }

                val remainingRounds = totalRounds - currentRound
                return highestPoint + (remainingRounds / patternLength) * heightGain
            }

            patternLength++
        }

        return highestPoint.toLong()
    }

    @Benchmark
    fun part1() = solve(2022)

    @Benchmark
    fun part2() = solve(1_000_000_000_000)

}

fun main() {
    val day17 = Day17()

    // test if implementation meets criteria from the description, like:
    day17.input = readInput("Day17_test").first()
    check(day17.part1() == 3068L)
    check(day17.part2() == 1514285714288L)

    day17.input = readInput("Day17").first()
    println(day17.part1())
    println(day17.part2())
}