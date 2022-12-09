package adventofcode

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