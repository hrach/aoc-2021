fun main() {

    data class Row1(
        val input: List<String>,
        val output: List<String>,
    )

    fun parse1(input: List<String>): List<Row1> {
        return input.map { row ->
            val (i, o) = row.split(" | ")
            Row1(
                input = i.split(' '),
                output = o.split(' '),
            )
        }
    }

    fun part1(input: List<String>): Int {
        val rows = parse1(input)
        return rows.sumOf {
            it.output.count { segment ->
                segment.length in listOf(2, 4, 3, 7)
            }
        }
    }

    // =============================================================================================

    data class Row2(
        val input: List<Int>,
        val output: List<Int>,
    )

    fun toBin(char: Char): Int {
        return when (char) {
            'a' -> 0b1000000
            'b' -> 0b0100000
            'c' -> 0b0010000
            'd' -> 0b0001000
            'e' -> 0b0000100
            'f' -> 0b0000010
            'g' -> 0b0000001
            else -> error(char)
        }
    }

    fun findByLen(inputs: List<Int>, len: Int): List<Int> =
        inputs.filter { it.countOneBits() == len }

    fun resolve(inputs: List<Int>): List<Int> {
        val number1 = findByLen(inputs, 2).first()
        val number4 = findByLen(inputs, 4).first()
        val number7 = findByLen(inputs, 3).first()
        val number8 = findByLen(inputs, 7).first()

        val sCF = number1
        val sBD = number4 xor number1

        val number3 = findByLen(inputs, 5).first { it and sCF == sCF }
        val sD = sBD and number3
        val sB = sBD - sD
        val number5 = findByLen(inputs, 5).first { it and sB == sB }

        val sF = sCF and number5
        val sE = number8 - number3 - sB
        val number2 = number3 - sF + sE
        val number9 = number3 or number4
        val number6 = number5 + sE
        val number0 = number8 - sD

        return listOf(
            number0,
            number1,
            number2,
            number3,
            number4,
            number5,
            number6,
            number7,
            number8,
            number9,
        )
    }

    fun parse2(input: List<String>): List<Row2> {
        return input.map { row ->
            val (i, o) = row.split(" | ")
            Row2(
                input = i.split(' ').map { it.map(::toBin).reduce { acc, i -> acc or i } },
                output = o.split(' ').map { it.map(::toBin).reduce { acc, i -> acc or i } },
            )
        }
    }

    fun resolveDisplay(row: Row2): Int {
        val numbers = resolve(row.input)
        val result = row.output.map { i ->
            numbers.indexOf(i)
        }.joinToString("").toInt()
        return result
    }

    fun part2(input: List<String>): Int {
        val rows = parse2(input)
        return rows.sumOf { row -> resolveDisplay(row) }
    }

    val testInput = readInput("Day08_test")
    check(part1(testInput) == 26)
    check(part2(testInput) == 61229)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
