package adventofcode

import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 * Represents a point in 2D space
 */
data class Point(val x: Int, val y: Int) {
    override fun toString() = "(x=$x, y=$y)"
}

/**
 * @return a [Move] which can be used to move from this to the target [Point]
 */
infix fun Point.getMoveTo(target: Point) = Move(x - target.x, y - target.y)

/**
 * @return a new [Point] after the [Move] has been applied
 */
operator fun Point.plus(move: Move) = copy(
    x = x + move.dx,
    y = y + move.dy
)

operator fun Point.minus(move: Move) = copy(
    x = x - move.dx,
    y = y + move.dy
)

/**
 * Calculate the index of a point in a grid of size m * n
 *
 * @param n the number of columns in the grid
 */
fun Point.getIndex(n: Int) = y * n + x

fun Point.getNeighbors() = listOf(
    Point(x + 1, y),
    Point(x - 1, y),
    Point(x, y + 1),
    Point(x, y - 1),
)

infix fun Point.atSamePositionAs(other: Point) = x == other.x && y == other.y

infix fun Point.lineTo(other: Point): List<Point> {
    val xDelta = (other.x - x).sign
    val yDelta = (other.y - y).sign
    val steps = maxOf((x - other.x).absoluteValue, (y - other.y).absoluteValue)
    return (1..steps).scan(this) { previous, _ -> Point(previous.x + xDelta, previous.y + yDelta) }
}

val Point.left get() = Point(x - 1, y)

val Point.right get() = Point(x + 1, y)

// Convert "x,y" to Point
fun Point(xAndY: String) = xAndY.split(',').let { Point(it[0].toInt(), it[1].toInt()) }