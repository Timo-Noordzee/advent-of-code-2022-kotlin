package adventofcode

data class Point3D(val x: Int, val y: Int, val z: Int) {
    override fun toString() = "(x=$x, y=$y, z=$z)"
}

operator fun Point3D.plus(move: Move3D) = Point3D(
    x = x + move.dx,
    y = y + move.dy,
    z = z + move.dz
)

fun Point3D(input: String) = input.split(',').let {
    Point3D(it[0].toInt(), it[1].toInt(), it[2].toInt())
}