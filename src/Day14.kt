fun main() {
    fun parse(input: List<String>): Pair<String, Map<String, Char>> {
        val i = input.first()
        val map = buildMap {
            input.drop(2).forEach { line ->
                val (a, b) = line.split(" -> ")
                set(a, b.toCharArray().first())
            }
        }
        return i to map
    }

    val cache = mutableMapOf<String, Map<Char, ULong>>()

    fun calcPair(key: String, n: Int, map: Map<String, Char>): Map<Char, ULong> {
        return cache.getOrPut(key + n.toString()) {
            if (n == 0) {
                if (key[0] == key[1]) {
                    return@getOrPut mutableMapOf(
                        key[0] to 2UL
                    )
                }
                return@getOrPut mutableMapOf(
                    key[0] to 1UL,
                    key[1] to 1UL
                )
            }
            val middle = map[key]!!
            val key1 = key[0].toString() + middle
            val key2 = middle + key[1].toString()
            val res1 = calcPair(key1, n - 1, map).toMutableMap()
            val res2 = calcPair(key2, n - 1, map)
            res2.forEach {
                res1[it.key] = (res1[it.key] ?: 0UL) + it.value
            }
            res1[middle] = res1[middle]!! - 1UL
            res1
        }
    }

    fun calc(input: List<String>, repeats: Int): ULong {
        val (pattern, map) = parse(input)

        val counts = mutableMapOf<Char, ULong>()
        for (i in 0..(pattern.length - 2)) {
            val subCounts = calcPair(
                key = pattern[i].toString() + pattern[i + 1],
                n = repeats,
                map = map,
            )
            subCounts.forEach { (c, cc) ->
                counts[c] = (counts[c] ?: 0UL) + cc
            }
            if (i != 0) {
                counts[pattern[i]] = counts[pattern[i]]!! - 1UL
            }
        }

        val min = counts.values.minOf { it }
        val max = counts.values.maxOf { it }
        return max - min
    }

    fun part1(input: List<String>): Int {
        return calc(input, repeats = 10).toInt()
    }

    fun part2(input: List<String>): ULong {
        return calc(input, repeats = 40)
    }

    val testInput = readInput("Day14_test")
    check(part1(testInput) == 1588)
    check(part2(testInput) == 2188189693529UL)

    cache.clear()

    val input = readInput("Day14")
    check(part1(input) == 3230)
    println(part1(input))
    println(part2(input))
}
