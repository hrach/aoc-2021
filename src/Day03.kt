fun main() {
    fun part1(input: List<String>): Int {
        val sums = input.fold(Array(input.first().length) { 0 }) { acc, line ->
            line.forEachIndexed { index, char ->
                when (char) {
                    '1' -> acc[index] += 1
                }
            }
            acc
        }

        val threshold = input.size / 2
        val gamma = sums.joinToString("") { if (it > threshold) "1" else "0" }.toInt(radix = 2)
        val epsilon = sums.joinToString("") { if (it > threshold) "0" else "1" }.toInt(radix = 2)
        return gamma * epsilon
    }

    fun part2(input: List<String>): Int {
        fun sum(data: List<String>, offset: Int): Int {
            return data.count { it[offset] == '1' }
        }
        fun oxygenBit(data: List<String>, offset: Int): Char {
            val sum = sum(data, offset)
            val threshold = data.size / 2f
            return if (sum >= threshold) '1' else '0'
        }
        fun co2Bit(data: List<String>, offset: Int): Char {
            val sum = sum(data, offset)
            val threshold = data.size / 2f
            return if (sum >= threshold) '0' else '1'
        }

        val oxygenList = input.toMutableList()
        val co2List = input.toMutableList()

        var i = 0
        while (oxygenList.size > 1) {
            val oxygenBit = oxygenBit(oxygenList, i)
            oxygenList.removeIf { line ->
                line[i] != oxygenBit
            }
            i += 1
        }

        var j = 0
        while (co2List.size > 1) {
            val co2Bit = co2Bit(co2List, j)
            co2List.removeIf { line ->
                line[j] != co2Bit
            }
            j += 1
        }

        return oxygenList.first().toInt(radix = 2) * co2List.first().toInt(radix = 2)
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 198)
    check(part2(testInput) == 230)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
