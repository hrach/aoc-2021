package day16

import readInput

fun toBin(i: String): String = i.toCharArray().joinToString("") {
    it.digitToInt(radix = 16).toString(radix = 2).padStart(4, '0')
}

data class Packet(
    val version: Int,
    val id: Int,
    val literal: Long?,
    val subPackets: List<Packet>,
)

@Suppress("NAME_SHADOWING")
fun decodePacket(s: String): Pair<Packet, Int> {
    var i = 0
    val version = s.substring(0..2).toInt(radix = 2)
    val id = s.substring(3..5).toInt(radix = 2)

    i += 6

    return when (id) {
        4 -> {
            var bin = ""
            var lastParsed: Boolean
            do {
                lastParsed = s[i] == '0'
                bin += s.substring(i + 1..i + 4)
                i += 5
            } while (!lastParsed)
            val n = bin.toLong(radix = 2)
            val packet = Packet(
                version = version,
                id = id,
                literal = n,
                subPackets = emptyList(),
            )
            packet to i
        }
        else -> {
            val lengthTypeId = s[i].digitToInt()
            i += 1
            val packet: Packet = when (lengthTypeId) {
                0 -> {
                    val dataLength = s.substring(i + 0..i + 14).toInt(radix = 2)
                    i += 15

                    val packets = mutableListOf<Packet>()
                    var readTotal = 0
                    while (readTotal < dataLength) {
                        val (packet, read) = decodePacket(s.substring(i))
                        readTotal += read
                        i += read
                        packets.add(packet)
                    }

                    Packet(
                        version = version,
                        id = id,
                        literal = null,
                        subPackets = packets,
                    )
                }
                1 -> {
                    val subPacketsCount = s.substring(i + 0..i + 10).toInt(radix = 2)
                    i += 11

                    Packet(
                        version = version,
                        id = id,
                        literal = null,
                        subPackets = buildList {
                            repeat(subPacketsCount) {
                                val (subPacket, read) = decodePacket(s.substring(i))
                                add(subPacket)
                                i += read
                            }
                        },
                    )
                }
                else -> error(lengthTypeId)
            }

            packet to i
        }
    }

}

fun Packet.versionSum(): Int = this.subPackets.sumOf { it.versionSum() } + version
fun Packet.calc(): Long = when (id) {
    0 -> subPackets.sumOf { it.calc() }
    1 -> subPackets.fold(1) { acc, packet -> acc * packet.calc() }
    2 -> subPackets.minOf { it.calc() }
    3 -> subPackets.maxOf { it.calc() }
    4 -> literal!!
    5 -> when (subPackets.first().calc() > subPackets.last().calc()) {
        true -> 1
        false -> 0
    }
    6 -> when (subPackets.first().calc() < subPackets.last().calc()) {
        true -> 1
        false -> 0
    }
    7 -> when (subPackets.first().calc() == subPackets.last().calc()) {
        true -> 1
        false -> 0
    }
    else -> error(id)
}

fun main() {
    fun part1(input: String): Int {
        val (packet) = decodePacket(toBin(input))
        return packet.versionSum()
    }

    fun part2(input: String): Long {
        val (packet) = decodePacket(toBin(input))
        return packet.calc()
    }

    check(part1("8A004A801A8002F478") == 16)
    check(part1("620080001611562C8802118E34") == 12)
    check(part1("C0015000016115A2E0802F182340") == 23)
    check(part1("A0016C880162017C3686B18A3D4780") == 31)

    check(part2("C200B40A82") == 3L)
    check(part2("04005AC33890") == 54L)
    check(part2("CE00C43D881120") == 9L)
    check(part2("D8005AC2A8F0") == 1L)
    check(part2("F600BC2D8F") == 0L)
    check(part2("9C005AC2F8F0") == 0L)
    check(part2("9C0141080250320F1802104A08") == 1L)

    val input = readInput("Day16")
    println(part1(input.first()))
    println(part2(input.first()))
}
