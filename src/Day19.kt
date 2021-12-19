package day19

import readInput
import kotlin.math.absoluteValue

data class Probe(
    val a: Int,
    val b: Int,
    val c: Int,
) {
    fun distanceTo(o: Probe): Int {
        return (a - o.a).absoluteValue + (b - o.b).absoluteValue + (c - o.c).absoluteValue
    }
}

fun rotations(scanner: Scanner): Sequence<Scanner> = sequence {
    // https://stackoverflow.com/a/50546727/859688
    // 1.  I
    // 2.  X
    // 3.  Y

    // 4.  XX = YXXY
    // 5.  XY
    // 6.  YX
    // 7.  YY = XYYX

    // 8.  XXX = XYXXY = YXXYX = YXYXY = YYXYY
    // 9.  XXY = YXXYY = YYYXX
    // 10. XYX = YXY
    // 11. XYY = XXYYX = YYXXX
    // 12. YXX = XXYYY = YYXXY
    // 13. YYX = XXXYY = XYYXX
    // 14. YYY = XXYXX = XYXYX = XYYXY = YXYYX

    // 15. XXXY
    // 16. XXYX = XYXY = YXYY
    // 17. XXYY = YYXX
    // 18. XYXX = YXYX = YYXY
    // 19. XYYY
    // 20. YXXX
    // 21. YYYX

    // 22. XXXYX = XXYXY = XYXYY = YXYYY
    // 23. XYXXX = YXYXX = YYXYX = YYYXY
    // 24. XYYYX = YXXXY

    fun Probe.rotX(): Probe = copy(b = -c, c = b)
    fun Probe.rotY(): Probe = copy(a = -c, c = a)

    yield(scanner)

    yield(scanner.copy(probes = scanner.probes.map { it.rotX() }))
    yield(scanner.copy(probes = scanner.probes.map { it.rotY() }))

    yield(scanner.copy(probes = scanner.probes.map { it.rotX().rotX() }))
    yield(scanner.copy(probes = scanner.probes.map { it.rotX().rotY() }))
    yield(scanner.copy(probes = scanner.probes.map { it.rotY().rotX() }))
    yield(scanner.copy(probes = scanner.probes.map { it.rotY().rotY() }))

    yield(scanner.copy(probes = scanner.probes.map { it.rotX().rotX().rotX() }))
    yield(scanner.copy(probes = scanner.probes.map { it.rotX().rotX().rotY() }))
    yield(scanner.copy(probes = scanner.probes.map { it.rotX().rotY().rotX() }))
    yield(scanner.copy(probes = scanner.probes.map { it.rotX().rotY().rotY() }))
    yield(scanner.copy(probes = scanner.probes.map { it.rotY().rotX().rotX() }))
    yield(scanner.copy(probes = scanner.probes.map { it.rotY().rotY().rotX() }))
    yield(scanner.copy(probes = scanner.probes.map { it.rotY().rotY().rotY() }))

    yield(scanner.copy(probes = scanner.probes.map { it.rotX().rotX().rotX().rotY() }))
    yield(scanner.copy(probes = scanner.probes.map { it.rotX().rotX().rotY().rotX() }))
    yield(scanner.copy(probes = scanner.probes.map { it.rotX().rotX().rotY().rotY() }))
    yield(scanner.copy(probes = scanner.probes.map { it.rotX().rotY().rotX().rotX() }))
    yield(scanner.copy(probes = scanner.probes.map { it.rotX().rotY().rotY().rotY() }))
    yield(scanner.copy(probes = scanner.probes.map { it.rotY().rotX().rotX().rotX() }))
    yield(scanner.copy(probes = scanner.probes.map { it.rotY().rotY().rotY().rotX() }))

    yield(scanner.copy(probes = scanner.probes.map { it.rotX().rotX().rotX().rotY().rotX() }))
    yield(scanner.copy(probes = scanner.probes.map { it.rotX().rotY().rotX().rotX().rotX() }))
    yield(scanner.copy(probes = scanner.probes.map { it.rotX().rotY().rotY().rotY().rotX() }))
}

fun shift(source: Probe, scanner: Scanner, j: Int): Scanner {
    val target = scanner.probes[j]
    val dA = source.a - target.a
    val dB = source.b - target.b
    val dC = source.c - target.c
    return scanner.copy(
        probes = scanner.probes.map { it.copy(a = it.a + dA, b = it.b + dB, c = it.c + dC) },
        shift = Probe(dA, dB, dC),
    )
}

data class Scanner(
    val probes: List<Probe>,
    val shift: Probe = Probe(0, 0, 0),
) {
    private val probesSet: Set<Probe> by lazy { probes.toSet() }

    fun match(other: Scanner): Scanner? {
        for (i in probes.indices) {
            for (rotated in rotations(other)) {
                for (j in rotated.probes.indices) {
                    val shifted = shift(probes[i], rotated, j)
                    var matched = 0
                    for (probe in probes) {
                        if (shifted.probesSet.contains(probe)) {
                            matched += 1
                            if (matched == 12) {
                                return shifted
                            }
                        }
                    }
                }
            }
        }
        return null
    }
}

fun sync(list: List<Scanner>): List<Scanner> = buildList {
    val input = list.toMutableList()
    val first = input.removeFirst()
    add(first)

    loop@ while (input.isNotEmpty()) {
        for (next in input) {
            for (master in this) {
                val match = master.match(next)
                if (match != null) {
                    add(match)
                    input.remove(next)
                    continue@loop
                }
            }
        }
        error("no match")
    }
}


//  This algo is a faster a lot, but doesn't calculate needed scanners for part 2.

//    val input = list.toMutableList()
//    val scanners = mutableListOf<Scanner>()

//
//    loop@ while (input.size != 1) {
//        for (i in input.indices) {
//            for (j in input.indices) {
//                val a = input[i]
//                val b = input[j]
//                if (a == b) continue
//
//                val match = a.match(b)
//                if (match != null) {
//                    input.remove(a)
//                    input.remove(b)
//                    input.add(
//                        Scanner(a.probes.toMutableList().union(match.probes).toList())
//                    )
//                    if (scanners.isEmpty()) {
//                        scanners.add(a)
//                    }
//                    scanners.add(match)
//                    println("merge $i & $j")
//                    continue@loop
//                }
//            }
//        }
//    }
//
//    return input.first() to scanners
//}

fun parse(input: List<String>): List<Scanner> = buildList {
    val lines = input.toMutableList()
    while (lines.isNotEmpty()) {
        lines.removeFirst() // drop header
        val probes = buildList {
            do {
                val line = lines.removeFirst()
                if (line.isEmpty()) break
                val (a, b, c) = line.split(',').map { it.toInt() }
                add(Probe(a, b, c))
            } while (lines.isNotEmpty())
        }
        add(Scanner(probes))
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val scanners = sync(parse(input))
        return scanners.flatMap { it.probes }.toSet().size
    }

    fun part2(input: List<String>): Int {
        val scanners = sync(parse(input))
        var maxDistance = 0
        for (i in scanners.indices) {
            for (j in scanners.indices) {
                if (i == j) continue
                val a = scanners[i].shift
                val b = scanners[j].shift
                val res = a.distanceTo(b)
                maxDistance = maxOf(
                    maxDistance,
                    res,
                )
            }
        }
        return maxDistance
    }

    val testInput2 = readInput("Day19_test")
    check(part1(testInput2) == 79)
    check(part2(testInput2) == 3621)

    val input = readInput("Day19")
    println(part1(input))
    println(part2(input))
}
