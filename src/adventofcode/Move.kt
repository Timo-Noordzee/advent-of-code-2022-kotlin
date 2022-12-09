package adventofcode

import kotlin.math.abs

class Move(val dx: Int, val dy: Int) {
    override fun toString() = "(dx=$dx, dy=$dy)"
}

/**
 * The Chebyshev distance (chessboard distance).
 * Both a cardinal move (dx != 0 || dy != 0) and an ordinal direction move (both dx != 0 && dy != 0) have a distance of 1
 */
val Move.chebyshevDistance: Int get() = maxOf(abs(dx), abs(dy))