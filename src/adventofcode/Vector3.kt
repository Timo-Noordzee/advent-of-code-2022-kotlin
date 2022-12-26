package adventofcode

data class Vector3(val i:Int, val j:Int, val k:Int)

operator fun Vector3.unaryMinus() = Vector3(-i, -j, -k)
operator fun Vector3.plus(v: Vector3) = Vector3(i + v.i, j + v.j, k + v.k)
operator fun Vector3.times(x: Int) = Vector3(i * x, j * x, k * x)
operator fun Vector3.times(v: Vector3): Int = i * v.i + j * v.j + k * v.k