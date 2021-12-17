package day17

import kotlin.math.absoluteValue

fun isMatch(velX: Int, velY: Int, targetX: IntRange, targetY: IntRange): Int? {
    var dx = velX
    var dy = velY
    var x = 0
    var y = 0
    var maxY = 0

    while ((x < targetX.last) && (y > targetY.first)) {
        x += dx
        y += dy
        maxY = maxOf(maxY, y)
        dx = (dx - 1).coerceAtLeast(0)
        dy -= 1

        if (x in targetX && y in targetY) {
            return maxY
        }
    }

    return null
}

fun calc(targetX: IntRange, targetY: IntRange): Map<Pair<Int, Int>, Int> {
    val maxX = targetX.last
    val minY = targetY.first

    val velXMin = 0
    val velXMax = maxX

    val velYMin = minY
    val velYMax = minY.absoluteValue + 1

    val results = mutableMapOf<Pair<Int, Int>, Int>()

    for (testX in velXMin..velXMax) {
        for (testY in velYMin..velYMax) {
            val foundMaxY = isMatch(testX, testY, targetX, targetY) ?: continue
            results[testX to testY] = foundMaxY
        }
    }

    return results
}

fun main() {
    fun part1(targetX: IntRange, targetY: IntRange): Int {
        val results = calc(targetX, targetY)
        return results.maxOf { it.value }
    }

    fun part2(targetX: IntRange, targetY: IntRange): Int {
        val results = calc(targetX, targetY)
        return results.count()
    }

    check(part1(20..30, -10..-5) == 45)
    check(part2(20..30, -10..-5) == 112)

    println(part1(211..232, -124..-69))
    println(part2(211..232, -124..-69))
}
