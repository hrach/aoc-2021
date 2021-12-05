import kotlin.math.max
import kotlin.math.min

fun main() {
    data class Coordinate(val x: Int, val y: Int)

    fun parseCoordinates(line: String): Pair<Coordinate, Coordinate> {
        val (start, end) = line.split(" -> ")
        val (sX, sY) = start.split(',').map { it.toInt() }
        val (eX, eY) = end.split(',').map { it.toInt() }
        return Coordinate(sX, sY) to Coordinate(eX, eY)
    }

    fun createLine(start: Coordinate, end: Coordinate, allowDiagonal: Boolean = false): List<Coordinate> {
        val minX = min(start.x, end.x)
        val minY = min(start.y, end.y)
        val maxX = max(start.x, end.x)
        val maxY = max(start.y, end.y)

        return when {
            minX == maxX -> {
                val range = minY..maxY
                range.zip(List(range.count()) { maxX }).map { Coordinate(it.second, it.first) }
            }
            minY == maxY -> {
                val range = minX..maxX
                range.zip(List(range.count()) { maxY }).map { Coordinate(it.first, it.second) }
            }
            allowDiagonal && ((maxX - minX) == (maxY - minY)) -> {
                val rangeX = if (start.x <= end.x) start.x..end.x else start.x downTo end.x
                val rangeY = if (start.y <= end.y) start.y..end.y else start.y downTo end.y
                rangeX.zip(rangeY).map { Coordinate(it.first, it.second) }
            }
            else -> emptyList()
        }
    }

    fun part1(input: List<String>): Int {
        val lines = input.map { line ->
            val (start, end) = parseCoordinates(line)
            createLine(start, end)
        }
        val numbers = lines.flatten()
        val grid = numbers.groupBy { it }
        return grid.count { it.value.size >= 2 }
    }

    fun part2(input: List<String>): Int {
        val lines = input.map { line ->
            val (start, end) = parseCoordinates(line)
            createLine(start, end, allowDiagonal = true)
        }
        val numbers = lines.flatten()
        val grid = numbers.groupBy { it }
        return grid.count { it.value.size >= 2 }
    }

    val testInput = readInput("Day05_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 12)

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
