package day15

import readInput
import java.util.*

data class Node(
    val x: Int,
    val y: Int,
    val dist: Int,
)

fun djk(input: List<String>): Int {
    val ll = input.size
    fun k(x: Int, y: Int): Int = y * ll + x

    val queue = PriorityQueue<Node>(compareBy { it.dist })
    val distances = mutableMapOf<Int, Int>()

    val prices = input.mapIndexed { y, row ->
        row.mapIndexed { x, c ->
            val dist = if (x == 0 && y == 0) 0 else 10000
            distances[k(x, y)] = dist
            queue.add(Node(x, y, dist))
            c.toString().toInt()
        }
    }
    val endK = k(prices.size - 1, prices.size - 1)

    fun related(node: Node, prices: List<List<Int>>): List<Pair<Int, Int>> {
        val x = node.x
        val y = node.y
        val l = prices.size - 1
        return listOfNotNull(
            if (x > 1) Pair(x - 1, y) else null,
            if (y > 1) Pair(x, y - 1) else null,
            if (x < l) Pair(x + 1, y) else null,
            if (y < l) Pair(x, y + 1) else null
        )
    }

    e@ while (queue.isNotEmpty()) {
        val u = queue.remove()
        for ((x, y) in related(u, prices)) {
            val newDist = u.dist + prices[y][x]
            val k = k(x, y)
            if (newDist < distances[k]!!) {
                val n = Node(x, y, distances[k]!!)
                distances[k] = newDist
                check(queue.remove(n))
                queue.add(n.copy(dist = newDist))
                if (k == endK) {
                    break@e
                }
            }
        }
    }

//    for (x in 0 until ll) {
//        for (y in 0 until ll) {
//            print(distances[k(x, y)].toString() + ",")
//        }
//        print("\n")
//    }

    return distances[k(prices.size - 1, prices.size - 1)]!!
}

fun main() {
    fun part1(input: List<String>): Int {
        return djk(input)
    }

    fun part2(input: List<String>): Int {
        val input2 = mutableListOf<List<Int>>()
        input.forEach {
            val nums = it.toCharArray().map { it.toString().toInt() }
            val row = nums.toMutableList()
            repeat(4) { i ->
                row += nums.map {
                    val n = it + i + 1
                    when {
                        n > 9 -> n - 9
                        else -> n
                    }
                }
            }
            input2.add(row)
        }
        val rows = input2.toList()
        repeat(4) { i ->
            rows.forEach { row ->
                val newRow = row.map {
                    val n = it + i + 1
                    when {
                        n > 9 -> n - 9
                        else -> n
                    }
                }
                input2.add(newRow)
            }
        }
        val newData = input2.map { it.joinToString("") }
        return djk(newData)
    }

    val testInput = readInput("Day15_test")
    check(part1(testInput) == 40)
    check(part2(testInput) == 315)

    val input = readInput("Day15")
    println(part1(input))
    println(part2(input))
}
