package adventofcode

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import kotlin.math.sqrt

private operator fun IntRange.component1() = first

private operator fun IntRange.component2() = last

private operator fun List<CharArray>.contains(point: Point) = point.y in indices && point.x in this[point.y].indices

private operator fun List<CharArray>.get(point: Point) = this[point.y][point.x]

private enum class Facing(val value: Int, val move: Move) {
    RIGHT(0, Move(1, 0)),
    DOWN(1, Move(0, 1)),
    LEFT(2, Move(-1, 0)),
    UP(3, Move(0, -1));

    companion object {
        fun from(value: Int) = values().first { it.value == value }
    }
}

private data class Jump(val x: Int, val y: Int, val facing: Facing)

private class Board(
    val grid: Array<CharArray>,
    val jumps: Map<Jump, Point>
) {

    var facing = Facing.RIGHT
    var position = kotlin.run {
        val x = grid[0].indexOfFirst { it == '.' }
        val y = grid.indices.first { i -> grid[i][x] == '.' }
        Point(x, y)
    }

    fun move(steps: Int) {
        repeat(steps) {
            val nextPosition = (position + facing.move).let { jumps[Jump(it.x, it.y, facing)] ?: it }
            if (grid[nextPosition.y][nextPosition.x] != '#') {
                position = nextPosition
            } else {
                return
            }
        }
    }

    fun rotate(direction: Char) {
        facing = when (direction) {
            'L' -> if (facing == Facing.RIGHT) Facing.UP else Facing.from(facing.value - 1)
            'R' -> Facing.from((facing.value + 1) % Facing.values().size)
            else -> error("check input")
        }
    }

    companion object {
        fun fromInput(input: List<String>): Board {
            val m = input.size
            val n = input.maxOf { it.length }

            val xRanges = Array(m) { i ->
                input[i].indexOfFirst { it != ' ' } until input[i].length
            }

            val yRanges = Array(n) { j ->
                val min = (0 until m).indexOfFirst { i -> input[i].getOrElse(j) { ' ' } != ' ' }
                val max = (0 until m).indexOfLast { i -> input[i].getOrElse(j) { ' ' } != ' ' }
                min..max
            }

            val grid = Array(m) { CharArray(n) { ' ' } }
            for (i in 0 until m) {
                val line = input[i]
                for (j in line.indices) {
                    grid[i][j] = line[j]
                }
            }

            val jumps = buildMap {
                xRanges.forEachIndexed { y, (start, end) ->
                    put(Jump(start - 1, y, Facing.LEFT), Point(end, y))
                    put(Jump(end + 1, y, Facing.RIGHT), Point(start, y))
                }
                yRanges.forEachIndexed { x, (start, end) ->
                    put(Jump(x, start - 1, Facing.UP), Point(x, end))
                    put(Jump(x, end + 1, Facing.DOWN), Point(x, start))
                }
            }

            return Board(
                grid = grid,
                jumps = jumps
            )
        }
    }
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
class Day22 {

    var input: List<String> = emptyList()

    private val directions = arrayOf(
        Move(1, 0),
        Move(0, 1),
        Move(-1, 0),
        Move(0, -1)
    )

    @Setup
    fun setup() {
        input = readInput("Day22")
    }

    @Benchmark
    fun part1(): Int {
        val board = Board.fromInput(input.dropLast(2))

        val regex = Regex("""\d+|\w""")
        val matches = regex.findAll(input.last()).map { it.value }.toList()

        matches.forEachIndexed { index, value ->
            if (index % 2 == 0) {
                board.move(value.toInt())
            } else {
                board.rotate(value[0])
            }
        }

        val (x, y) = board.position
        return (y + 1) * 1_000 + (x + 1) * 4 + board.facing.value
    }

    @Benchmark
    fun part2(): Int {
        // 3D faces basis
        data class FaceVector(val i: Vector3, val j: Vector3, val n: Vector3)
        fun FaceVector.d2v(d: Int): Vector3 = i * directions[d].dy + j * directions[d].dx
        fun FaceVector.v2d(v: Vector3): Int = (0..3).single { d -> d2v(d) == v }

        data class Face(val i: Int, val j: Int)

        val totalTiles = input.dropLast(2).sumOf { row -> row.count { it != ' ' } }
        val faceSize = sqrt(totalTiles / 6.0).toInt()
        val faces = buildSet {
            input.dropLast(2).forEachIndexed { i, row ->
                row.forEachIndexed { j, char ->
                    if (char != ' ') add(Face(i / faceSize, j / faceSize))
                }
            }
        }

        // dfs on faces to construct folding
        val faceToVector = HashMap<Face, FaceVector>() // visited faces to vectors
        val n2Face = HashMap<Vector3, Face>() // normals to visited faces
        val faceToDirections = HashMap<Face, List<FaceVector>>() // neighbour faces in all directions

        fun dfs(face: Face, faceVector: FaceVector) {
            if (face in faceToVector) return
            faceToVector[face] = faceVector
            n2Face[faceVector.n] = face
            faceToDirections[face] = List(4) { d ->
                val (dj, di) = directions[d]
                val fd = Face(face.i + di, face.j + dj)
                val fvd = when (d) {
                    0 -> FaceVector(faceVector.i, faceVector.n, -faceVector.j)
                    1 -> FaceVector(faceVector.n, faceVector.j, -faceVector.i)
                    2 -> FaceVector(faceVector.i, -faceVector.n, faceVector.j)
                    3 -> FaceVector(-faceVector.n, faceVector.j, faceVector.i)
                    else -> error(d)
                }
                if (fd in faces) dfs(fd, fvd) // fold neighbour
                fvd
            }
        }

        val initialFace = faces.filter { it.i == 0 }.minBy { it.j } // initial face
        dfs(initialFace, FaceVector(Vector3(1, 0, 0), Vector3(0, 1, 0), Vector3(0, 0, 1)))

        val tiles = input.dropLast(2).map { it.toCharArray() }

        val regex = Regex("""\d+|\w""")
        val matches = regex.findAll(input.last()).map { it.value }.toList()

        var direction = 0
        var position = Point(initialFace.j * faceSize, 0)

        matches.forEachIndexed { index, value ->
            if (index % 2 == 0) {
                for (step in 1..value.toInt()) {
                    var nextPosition = position + directions[direction]
                    var nextDirection = direction

                    if (nextPosition !in tiles || tiles[nextPosition] == ' ') {
                        val currentFace = Face(position.y / faceSize, position.x / faceSize)
                        val faceVector = faceToDirections.getValue(currentFace)[direction] // basis of the next face if it was connected
                        val nextFace = n2Face[faceVector.n]!! // next face (look up by the normal)
                        val actualFaceVector = faceToVector[nextFace]!! // the actual basis of the new face
                        nextDirection = actualFaceVector.v2d(faceVector.d2v(direction)) // direction on the new face

                        // compute 3D vector of the offset on the face in the original basis
                        val vv = faceVector.i * (nextPosition.y.mod(faceSize) + 1) + faceVector.j * (nextPosition.x.mod(faceSize) + 1)

                        // project offset in the new basis
                        fun flipNeg(s: Int) = if (s < 0) faceSize + 1 + s else s
                        nextPosition = Point(
                            nextFace.j * faceSize + flipNeg(vv * actualFaceVector.j) - 1,
                            nextFace.i * faceSize + flipNeg(vv * actualFaceVector.i) - 1,
                        )
                    }

                    when (tiles[nextPosition]) {
                        '.' -> {
                            position = nextPosition
                            direction = nextDirection
                        }
                        '#' -> break
                        else -> error("check input")
                    }
                }
            } else {
                direction = when (value[0]) {
                    'L' -> (direction + 3) % 4
                    'R' -> (direction + 1) % 4
                    else -> error("check input")
                }
            }
        }


        return 1000 * (position.y + 1) + 4 * (position.x + 1) + direction
    }

}

fun main() {
    val day22 = Day22()

    // test if implementation meets criteria from the description, like:
    day22.input = readInput("Day22_test")
    check(day22.part1() == 6032)
    check(day22.part2() == 5031)

    day22.input = readInput("Day22")
    println(day22.part1())
    println(day22.part2())
}