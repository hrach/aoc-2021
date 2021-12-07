import kotlin.math.absoluteValue
import kotlin.math.sign

fun main() {
    fun part1(input: List<String>): Int {
        val numbers = input.first().split(',').map { it.toInt() }.sorted()
        val median = numbers[numbers.size / 2]
        val diffSum = numbers.fold(0) { acc, i ->
            (i - median).absoluteValue + acc
        }
        return diffSum
    }

    fun Int.weight(to: Int): Int {
        val distance = (this - to).absoluteValue
        return (distance * (distance + 1)) / 2
    }

    fun List<Int>.sum(target: Int): Int {
        return fold(0) { acc, i -> i.weight(target) + acc }
    }

    fun part2(input: List<String>): Int {
        val numbers = input.first().split(',').map { it.toInt() }.sorted()

        var target = numbers[numbers.size / 2]
        var lastSum = numbers.sum(target)

        while (true) {
            val numWithMaxWeight = numbers.maxByOrNull { it.weight(target) }!!
            target += (numWithMaxWeight - target).sign
            val sum = numbers.sum(target)
            if (sum > lastSum) {
                break
            }
            lastSum = sum
        }

        return lastSum
    }

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 37)
    check(part2(testInput) == 168)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
