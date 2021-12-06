fun main() {
    fun calc(input: List<String>, repeats: Int): Long {
        val numbers = input.first().split(',').map { it.toInt() }

        var actual: MutableMap<Int, Long> = numbers.groupBy { it }.mapValues { it.value.size.toLong() }.toMutableMap()

        repeat(repeats) {
            val toReborn = actual[0] ?: 0
            actual.remove(0)
            actual = actual.mapKeys { (key) ->
                key - 1
            }.toMutableMap()
            if (toReborn != 0L) {
                actual[6] = actual[6]?.plus(toReborn) ?: toReborn
                actual[8] = actual[8]?.plus(toReborn) ?: toReborn
            }
        }

        return actual.values.sum()
    }

    fun part1(input: List<String>): Long {
        return calc(input, repeats = 80)
    }

    fun part2(input: List<String>): Long {
        return calc(input, repeats = 256)
    }

    val testInput = readInput("Day06_test")
    check(part1(testInput) == 5934L)
    check(part2(testInput) == 26984457539)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
