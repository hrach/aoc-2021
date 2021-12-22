@file:Suppress("NOTHING_TO_INLINE")

package day22

import readInput

data class Cube(
    val x: IntRange,
    val y: IntRange,
    val z: IntRange,
) {
    fun sum(): Long {
        return x.count().toLong() * y.count() * z.count()
    }
}

data class Command(
    val state: Boolean,
    val cube: Cube,
)

val regexp = """(on|off) x=(-?\d+)..(-?\d+),y=(-?\d+)..(-?\d+),z=(-?\d+)..(-?\d+)""".toRegex()

fun parse(input: List<String>): List<Command> =
    input.map { line ->
        val (state, xA, xB, yA, yB, zA, zB) = regexp.matchEntire(line)!!.destructured
        Command(
            state = state == "on",
            cube = Cube(
                x = xA.toInt()..xB.toInt(),
                y = yA.toInt()..yB.toInt(),
                z = zA.toInt()..zB.toInt(),
            )
        )
    }

inline fun IntRange.containsPartially(o: IntRange): Boolean =
    contains(o.first) || contains(o.last)

inline fun Cube.hasIntersection(o: Cube): Boolean =
    (x.containsPartially(o.x) || o.x.containsPartially(x)) &&
            (y.containsPartially(o.y) || o.y.containsPartially(y)) &&
            (z.containsPartially(o.z) || o.z.containsPartially(z))

fun Cube.remove(o: Cube): List<Cube> {
    if (!hasIntersection(o)) {
        return listOf(this)
    }

    val cubes = listOf(
        // left
        Cube(
            x = x.first..(o.x.first - 1).coerceAtMost(x.last),
            y = y,
            z = z,
        ),

        // middle back
        Cube(
            x = (o.x.first).coerceAtLeast(x.first)..o.x.last.coerceAtMost(x.last),
            y = y,
            z = (o.z.last + 1).coerceAtLeast(z.first)..z.last,
        ),

        // middle front
        Cube(
            x = o.x.first.coerceAtLeast(x.first)..o.x.last.coerceAtMost(x.last),
            y = y,
            z = z.first..(o.z.first - 1).coerceAtMost(z.last),
        ),

        // rest top
        Cube(
            x = o.x.first.coerceAtLeast(x.first)..o.x.last.coerceAtMost(x.last),
            y = (o.y.last + 1).coerceAtLeast(y.first)..y.last,
            z = o.z.first.coerceAtLeast(z.first)..o.z.last.coerceAtMost(z.last),
        ),

        // rest bottom
        Cube(
            x = o.x.first.coerceAtLeast(x.first)..o.x.last.coerceAtMost(x.last),
            y = y.first..(o.y.first - 1).coerceAtMost(y.last),
            z = o.z.first.coerceAtLeast(z.first)..o.z.last.coerceAtMost(z.last),
        ),

        // right
        Cube(
            x = (o.x.last + 1).coerceAtLeast(x.first)..x.last,
            y = y,
            z = z,
        ),
    ).filter { it.sum() != 0L }
    return cubes
}

fun List<Cube>.union(): List<Cube> {
    val results = mutableSetOf<Cube>()
    for (a in indices) {
        val cA = this[a]
        var result = setOf(cA)
        for (b in a + 1..indices.last) {
            val cB = this[b]
            result = result.flatMap {
                it.remove(cB)
            }.toSet()
        }
        results.addAll(result)
    }
    return results.toList()
}

fun calc(input: List<String>): Long {
    val commands = parse(input).toMutableList()

    var cubes = mutableListOf(commands.removeFirst().cube)

    while (commands.isNotEmpty()) {
        val command = commands.removeFirst()
        if (command.state) {
            cubes.add(command.cube)
        } else {
            cubes = cubes.flatMap { s ->
                s.remove(command.cube)
            }.toMutableList()
        }
    }

    return cubes.union().sumOf { it.sum() }
}

fun main() {
    fun part1(input: List<String>): Long {
        return calc(input)
    }

    fun part2(input: List<String>): Long {
        return calc(input)
    }

    check(
        part1(
            """
        on x=10..12,y=10..12,z=10..12
        on x=11..13,y=11..13,z=11..13
        off x=9..11,y=9..11,z=9..11
        on x=10..10,y=10..10,z=10..10
    """.trimIndent().lines()
        ) == 39L
    )

    val testInput = readInput("Day22_test")
    check(part1(testInput.dropLast(2)) == 590784L)

    val input = readInput("Day22")
    println(part1(input.take(20)))
    println(part2(input))
}
